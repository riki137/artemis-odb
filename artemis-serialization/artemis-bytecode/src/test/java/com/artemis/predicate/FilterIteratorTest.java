package com.artemis.predicate;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.artemis.predicate.FilterIterator.filter;
import static com.artemis.predicate.Predicates.match;
import static com.artemis.predicate.Predicates.not;
import static com.artemis.predicate.Predicates.toList;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class FilterIteratorTest {

	@Test
	public void test_filter_and_match_strings() {
		List<String> list = asList("one", "two", "three", "four", "five");

		// the cruft
		List<String> filtered = toList(filter(list, match("three")));
		List<String> negated = toList(filter(list, not(match("three"))));

		ArrayList<String> expectedFiltered = new ArrayList<>();
		expectedFiltered.add("three");
		assertEquals(expectedFiltered, filtered);

		ArrayList<String> expectedNegated = new ArrayList<>(list);
		expectedNegated.remove("three");
		assertEquals(expectedNegated, negated);
	}
}