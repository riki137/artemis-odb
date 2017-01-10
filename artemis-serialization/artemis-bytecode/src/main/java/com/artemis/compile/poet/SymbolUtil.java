package com.artemis.compile.poet;

import com.artemis.compile.SymbolTable;
import com.artemis.predicate.Predicate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class SymbolUtil {
	private SymbolUtil() {}

	static boolean isAccessible(SymbolTable.Entry entry) {
		Field f = field(entry);
		return f != null
			&& 0 != (Modifier.PUBLIC & f.getModifiers());
	}

	public static Field field(SymbolTable.Entry entry) {
		String field = entry.field;

		Class<?> type = entry.owner;
		do {
			for (Field f : type.getDeclaredFields()) {
				if (field.equals(f.getName()))
					return f;
			}
		} while ((type = type.getSuperclass()) != null);

		return null;
	}

	public static Method method(SymbolTable.Entry entry, Predicate<Method> filter) {
		Class<?> type = entry.owner;
		do {
			for (Method m : type.getDeclaredMethods()) {
				if (filter.apply(m))
					return m;
			}
		} while ((type = type.getSuperclass()) != null);

		return null;
	}
}
