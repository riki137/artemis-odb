package com.artemis.compile;

infix fun <T> T.assertEquals(expected: T) =
	kotlin.test.assertEquals(expected, this)

infix fun <T: Iterable<*>> T.assertEqualsAsSets(expected: T) =
	kotlin.test.assertEquals(expected.toSet(), this.toSet())
