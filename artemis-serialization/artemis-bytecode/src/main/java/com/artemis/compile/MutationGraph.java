package com.artemis.compile;

import java.util.*;


/**
 * Tracks written fields of objects from a {@link NodeOld}. This class
 * serves as a basis for resolving the set of necessary {@code setters}.
 */
public class MutationGraph {
	private Map<Class<?>, Set<SymbolTableOld.Entry>> graph = new HashMap<>();

	private final SymbolTableOld symbols;

	public MutationGraph(SymbolTableOld symbols) {
		this.symbols = symbols;
	}

	public void add(Class<?> owner, NodeOld node) {
		if (owner != null)
			typeNode(owner).add(symbols.lookup(owner, node.meta.field));

		if (!SymbolTableOld.isBuiltinType(node.meta.type)) {
			for (NodeOld child : node.children()) {
				add(node.meta.type, child);
			}
		}
	}

	public List<SymbolTableOld.Entry> getRegistered() {
		List<SymbolTableOld.Entry> entries = new ArrayList<>();
		for (Map.Entry<Class<?>, Set<SymbolTableOld.Entry>> entry : graph.entrySet()) {
			entries.addAll(entry.getValue());
		}

		Collections.sort(entries, new Comparator<SymbolTableOld.Entry>() {
			@Override
			public int compare(SymbolTableOld.Entry o1, SymbolTableOld.Entry o2) {
				int result = o1.owner.getName().compareTo(o2.owner.getName());
				if (result == 0)
					result = o1.field.compareTo(o2.field);

				return result;
			}
		});

		return entries;
	}

	private Set<SymbolTableOld.Entry> typeNode(Class<?> type) {
		Set<SymbolTableOld.Entry> nodes = graph.get(type);
		if (nodes == null) {
			nodes = new HashSet<>();
			graph.put(type, nodes);
		}

		return nodes;
	}
}
