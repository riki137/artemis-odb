package com.artemis.predicate;

import com.artemis.compile.SymbolTableOld;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class FindSetterFor implements Predicate<Method> {
	private final SymbolTableOld.Entry entry;

	FindSetterFor(SymbolTableOld.Entry entry) {
		this.entry = entry;
	}

	@Override
	public boolean apply(Method m) {
		if (0 == (Modifier.PUBLIC & m.getModifiers()))
			return false;

		return check(m, "set" + entry.field)
			|| check(m, entry.field);
	}

	private boolean check(Method m, String setterPattern) {
		String name = m.getName().toLowerCase();
		if (name.equals(setterPattern.toLowerCase())) {
			Class<?>[] types = m.getParameterTypes();
			return types.length == 1 && entry.type == types[0];
		}

		return false;
	}
}
