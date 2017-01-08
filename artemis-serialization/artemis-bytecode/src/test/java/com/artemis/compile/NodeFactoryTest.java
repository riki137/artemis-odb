package com.artemis.compile;

import com.artemis.component.ArrayComponent;
import com.artemis.component.PositionXy;
import com.artemis.component.PrimitiveComponent;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.artemis.compile.SymbolTable.unbox;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class NodeFactoryTest {
	private Json json;

	@Before
	public void init() {
		json = new Json(JsonWriter.OutputType.json);
		json.setUsePrototypes(false);
	}

	@Test
	public void read_simple_component() throws Exception {
		testComponent(PrimitiveComponent.class, PrimitiveComponent.expected);
	}

	@Test
	public void read_simpleish_component() throws Exception {
		testComponent(PositionXy.class, PositionXy.expected);
	}

	@Test @Ignore
	public void read_array_component() throws Exception {
		testComponent(ArrayComponent.class, ArrayComponent.expected);
	}

	void testComponent(Class<?> type, Node expected) throws Exception {
		String s = json.prettyPrint(type.newInstance());

		SymbolTable symbols = new SymbolTable();
		symbols.register(type);
		NodeFactory nodeFactory = new NodeFactory(symbols);

		Node node = nodeFactory.create(type, new JsonReader().parse(s));
		for (Node n : node.children()) {
			if (SymbolTable.isBuiltinType(n.type)) {
				assertEquals(n.toString(),
					widen(n.type),
					widen(unbox(n.payload.getClass())));
			} else {
				assertNull(n.toString(), n.payload);
			}
		}

		assertEquals(node.toString(), expected, node);
	}

	static Class<?> widen(Class<?> t) {
		if (t == short.class || t == char.class || t == byte.class)
			return int.class;

		return t;
	}

}