package com.artemis.predicate;

import com.artemis.compile.SymbolTable;

import java.lang.reflect.Method;

public final class Predicates {
	public static Predicate<Method> findSetterFor(SymbolTable.Entry entry) {
		return new FindSetterFor(entry);
	}
}
