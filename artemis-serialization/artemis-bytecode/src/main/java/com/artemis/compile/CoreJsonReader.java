package com.artemis.compile;

import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;
import java.util.Map;

class CoreJsonReader {
	private Map<Class<?>, ValueReader> readers = new HashMap<>();

	public CoreJsonReader() {
		readers.put(double.class, new ValueReader() {
			@Override
			public Object read(JsonValue json) {
				return json.asDouble();
			}
		});
		readers.put(float.class, new ValueReader() {
			@Override
			public Object read(JsonValue json) {
				return json.asFloat();
			}
		});
		readers.put(long.class, new ValueReader() {
			@Override
			public Object read(JsonValue json) {
				return json.asLong();
			}
		});
		readers.put(int.class, new Int32ValueReader());
		readers.put(short.class, new Int32ValueReader());
		readers.put(byte.class, new Int32ValueReader());
		readers.put(char.class, new ValueReader() {
			@Override
			public Object read(JsonValue json) {
				return json.asChar();
			}
		});
		readers.put(boolean.class, new ValueReader() {
			@Override
			public Object read(JsonValue json) {
				return json.asBoolean();
			}
		});
		readers.put(String.class, new ValueReader() {
			@Override
			public Object read(JsonValue json) {
				return json.asString();
			}
		});
	}

	public Object read(Class<?> type, JsonValue json) {
		ValueReader reader = readers.get(type);
		if (reader == null)
			throw new NullPointerException(type.getSimpleName() + " " + json);

		return reader.read(json);
	}

	interface ValueReader {
		Object read(JsonValue json);
	}

	private static class Int32ValueReader implements ValueReader {
		@Override
		public Object read(JsonValue json) {
			return json.asInt();
		}
	}
}
