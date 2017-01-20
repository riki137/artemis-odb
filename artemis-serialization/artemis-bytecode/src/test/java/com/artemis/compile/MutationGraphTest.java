package com.artemis.compile;

import com.artemis.component.PrimitiveComponent;
import org.junit.Test;

import java.util.List;

import static com.artemis.compile.NodeOld.node;
import static org.junit.Assert.*;

public class MutationGraphTest {

	@Test
	public void test_simple_component() {
		SymbolTableOld symbols = new SymbolTableOld();
		symbols.register(PrimitiveComponent.class);

		MutationGraph graph = new MutationGraph(symbols);

		graph.add(null,
			node(PrimitiveComponent.class,
				node(String.class,  "text",    "zero"),
				node(int.class,     "aInt",    2)));

		List<SymbolTableOld.Entry> registered = graph.getRegistered();
		assertEquals(2, registered.size());

		graph.add(null, PrimitiveComponent.expected);
		assertEquals(9, graph.getRegistered().size());
	}

}