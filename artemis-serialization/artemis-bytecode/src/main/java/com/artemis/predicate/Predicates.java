package com.artemis.predicate;

import com.artemis.compile.SymbolTable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class Predicates {
	public static Predicate<Method> findSetterFor(SymbolTable.Entry entry) {
		return new FindSetterFor(entry);
	}

	public static <T> List<T> toList(Iterable<T> it) {
		return toList(it.iterator());
	}

	public static <T> List<T> toList(Iterator<T> it) {
		List<T> l = new ArrayList<>();
		while (it.hasNext())
			l.add(it.next());

		return l;
	}

	public static <T> Predicate<T> match(T any) {
		return new Match<>(any);
	}

	public static <T> Predicate<T> not(final Predicate<T> predicate) {
		return new Not<>(predicate);
	}

	private static class Not<T> implements Predicate<T> {
		private final Predicate<T> predicate;

		public Not(Predicate<T> predicate) {
			this.predicate = predicate;
		}

		@Override
		public boolean apply(T t) {
			return !predicate.apply(t);
		}
	}

	private static class Match<T> implements Predicate<T> {
		private final T match;

		private Match(T match) {
			this.match = match;
		}

		@Override
		public boolean apply(T t) {
			return match != null ? match.equals(t) : t == null;
		}
	}
}
