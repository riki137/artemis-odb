package com.artemis.compile;

import com.artemis.predicate.Predicate;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Field;

import static com.artemis.compile.NodeOld.node;
import static com.artemis.predicate.FilterIterator.filter;

public class NodeFactoryOld {
	private final CoreJsonReader jsonReader;
	private SymbolTableOld symbols;

	private Predicate<Field> validFields = new Predicate<Field>() {
		@Override
		public boolean apply(Field field) {
			return SymbolTableOld.isValid(field);
		}
	};

	protected NodeFactoryOld(SymbolTableOld symbolTable) {
		symbols = symbolTable;
		jsonReader = new CoreJsonReader();
	}

	public NodeOld create(Class<?> type, JsonValue json) {
		return create(type, json, json.name);
	}

	public NodeOld create(Class<?> type, JsonValue json, String name) {
		NodeOld node = node(type, name);

		Class<?> current = type;
		do {
			for (Field f : current.getDeclaredFields()) {
				if (SymbolTableOld.isValid(f)) {
					create(type, json.get(f.getName()), node);
				}
			}
			current = current.getSuperclass();
		} while (current != Object.class);

		return node;
	}

	public NodeOld create(Object source) {
		return create(source, null);
	}

	public NodeOld create(Object source, String name) {
		NodeOld node = node(source.getClass(), name);

		Class<?> current = source.getClass();
		do {
			Field[] fields = current.getDeclaredFields();
			for (Field f : filter(fields, validFields)) {
				create(source, f, node);
			}
			current = current.getSuperclass();
		} while (current != Object.class);

		return node;
	}

	private void create(Object owner, Field field, NodeOld n) {
		Object o = readField(owner, field);
		if (SymbolTableOld.isBuiltinType(field.getType())) {
			n.add(node(field.getType(), field.getName(), o));
		} else {
			n.add(create(o, field.getName()));
		}
	}

	private Object readField(Object owner, Field field) {
		try {
			return field.get(owner);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void create(Class<?> type, JsonValue json, NodeOld n) {
		SymbolTableOld.Entry symbol = symbols.lookup(type, json.name);
		if (symbol == null) {
			String name = json != null ? json.name : null;
			throw new NullPointerException(type.getSimpleName() + "::" + name);
		}

		if (SymbolTableOld.isBuiltinType(symbol.type)) {
			Object o = jsonReader.read(symbol.type, json);
			n.add(node(symbol.type, symbol.field, o));
		} else {
			NodeOld object = create(symbol.type, json);
			n.add(object);
		}
	}
}
