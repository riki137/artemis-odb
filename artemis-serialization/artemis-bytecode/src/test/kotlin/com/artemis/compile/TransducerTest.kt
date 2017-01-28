package com.artemis.compile
import net.onedaybeard.transducers.map
import net.onedaybeard.transducers.transduce
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

	@Test
	fun test_subduce() {
		val pairing = makePair(left = map { i: Int -> i * 3},
		                       right = map { i: Int -> i * 2 })

		intoList(xf = pairing,
		         input = (0..9)).let(::println)

//		intoList(xf = subduce(map(Int::toString)),
//		          init = mutableListOf<String>(),
//		          input = (0..9)
//		) assertEquals mapOf(0 to listOf(0, 2, 4, 6, 8),
//		                     1 to listOf(1, 3, 5, 7, 9))

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

	@Test
	fun test_make_pair() {
//		makePair(left = map { i: Int -> i },
//		         right = { i: Int -> i * 2 })
//		transduce(xf = groupBy { it and 1 },
//		          rf = { result, input: Map<Int, Iterable<Int>> -> result },
//		          init = mutableMapOf<Int, Iterable<Int>>(),
//		          input = (0..9)
//		) assertEquals mapOf(0 to listOf(0, 2, 4, 6, 8),
//		                     1 to listOf(1, 3, 5, 7, 9))
//
//		intoMap(xf = groupBy { input: Int -> input and 0x1 },
//		        keyType = Key<Int>(),
//		        input = (0..9)
//		) assertEquals mapOf(0 to listOf(0, 2, 4, 6, 8),
//		                     1 to listOf(1, 3, 5, 7, 9))
//
//		intoMap(xf = groupBy { input: Int -> input and 0x1 },
//		        keyType = Key<Int>(),
//		        input = listOf<Int>()
//		) assertEquals mapOf()
	}
}
