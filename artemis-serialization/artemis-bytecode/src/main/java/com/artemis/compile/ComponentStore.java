package com.artemis.compile;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

public class ComponentStore {
	private final Map<String, Class<?>> keyToComponent = new HashMap<>();

	protected ComponentStore() {}

	public boolean register(String key, Class<?> component) {
		if (!keyToComponent.containsKey(key)) {
			keyToComponent.put(key, component);
			return true;
		} else {
			return false;
		}
	}

	public Collection<Class<? extends Component>> types() {
		List<Class<? extends Component>> types = new ArrayList<>();
		for (Map.Entry<String, Class<?>> entry : keyToComponent.entrySet()) {
			types.add((Class<? extends Component>) entry.getValue());
		}

		return types;
	}

	public Class<? extends Component> get(String name) {
		return (Class<? extends Component>) keyToComponent.get(name);
	}

	public boolean register(JsonValue json) {
		JsonValue typeIt = json.get("componentIdentifiers").child;
		boolean newRegistrars = false;
		do {
			try {
				Class<?> componentType = Class.forName(typeIt.name);
				newRegistrars |= register(typeIt.asString(), componentType);
			} catch (ClassNotFoundException e) {
				// FIXME - proper exception
				throw new RuntimeException(e);
			}
		} while ((typeIt = typeIt.next) != null);

		return newRegistrars;
	}
}
