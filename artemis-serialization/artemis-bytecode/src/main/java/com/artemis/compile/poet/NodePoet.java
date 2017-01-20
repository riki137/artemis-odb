package com.artemis.compile.poet;

import com.artemis.compile.NodeOld;
import com.artemis.compile.SymbolTableOld;
import com.artemis.predicate.Predicate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;

import java.util.ArrayList;
import java.util.List;

import static com.artemis.compile.SymbolTableOld.isBuiltinType;
import static com.artemis.predicate.FilterIterator.filter;
import static com.artemis.predicate.Predicates.not;
import static java.lang.Character.toLowerCase;

public class NodePoet  {
	final SymbolTableOld symbols;

	private List<String> variableNames = new ArrayList<>();

	private static final Predicate<NodeOld> isPrimitiveNode = new Predicate<NodeOld>() {
		@Override
		public boolean apply(NodeOld node) {
			return isBuiltinType(node.meta.type);
		}
	};

	private static final Predicate<NodeOld> isTrivial = new Predicate<NodeOld>() {
		@Override
		public boolean apply(NodeOld node) {
			if (isBuiltinType(node.meta.type))
				return true;

			if (node.children().isEmpty())
				return false;

			for (NodeOld noSerializer : filter(node.children(), not(isTrivial))) {
				System.out.println("missing serializer for: " + noSerializer);
				return false;
			}

			return true;
		}
	};

	public NodePoet(SymbolTableOld symbols) {
		this.symbols = symbols;
	}

	public CodeBlock generate(NodeOld node, SymbolTableOld.Entry symbol) {
		CodeBlock.Builder builder = CodeBlock.builder();
		write(node, symbol, "c", builder);
		return builder.build();
	}

	private void write(NodeOld node,
	                   SymbolTableOld.Entry symbol,
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
			for (NodeOld child : node.children()) {
				SymbolTableOld.Entry nextSymbol = symbols.lookup(node.meta.type, child.meta.field);
				write(child, nextSymbol, nextSymbol.field, out);
			}
		}
//		CodeBlock.builder()
//			.addStatement("e$")
	}

	private String allocateVariable(NodeOld node) {
		String name = node.meta.type.getSimpleName();
		name = toLowerCase(name.charAt(0)) + name.substring(1);
		name += node.hashCode();
		variableNames.add(name);

		return name;
	}
}
