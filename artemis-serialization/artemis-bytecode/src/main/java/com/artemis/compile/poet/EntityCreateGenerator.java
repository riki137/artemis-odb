package com.artemis.compile.poet;

import com.artemis.World;
import com.artemis.compile.EntityData;
import com.artemis.compile.Node;
import com.artemis.io.EntityPoolFactory;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class EntityCreateGenerator implements SourceGenerator {
	private final List<BodyFactory> factories = new ArrayList<>();
	private NodePoet nodePoet;
	private List<EntityData.Entry> entities;

	public EntityCreateGenerator(List<EntityData.Entry> entities, NodePoet nodePoet) {
		this.nodePoet = nodePoet;
		this.entities = entities;
//		factories.add();
//		factories.add(new SetterBody());
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
				code.add(nodePoet.generate(component, entity));
				code.unindent().add("}\n");
			}
			code.add("\n");
		}

		return code.build();
	}

	protected CodeBlock generate(EntityData.Entry entry) {
		for (BodyFactory factory : factories) {
				return factory.generate(entry);
		}

		throw new RuntimeException("Failed generation: " + entry);
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

//	private static class CreateComponent implements BodyFactory<Node> {
//
//		@Override
//		public CodeBlock generate(Node component) {
////			int id = entry.entityId;
//
//			return CodeBlock.builder()
//				.add("// creating entity $L\n", id)
//				.addStatement("int e$L = factory.createEntityId()", id)
//				.addStatement("transmuter$L.transmute(e$L)", entry.archetype, id)
//				.add("\n")
//				.build();
//		}
//	}

	public interface BodyFactory<T> {
		CodeBlock generate(T entry);
	}
}
