package com.artemis.compile

import com.artemis.component.CompC
import com.artemis.component.PrimitiveComponent
import org.junit.Test

class SymbolTest {
	@Test
	fun fieldsOfTest() {
		val expected = setOf(Symbol(CompC::class.java, "a", Int::class.java),
		                     Symbol(CompC::class.java, "b", Float::class.java),
		                     Symbol(CompC::class.java, "c", Byte::class.java))

		symbolsOf(CompC::class) assertEqualsAsSets expected
	}

	@Test
	fun mutation_tracking_test() {
		Node(PrimitiveComponent::class,
		     Node(String::class, "text", "zero"),
		     Node(Int::class, "aInt", 2)).let(::print)
	}
}