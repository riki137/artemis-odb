package com.artemis.compile.poet;

import com.artemis.compile.EntityData;
import com.artemis.compile.Node;
import com.artemis.compile.SymbolTable;
import com.artemis.predicate.Predicate;
import com.squareup.javapoet.ClassName;
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
				System.out.println("missing serializer for: " + noSerializer);
				return false;
			}

			return true;
		}
	};

	public NodePoet(SymbolTable symbols) {
		this.symbols = symbols;
	}

	public CodeBlock generate(Node node, SymbolTable.Entry symbol) {
		CodeBlock.Builder builder = CodeBlock.builder();
		write(node, symbol, "c", builder);
		return builder.build();
	}

	private void write(Node node,
	                   SymbolTable.Entry symbol,
	                   String ownerVariable,
	                   CodeBlock.Builder out) {

		ClassName util = ClassName.get("com.artemis", "GlobalUtil");
		if (isBuiltinType(node.meta.type)) {
//			SymbolTable.Entry symbol = symbols.lookup(node);
//			String name = allocateVariable(node, ownerVariable);
//			System.out.println(name);
			String methodName = symbol.owner.getSimpleName() + "_" + symbol.field;
			out.addStatement("$L($N, $S)", methodName, ownerVariable, node.payload);
		} else if (isTrivial.apply(node)) {
			for (Node child : node.children()) {
				SymbolTable.Entry nextSymbol = symbols.lookup(node.meta.type, child.meta.field);
				write(child, nextSymbol, nextSymbol.field, out);
			}
		}
//		CodeBlock.builder()
//			.addStatement("e$")
	}

	private String allocateVariable(Node node) {
		String name = node.meta.type.getSimpleName();
		name = toLowerCase(name.charAt(0)) + name.substring(1);
		name += node.hashCode();
		variableNames.add(name);

		return name;
	}
}
