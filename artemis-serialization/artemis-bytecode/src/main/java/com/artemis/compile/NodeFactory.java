package com.artemis.compile;

import com.artemis.predicate.Predicate;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Field;

import static com.artemis.compile.Node.node;
import static com.artemis.predicate.FilterIterator.filter;

public class NodeFactory {
	private final CoreJsonReader jsonReader;
	private SymbolTable symbols;

	private Predicate<Field> validFields = new Predicate<Field>() {
		@Override
		public boolean apply(Field field) {
			return SymbolTable.isValid(field);
		}
	};

	protected NodeFactory(SymbolTable symbolTable) {
		symbols = symbolTable;
		jsonReader = new CoreJsonReader();
	}

	public Node create(Class<?> type, JsonValue json) {
		return create(type, json, json.name);
	}

	public Node create(Class<?> type, JsonValue json, String name) {
		Node node = node(type, name);

		Class<?> current = type;
		do {
			for (Field f : current.getDeclaredFields()) {
				if (SymbolTable.isValid(f)) {
					create(type, json.get(f.getName()), node);
				}
			}
			current = current.getSuperclass();
		} while (current != Object.class);

		return node;
	}

	public Node create(Object source) {
		return create(source, null);
	}

	public Node create(Object source, String name) {
		Node node = node(source.getClass(), name);

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

	private void create(Object owner, Field field, Node n) {
		Object o = readField(owner, field);
		if (SymbolTable.isBuiltinType(field.getType())) {
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

	private void create(Class<?> type, JsonValue json, Node n) {
		SymbolTable.Entry symbol = symbols.lookup(type, json.name);
		if (symbol == null) {
			String name = json != null ? json.name : null;
			throw new NullPointerException(type.getSimpleName() + "::" + name);
		}

		if (SymbolTable.isBuiltinType(symbol.type)) {
			Object o = jsonReader.read(symbol.type, json);
			n.add(node(symbol.type, symbol.field, o));
		} else {
			Node object = create(symbol.type, json);
			n.add(object);
		}
	}
}
