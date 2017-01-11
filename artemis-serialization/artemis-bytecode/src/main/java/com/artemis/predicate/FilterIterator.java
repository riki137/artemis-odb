package com.artemis.predicate;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FilterIterator<T> implements Iterator<T> {
	private final Iterator<T> src;
	private final Predicate filter;

	private boolean isNextChecked = false;
	private T next;

	private FilterIterator(Iterator<T> src, Predicate filter) {
		this.src = src;
		this.filter = filter;
	}

	public static <T> Iterable<T> filter(final T[] src,
	                                     final Predicate predicate) {

		return filter(Arrays.asList(src), predicate);
	}

	public static <T> Iterable<T> filter(final Collection<T> src,
	                                     final Predicate predicate) {

		return new Iterable<T>() {
			@Override
			public Iterator<T> iterator() {
				return new FilterIterator<>(src.iterator(), predicate);
			}
		};
	}

	@Override
	public boolean hasNext() {
		if (!isNextChecked) {
			isNextChecked = true;
			while (src.hasNext()) {
				T t = src.next();
				if (filter.apply(t)) {
					next = t;
					break;
				}
			}
		}

		return next != null;
	}

	@Override
	public T next() {
		isNextChecked = false;

		T t = next;
		next = null;

		return t;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
