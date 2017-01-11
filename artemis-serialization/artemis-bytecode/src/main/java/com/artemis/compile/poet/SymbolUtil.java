package com.artemis.compile.poet;

import com.artemis.compile.SymbolTable;
import com.artemis.predicate.Predicate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static java.lang.Character.toLowerCase;

final class SymbolUtil {
	private SymbolUtil() {}

	static String mapperName(Class<?> type) {
		String name = type.getSimpleName();
		return String.format("%s%sMapper",
			toLowerCase(name.charAt(0)), name.substring(1));
	}

	static String mapperName(SymbolTable.Entry entry) {
		return mapperName(entry.type);
	}

	static boolean isWritable(SymbolTable.Entry entry) {
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
