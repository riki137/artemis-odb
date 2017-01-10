package com.artemis.compile;

import com.artemis.component.ArrayComponent;
import com.artemis.component.PositionXy;
import com.artemis.component.PrimitiveComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonWriter;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.lang.model.element.Modifier;

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
	public void marshall_simple() throws Exception {
		SymbolTable symbols = new SymbolTable();
		NodeFactory nodeFactory = new NodeFactory(symbols);

		assertEquals(PrimitiveComponent.expected,
			nodeFactory.create(new PrimitiveComponent()));
	}

	@Test
	public void marshall_simpleish() throws Exception {
		SymbolTable symbols = new SymbolTable();
		NodeFactory nodeFactory = new NodeFactory(symbols);

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



		Node n = toNodeViaJson(new Vector2(12, 3.2f));

		Node x = n.children().get(0);
		MethodSpec methodSpec = MethodSpec.methodBuilder(name(n) + "_" + x.meta.field)
			.addModifiers(Modifier.STATIC, Modifier.PUBLIC)
			.addParameter(n.meta.type, "owner")
			.addParameter(x.meta.type, "value")
			.addCode("$N.$N = $L", "owner", x.meta.field, x.payload)
			.build();
		FieldSpec.builder(Void.class, name(n) + "_" + n.meta.field);


	}

	@Test
	public void test_generate_setter() {
		Vector2 objectToTest = new Vector2(0xdead, 0xc0de);

		SymbolTable symbols = new SymbolTable();
		symbols.register(objectToTest.getClass());
		NodeFactory factory = new NodeFactory(symbols);

		Node n = factory.create(objectToTest);

		MutationGraph mutationGraph = new MutationGraph(symbols);
		mutationGraph.add(null, n);

		for (SymbolTable.Entry entry : mutationGraph.getRegistered()) {
//			MethodSpec methodSpec = FieldWriters.generate(entry);

//			System.out.println(methodSpec);
			throw new RuntimeException();
		}
	}

	private static String name(Node node) {
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
		SymbolTable symbols = new SymbolTable();
		symbols.register(object.getClass());
		NodeFactory factory = new NodeFactory(symbols);

		Node expected = toNodeViaJson(object);
		assertEquals(expected, factory.create(object));
	}

	private Node toNodeViaJson(Object object) {
		String json = this.json.prettyPrint(object);
		SymbolTable symbols = new SymbolTable();
		symbols.register(object.getClass());
		NodeFactory nodeFactory = new NodeFactory(symbols);

		Node node = nodeFactory.create(object.getClass(), new JsonReader().parse(json));
		for (Node n : node.children()) {
			if (SymbolTable.isBuiltinType(n.meta.type)) {
				assertEquals(n.toString(),
					widen(n.meta.type),
					widen(unbox(n.payload.getClass())));
			} else {
				assertNull(n.toString(), n.payload);
			}
		}
		return node;
	}

	private void assertNodeEquals(Class<?> object, Node expected) throws Exception {
		assertNodeEquals(object.newInstance(), expected);
	}

	private void assertNodeEquals(Object object, Node expected) throws Exception {
		Node node = toNodeViaJson(object);
		assertEquals(node.toString(), expected, node);
	}

	static Class<?> widen(Class<?> t) {
		if (t == short.class || t == char.class || t == byte.class)
			return int.class;

		return t;
	}

}