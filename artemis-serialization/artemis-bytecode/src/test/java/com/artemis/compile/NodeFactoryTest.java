package com.artemis.compile;

import com.artemis.component.ArrayComponent;
import com.artemis.component.ComponentX;
import com.artemis.component.PositionXy;
import com.artemis.component.PrimitiveComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.artemis.compile.SymbolTableOld.unbox;
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
	public void marshall_simple() throws Exception {
		SymbolTableOld symbols = new SymbolTableOld();
		NodeFactoryOld nodeFactory = new NodeFactoryOld(symbols);

		assertEquals(PrimitiveComponent.expected,
			nodeFactory.create(new PrimitiveComponent()));
	}

	@Test
	public void marshall_simpleish() throws Exception {
		SymbolTableOld symbols = new SymbolTableOld();
		NodeFactoryOld nodeFactory = new NodeFactoryOld(symbols);

		assertEquals(PositionXy.expected,
			nodeFactory.create(new PositionXy()));
	}

	@Test
	public void read_simple_component() throws Exception {
		assertNodeEquals(PrimitiveComponent.class, PrimitiveComponent.expected);
	}

	@Test
	public void read_simpleish_component() throws Exception {
		assertNodeEquals(PositionXy.class, PositionXy.expected);
	}

	@Test @Ignore
	public void read_array_component() throws Exception {
		assertNodeEquals(ArrayComponent.class, ArrayComponent.expected);
	}

	@Test
	public void test_nodes_Vector2() throws Exception {
		assertNodeSymmetry(new Vector2(2, 4.2f));
	}

	@Test
	public void test_nodes_ComponentX() throws Exception {
		ComponentX c = new ComponentX();
		c.text = "yes?";
		assertNodeSymmetry(c);
	}

	@Test
	public void test_generate_setter() {
		Vector2 objectToTest = new Vector2(0xdead, 0xc0de);

		SymbolTableOld symbols = new SymbolTableOld();
		symbols.register(objectToTest.getClass());
		NodeFactoryOld factory = new NodeFactoryOld(symbols);

		NodeOld n = factory.create(objectToTest);

		MutationGraph mutationGraph = new MutationGraph(symbols);
		mutationGraph.add(null, n);

		for (SymbolTableOld.Entry entry : mutationGraph.getRegistered()) {
//			MethodSpec methodSpec = FieldWriters.generate(entry);

//			System.out.println(methodSpec);
			throw new RuntimeException();
		}
	}

	private static String name(NodeOld node) {
		return node.meta.type.getSimpleName();
	}

	@Test
	public void test_nodes_PrimitiveComponent() throws Exception {
		assertNodeSymmetry(new PrimitiveComponent());
	}

	@Test
	public void test_nodes_PositionXy() throws Exception {
		assertNodeSymmetry(new PositionXy());
	}

	private void assertNodeSymmetry(Object object) throws Exception {
		SymbolTableOld symbols = new SymbolTableOld();
		symbols.register(object.getClass());
		NodeFactoryOld factory = new NodeFactoryOld(symbols);

		NodeOld expected = toNodeViaJson(object);
		assertEquals(expected, factory.create(object));
	}

	private NodeOld toNodeViaJson(Object object) {
		String json = this.json.prettyPrint(object);
		SymbolTableOld symbols = new SymbolTableOld();
		symbols.register(object.getClass());
		NodeFactoryOld nodeFactory = new NodeFactoryOld(symbols);

		NodeOld node = nodeFactory.create(object.getClass(), new JsonReader().parse(json));
		for (NodeOld n : node.children()) {
			if (SymbolTableOld.isBuiltinType(n.meta.type)) {
				assertEquals(n.toString(),
					widen(n.meta.type),
					widen(unbox(n.payload.getClass())));
			} else {
				assertNull(n.toString(), n.payload);
			}
		}
		return node;
	}

	private void assertNodeEquals(Class<?> object, NodeOld expected) throws Exception {
		assertNodeEquals(object.newInstance(), expected);
	}

	private void assertNodeEquals(Object object, NodeOld expected) throws Exception {
		NodeOld node = toNodeViaJson(object);
		assertEquals(node.toString(), expected, node);
	}

	public static Class<?> widen(Class<?> t) {
		if (t == short.class || t == char.class || t == byte.class)
			return int.class;

		return t;
	}

}