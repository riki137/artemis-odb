package com.artemis.compile.poet;

import com.artemis.compile.EntityData;
import com.artemis.compile.Node;
import com.artemis.compile.SymbolTable;
import com.artemis.predicate.Predicate;
import com.squareup.javapoet.CodeBlock;

import java.util.ArrayList;
import java.util.List;

import static com.artemis.compile.SymbolTable.isBuiltinType;
import static com.artemis.predicate.FilterIterator.filter;
import static com.artemis.predicate.Predicates.not;
import static java.lang.Character.toLowerCase;

public class NodePoet  {
	final SymbolTable symbols;

	private List<String> variableNames = new ArrayList<>();

	private static final Predicate<Node> isPrimitiveNode = new Predicate<Node>() {
		@Override
		public boolean apply(Node node) {
			return isBuiltinType(node.meta.type);
		}
	};

	private static final Predicate<Node> isTrivial = new Predicate<Node>() {
		@Override
		public boolean apply(Node node) {
			if (isBuiltinType(node.meta.type))
				return true;

			if (node.children().isEmpty())
				return false;

			for (Node noSerializer : filter(node.children(), not(isTrivial))) {
				System.out.println("no serializer");
				return false;
			}

			return true;
		}
	};

	public NodePoet(SymbolTable symbols) {
		this.symbols = symbols;
	}

	public CodeBlock generate(Node node, EntityData.Entry entity) {
		CodeBlock.Builder builder = CodeBlock.builder();
		write(node, null, entity, builder);
		return builder.build();
	}

	private void write(Node node,
	                   String ownerVariable,
	                   EntityData.Entry entity,
	                   CodeBlock.Builder out) {

		if (isBuiltinType(node.meta.type)) {
			String name = allocateVariable(node, entity);
			out.addStatement("$T $L", node.meta.type, name);
		} else if (isTrivial.apply(node)) {
			for (Node child : node.children()) {
				write(child, ownerVariable, entity, out);
			}
		}
//		CodeBlock.builder()
//			.addStatement("e$")
	}

	private String allocateVariable(Node node, EntityData.Entry entry) {
		String name = node.meta.type.getSimpleName();
		name = toLowerCase(name.charAt(0)) + name.substring(1);
		name += entry.entityId;
		variableNames.add(name);

		return name;
	}
}
