package com.artemis.compile;

import java.util.HashMap;
import java.util.Map;

public class ComponentStore {
	private final Map<String, Class<?>> keyToComponent = new HashMap<>();

	public void register(String key, Class<?> component) {
		keyToComponent.put(key, component);
	}
}
