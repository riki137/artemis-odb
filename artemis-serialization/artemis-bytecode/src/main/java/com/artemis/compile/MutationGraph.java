package com.artemis.compile;

import java.util.*;


/**
 * Tracks written fields of objects from a {@link Node}. This class
 * serves as a basis for resolving the set of necessary {@code setters}.
 */
public class MutationGraph {
	private Map<Class<?>, Set<SymbolTable.Entry>> graph = new HashMap<>();

	private final SymbolTable symbols;

	public MutationGraph(SymbolTable symbols) {
		this.symbols = symbols;
	}

	public void add(Class<?> owner, Node node) {
		if (owner != null)
			typeNode(owner).add(symbols.lookup(owner, node.meta.field));

		if (!SymbolTable.isBuiltinType(node.meta.type)) {
			for (Node child : node.children()) {
				add(node.meta.type, child);
			}
		}
	}

	public List<SymbolTable.Entry> getRegistered() {
		List<SymbolTable.Entry> entries = new ArrayList<>();
		for (Map.Entry<Class<?>, Set<SymbolTable.Entry>> entry : graph.entrySet()) {
			entries.addAll(entry.getValue());
		}

		Collections.sort(entries, new Comparator<SymbolTable.Entry>() {
			@Override
			public int compare(SymbolTable.Entry o1, SymbolTable.Entry o2) {
				int result = o1.owner.getName().compareTo(o2.owner.getName());
				if (result == 0)
					result = o1.field.compareTo(o2.field);

				return result;
			}
		});

		return entries;
	}

	private Set<SymbolTable.Entry> typeNode(Class<?> type) {
		Set<SymbolTable.Entry> nodes = graph.get(type);
		if (nodes == null) {
			nodes = new HashSet<>();
			graph.put(type, nodes);
		}

		return nodes;
	}
}
