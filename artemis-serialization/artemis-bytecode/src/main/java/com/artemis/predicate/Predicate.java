package com.artemis.predicate;

public interface Predicate<T> {
	boolean apply(T t);
}
