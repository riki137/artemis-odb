package com.artemis.compile;

import java.lang.reflect.Field;
import java.util.*;

import static com.artemis.compile.SymbolTableOld.Key.key;
import static java.lang.reflect.Modifier.STATIC;
import static java.lang.reflect.Modifier.TRANSIENT;

/**
 * <p>Registers type-field mappings. Primitive fields are assigned
 * as-is, while other data types are registered upon being
 * encountered.</p>
 *
 * <p>The {@link GlobalComponentContext} is responsible for registering
 * new types. This class is re-usable across compiler contexts.
 * Symbols entries are resolved from the class themselves; not all
 * referenced entries are guaranteed to be referenced in the json</p>
 */
public class SymbolTableOld {
	private final Map<Key, Entry> symbolMap = new HashMap<>();
	private final Set<Class<?>> registered = new HashSet<>();

	protected SymbolTableOld() {}

	static boolean isValid(Field f) {
		return 0 == ((STATIC | TRANSIENT) & f.getModifiers());
	}

	public static boolean isBuiltinType(Class<?> fieldType) {
		return fieldType.isPrimitive() || fieldType == String.class;
	}

	public static Class<?> unbox(Class<?> t) {
		if (Long.class == t)
			return long.class;
		else if (Integer.class == t)
			return int.class;
		else if (Short.class == t)
			return short.class;
		else if (Character.class == t)
			return char.class;
		else if (Byte.class == t)
			return byte.class;
		else if (Boolean.class == t)
			return boolean.class;
		else if (Double.class == t)
			return double.class;
		else if (Float.class == t)
			return float.class;
		else
			return t;
	}

	public void register(Class<?> type) {
		if (isBuiltinType(type) || registered.contains(type))
			return;

		registered.add(type);
		Class<?> current = type;
		do {
			for (Field f : current.getDeclaredFields()) {
				if (!isValid(f))
					continue;

				register(f.getType());

				Entry e = new Entry(current, f.getName(), f.getType());
				symbolMap.put(key(e), e);
			}

			current = current.getSuperclass();
		} while (current != Object.class);

		assert registered.contains(type);
	}

	public Entry lookup(NodeOld node) {
		return lookup(node.meta.type, node.children().get(0).meta.field);
	}

	public Entry lookup(Class<?> owner, String field) {
		Entry entry = symbolMap.get(key(owner, field));
		if (entry == null) {
			String message = owner.getSimpleName() + ": " + field;
			throw new NullPointerException(message);
		}

		return entry;
	}

	public static final class Entry {
		public final Class<?> type;
		public final Class<?> owner;
		public final String field;

		Entry(Class<?> owner, String field, Class<?> type) {
			this.type = type;
			this.field = field;
			this.owner = owner;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;

			if (o == null || getClass() != o.getClass())
				return false;

			Entry entry = (Entry) o;
			if (!type.equals(entry.type))
				return false;
			if (!owner.equals(entry.owner))
				return false;

			return field.equals(entry.field);
		}

		@Override
		public int hashCode() {
			int result = type.hashCode();
			result = 977 * result + owner.hashCode();
			result = 977 * result + field.hashCode();
			return result;
		}

		@Override
		public String toString() {
			return "(" + owner.getSimpleName() + "::" + field + " " + type.getSimpleName() + ')';
		}
	}

	/**
	 * Type for use as identifier in maps.
	 */
	static class Key {
		public final Class<?> owner;
		public final String field;

		private Key(Class<?> owner, String field) {
			this.owner = owner;
			this.field = field;
		}

		static Key key(Class<?> owner, String field) {
			return new Key(owner, field);
		}

		static Key key(Entry entry) {
			return new Key(entry.owner, entry.field);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Key key = (Key) o;

			if (!owner.equals(key.owner)) return false;
			return field.equals(key.field);
		}

		@Override
		public int hashCode() {
			int result = owner.hashCode();
			if (field != null)
				result = 977 * result + field.hashCode();

			return result;
		}

		@Override
		public String toString() {
			return "(" + owner.getSimpleName() + "::" + field + ')';
		}
	}
}
