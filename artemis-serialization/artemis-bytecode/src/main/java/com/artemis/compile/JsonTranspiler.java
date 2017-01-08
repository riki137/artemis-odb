package com.artemis.compile;

import com.artemis.compile.poet.ComponentMapperGenerator;
import com.artemis.compile.poet.PrefabGenerator;
import com.artemis.compile.poet.TransmuterFieldGenerator;
import com.badlogic.gdx.utils.JsonValue;

// yes, yes - not a compiler, and transpiler is (maybe) not a word,
// but the vocabulary makes things a bit easier to navigate.
public class JsonTranspiler {
	private final SymbolTable symbols = new SymbolTable();
	private final ComponentStore components = new ComponentStore();
	private final NodeFactory factory = new NodeFactory(symbols);

	public void compile(JsonValue json) {
		boolean newTypesRegistered = components.register(json);
		if (newTypesRegistered) {
			for (Class<?> type : components.types()) {
				symbols.register(type);
			}
		}

		TransmuterStore transmuters = new TransmuterStore();
		transmuters.register(components, json);

		PrefabGenerator prefab = new PrefabGenerator("Sample");
		prefab.add(new TransmuterFieldGenerator(transmuters));
		prefab.add(new ComponentMapperGenerator(components));

		String generate = prefab.generate();
		System.out.println(generate);
//		components.types()
//		factory.create()
	}
}
