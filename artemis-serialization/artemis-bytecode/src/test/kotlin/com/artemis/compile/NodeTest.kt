package com.artemis.compile

import com.artemis.component.ComponentX
import com.artemis.component.PositionXy
import com.artemis.component.PrimitiveComponent
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonWriter
import org.junit.Before
import org.junit.Test

class NodeTest {
	private val json: Json = Json(JsonWriter.OutputType.json)

	@Before
	fun init() {
		json!!.setUsePrototypes(false)
	}

	@Test
	@Throws(Exception::class)
	fun marshall_simple() {
		val expected =
			Node(PrimitiveComponent::class,
			     Node(String::class, "text", "zero"),
			     Node(Long::class, "aLong", 1L),
			     Node(Int::class, "aInt", 2),
			     Node(Short::class, "aShort", 3.toShort()),
			     Node(Char::class, "aChar", '4'),
			     Node(Boolean::class, "aBool", true),
			     Node(Byte::class, "aByte", 6.toByte()),
			     Node(Float::class, "aFloat", 7f),
			     Node(Double::class, "aDouble", 8.0))

		toNode(PrimitiveComponent()) assertEquals expected
	}

	@Test
	fun marshall_simpleish() {
		val expected =
			Node(PositionXy::class,
		     Node(Vector2::class, "xy",
		         Node(Float::class, "x", 420f),
		         Node(Float::class, "y", -12.345f)))

		toNode(PositionXy()) assertEquals expected
	}

	@Test
	fun test_nodes_PrimitiveComponent() {
		assertNodeSymmetry(PrimitiveComponent())
	}

	@Test
	fun test_nodes_PositionXy() {
		assertNodeSymmetry(PositionXy())
	}

	@Test
	fun test_nodes_Vector2() {
		assertNodeSymmetry(Vector2(2f, 4.2f))
	}

	@Test
	fun test_nodes_ComponentX() {
		val c = ComponentX()
		c.text = "yes?"
		assertNodeSymmetry(c)
	}

	fun assertNodeSymmetry(obj: Any) {
		toNodeViaJson(obj) assertEquals toNode(obj)
	}

	private fun toNodeViaJson(obj: Any): Node {
		return toNode(obj.javaClass, JsonReader().parse(json.prettyPrint(obj)))
	}
}
