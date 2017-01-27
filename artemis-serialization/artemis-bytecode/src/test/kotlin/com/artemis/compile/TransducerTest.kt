package com.artemis.compile
import net.onedaybeard.transducers.*
import org.junit.Test

class TransducerTest {
	@Test
	fun test_group_by() {
		transduce(xf = groupBy { it and 1 },
		          rf = { result, input: Map<Int, Iterable<Int>> -> result },
		          init = mutableMapOf<Int, Iterable<Int>>(),
		          input = (0..9)
		) assertEquals mapOf(0 to listOf(0, 2, 4, 6, 8),
		                     1 to listOf(1, 3, 5, 7, 9))

		intoMap(xf = groupBy { input: Int -> input and 0x1 },
		        keyType = Key<Int>(),
		        input = (0..9)
		) assertEquals mapOf(0 to listOf(0, 2, 4, 6, 8),
		                     1 to listOf(1, 3, 5, 7, 9))

		intoMap(xf = groupBy { input: Int -> input and 0x1 },
		        keyType = Key<Int>(),
		        input = listOf<Int>()
		) assertEquals mapOf()
	}
}
