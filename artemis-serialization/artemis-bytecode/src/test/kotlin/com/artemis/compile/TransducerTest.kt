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
        intoList(xf = makePair(map { i: Int -> i % 2 },
                               map { i: Int -> i * 2 }),
                 input = (0..5)
        ) assertEquals listOf(0 to 0,
                              1 to 2,
                              0 to 4,
                              1 to 6,
                              0 to 8,
                              1 to 10)

        val pairing = makePair(left = map { i: Int -> i % 2 } + map(Int::toString),
                               right = map { i: Int -> i * 2 })
        intoMap(xf = pairing + groupBy<String, Int>(),
                keyType = Key<Any>(), // <- heh, type-erasure
                input = (0..9)
        ) assertEquals mapOf("0" to listOf(0, 4, 8, 12, 16),
                             "1" to listOf(2, 6, 10, 14, 18))
    }

    @Test
    fun test_subduce_cat() {
        intoList(xf = makePair(map { i: Int -> i % 2 },
                               map { i: Int -> i * 2 }),
                 input = (0..5)
        ) assertEquals listOf(0 to 0,
                              1 to 2,
                              0 to 4,
                              1 to 6,
                              0 to 8,
                              1 to 10)

        val pairing = makePair(left = map { i: Int -> i % 2 } + map(Int::toString),
                               right = map { i: Int -> i * 2 })
        intoMap(xf = pairing + groupBy<String, Int>(),
                keyType = Key<Any>(), // <- heh, type-erasure
                input = (0..9)
        ) assertEquals mapOf("0" to listOf(0, 4, 8, 12, 16),
                             "1" to listOf(2, 6, 10, 14, 18))

    }
}
