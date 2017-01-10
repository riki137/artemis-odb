package com.artemis.compile;

import com.artemis.compile.poet.*;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.List;

// yes, yes - not a compiler, and transpiler is (maybe) not a word,
// but the vocabulary makes things a bit easier to navigate.
public class JsonTranspiler {
	private final SymbolTable symbols = new SymbolTable();
	private final ComponentStore components = new ComponentStore();
	private final NodeFactory factory = new NodeFactory(symbols);
	private final MutationGraph mutationGraph = new MutationGraph(symbols);

	public void compile(JsonValue json) {
		boolean newTypesRegistered = components.register(json);
		if (newTypesRegistered) {
			for (Class<?> type : components.types()) {
				symbols.register(type);

				try {
					Node node = factory.create(type.newInstance());
					mutationGraph.add(null, node);
				} catch (InstantiationException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}

		TransmuterStore transmuters = new TransmuterStore();
		transmuters.register(components, json);

		TargetGenerator prefab = new TargetGenerator("Sample");
		prefab.add(new TransmuterFieldGenerator(transmuters));
		prefab.add(new ComponentMapperGenerator(components));
		prefab.add(new GlobalUtilGenerator(mutationGraph));

//		TargetGenerator prefab = new TargetGenerator("GlobalUtil");
//		prefab.add(new GlobalUtilGenerator());

		String generate = prefab.generate();
		System.out.println(generate);

//		factory.
//		components.types()
//		factory.create()
	}

}
