package com.artemis.compile.poet

import com.artemis.Component
import com.artemis.ComponentMapper
import com.artemis.EntityTransmuter
import com.artemis.World
import com.artemis.annotations.AspectDescriptor
import com.artemis.compile.*
import com.artemis.io.EntityPoolFactory
import com.artemis.prefab.CompiledPrefab
import com.squareup.javapoet.*
import com.squareup.javapoet.MethodSpec.methodBuilder
import net.onedaybeard.transducers.filter
import net.onedaybeard.transducers.map
import javax.lang.model.element.Modifier
import kotlin.comparisons.compareBy

private inline fun mapperName(type: Class<*>) : String {
    val name = type.simpleName
    return "${name[0].toLowerCase()}${name.substring(1)}Mapper"
}

private inline fun <reified T : Any> className() : ClassName {
    return ClassName.get(T::class.java)
}

private inline fun componentMapper(componentType: Class<*>) : ParameterizedTypeName {
    return ParameterizedTypeName.get(ComponentMapper::class.java, componentType)
}

private fun annotation(types: List<Class<out Component>>): AnnotationSpec {

    val format = types.joinTo(buffer = StringBuilder(),
                              prefix = "{",
                              postfix = "}",
                              separator = ", ",
                              transform = { "\$T.class" }).toString()

    return AnnotationSpec.builder(AspectDescriptor::class.java)
            .addMember("all", format, *types.toTypedArray<Class<*>>() as Array<Any>)
            .build()
}

private fun transmuters(archetypes: List<Archetype>): (TypeSpec.Builder) -> Unit {
    return { builder ->
        archetypes.forEach { archetype ->
            val field = FieldSpec.builder(className<EntityTransmuter>(),
                                          "transmuter${archetype.id}",
                                          Modifier.PRIVATE)
                    .addAnnotation(annotation(archetype.types))
                    .build()

            builder.addField(field)
        }
    }
}

private fun componentMappers(context: Context): (TypeSpec.Builder) -> Unit {
    val componentFilter = filter { input: Class<*> -> Component::class.java.isAssignableFrom(input) }
    val mappers = nodesToSymbols(context.symbols) + map(Symbol::owner) + componentFilter

    val referenced = intoSet(xf = mappers + cast<Class<Component>>(),
                             input = context.entities)
            .sortedWith(compareBy { it.simpleName })

    return { builder ->
        referenced.forEach { mapper ->

            val field = FieldSpec.builder(componentMapper(mapper),
                                          mapperName(mapper),
                                          Modifier.PRIVATE)
            builder.addField(field.build())
        }
    }
}

private fun createEntity(e: EntityData): CodeBlock {
    val id = e.entityId
    return CodeBlock.builder()
            .add("// creating entity \$L\n", id)
            .addStatement("int e\$L = factory.createEntityId()", id)
            .addStatement("transmuter\$L.transmute(e\$L)", e.archetype, id)
            .build()
}

private fun createEntities(context: Context): (TypeSpec.Builder) -> Unit {

    return { builder ->
        val code = CodeBlock.builder()
        for (e in context.entities) {
            code.add(createEntity(e))
            for (component in e.components) {
                code.add("{\n").indent()
                code.addStatement("\$T c = \$L.get(e\$L)",
                                  component.type,
                                  mapperName(component.type),
                                  e.entityId)
                code.unindent().add("}\n")
            }
            code.add("\n")
        }

        builder.addMethod(
            methodBuilder("compiledCreate")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(World::class.java, "world")
                .addStatement("\$T factory = entityFactory(world, \$L)",
                              EntityPoolFactory::class.java, context.entities.size)
                .addCode("\n")
                .addCode(code.build()).build())
    }
}

fun generatePrefab(name: String, context: Context) : String {
    val archetypes: List<Archetype> = archetypesOf(context.json, context.componentLookup)
    return generate(
            name,
            { it.superclass(CompiledPrefab::class.java) },
            transmuters(archetypes),
            componentMappers(context),
            createEntities(context))
}


private fun generate(name: String,
                     vararg generators: (builder: TypeSpec.Builder) -> Unit)
        : String {

    val builder: TypeSpec.Builder = TypeSpec.classBuilder(name)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)

    generators.forEach { it(builder) }

    val jf = JavaFile.builder("com.artemis.generated", builder.build())
            .indent("\t")
            .build()

    return jf.toString()
}
