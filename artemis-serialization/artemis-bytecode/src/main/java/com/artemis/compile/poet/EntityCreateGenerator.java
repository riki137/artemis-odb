package com.artemis.compile.poet;

import com.artemis.World;
import com.artemis.compile.EntityData;
import com.artemis.compile.Node;
import com.artemis.compile.SymbolTable;
import com.artemis.io.EntityPoolFactory;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.List;

public class EntityCreateGenerator implements SourceGenerator {
	private final NodePoet nodePoet;
	private final List<EntityData.Entry> entities;
	private final SymbolTable symbols;

	public EntityCreateGenerator(List<EntityData.Entry> entities,
	                             NodePoet nodePoet,
	                             SymbolTable symbols) {

		this.nodePoet = nodePoet;
		this.entities = entities;
		this.symbols = symbols;
	}

	protected CodeBlock generate(List<EntityData.Entry> entries) {
		CodeBlock.Builder code = CodeBlock.builder();
		for (EntityData.Entry entity : entries) {
//			code.add(generate(entity));
			CreateEntity creator = new CreateEntity();
			code.add(creator.generate(entity));
			for (Node component : entity.components) {
				code.add("{\n").indent();
				code.addStatement("$T c = $L.get(e$L)",
					component.meta.type,
					SymbolUtil.mapperName(component.meta.type),
					entity.entityId);
				SymbolTable.Entry symbol = symbols.lookup(component);
				code.add(nodePoet.generate(component, symbol));
				code.unindent().add("}\n");
			}
			code.add("\n");
		}

		return code.build();
	}

	public void generate(TypeSpec.Builder builder) {
//		CodeBlock generated = generate(entities);
		MethodSpec.Builder method = MethodSpec.methodBuilder("compiledCreate")
			.addModifiers(Modifier.PUBLIC)
			.addParameter(World.class, "world")
			.addStatement("$T factory = entityFactory(world, $L)",
				EntityPoolFactory.class, entities.size())
			.addCode("\n");

		method.addCode(generate(entities));

		builder.addMethod(method.build());
	}

	private static class CreateEntity implements BodyFactory<EntityData.Entry> {
		@Override
		public CodeBlock generate(EntityData.Entry entry) {
			int id = entry.entityId;

			return CodeBlock.builder()
				.add("// creating entity $L\n", id)
				.addStatement("int e$L = factory.createEntityId()", id)
				.addStatement("transmuter$L.transmute(e$L)", entry.archetype, id)
				.build();
		}
	}

	public interface BodyFactory<T> {
		CodeBlock generate(T entry);
	}
}
