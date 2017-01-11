package com.artemis.compile;

import com.artemis.compile.poet.*;
import com.badlogic.gdx.utils.JsonValue;

// yes, yes - not a compiler, and transpiler is (maybe) not a word,
// but the vocabulary makes things a bit easier to navigate.
public class JsonTranspiler {
	private final SymbolTable symbols = new SymbolTable();
	private final GlobalComponentContext components = new GlobalComponentContext();
	private final NodeFactory factory = new NodeFactory(symbols);
	private final MutationGraph mutationGraph = new MutationGraph(symbols);

	public void compile(JsonValue json) {
		boolean newTypesRegistered = components.register(json);
		if (newTypesRegistered) {
			for (Class<?> type : components.types()) {
				symbols.register(type);
			}
		}

//		List<EntityData> entityNodes = parseEntityData(json, factory);

		EntityData entityData = new EntityData(json, components, factory);
		for (EntityData.Entry data : entityData.entities) {
			for (Node n : data.components) {
				mutationGraph.add(null, n);
			}
		}

		TargetFabricator prefab = new TargetFabricator("SamplePrefab");
		prefab.add(new SuperClassGenerator(CompiledPrefab.class));
		prefab.add(new TransmuterFieldGenerator(json, components));
		prefab.add(new ComponentMapperGenerator(components));

		TargetFabricator globalUtil = new TargetFabricator("GlobalUtil");
		globalUtil.add(new GlobalUtilGenerator(mutationGraph));
		String global = globalUtil.generate();

//		TargetGenerator prefab = new TargetGenerator("GlobalUtil");
//		prefab.add(new GlobalUtilGenerator());

		String generate = prefab.generate();
		System.out.println(generate);

//		factory.
//		components.types()
//		factory.create()
	}





}
