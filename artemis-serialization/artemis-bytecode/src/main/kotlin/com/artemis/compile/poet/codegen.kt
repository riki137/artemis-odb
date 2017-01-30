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
import net.onedaybeard.transducers.map
import net.onedaybeard.transducers.transduce
import javax.lang.model.element.Modifier

private fun mapperName(type: Class<*>) : String {
    val name = type.simpleName
    return "${name[0].toLowerCase()}${name.substring(1)}Mapper"
}

private inline fun <reified T : Any> className() : ClassName {
    return ClassName.get(T::class.java)
}

private fun componentMapper(componentType: Class<*>) : ParameterizedTypeName {
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
            FieldSpec.builder(className<EntityTransmuter>(),
                              "transmuter${archetype.id}",
                              Modifier.PRIVATE)
                    .addAnnotation(annotation(archetype.types))
                    .build()
                    .let { builder.addField(it) }
        }
    }
}

private fun componentMappers(context: Context): (TypeSpec.Builder) -> Unit {
    return { typeBuilder ->
        transduce(xf = nodesToSymbols(context.symbols) +
                       map(Symbol::owner) +
                       distinct() +
                       isAssignableFrom<Component>() +
                       cast<Class<Component>>(),
                  rf = { result, input ->
                      FieldSpec.builder(componentMapper(input),
                                        mapperName(input),
                                        Modifier.PRIVATE)
                              .build()
                              .let { result.addField(it) } },
                  init = typeBuilder,
                  input = context.entities)
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
            for ((type) in e.components) {
                code.add("{\n").indent()
                code.addStatement("\$T c = \$L.get(e\$L)",
                                  type,
                                  mapperName(type),
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
