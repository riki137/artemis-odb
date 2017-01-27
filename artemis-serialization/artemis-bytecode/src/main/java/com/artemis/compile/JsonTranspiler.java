package com.artemis.compile;

import com.artemis.compile.poet.*;
import com.artemis.prefab.CompiledPrefab;
import com.badlogic.gdx.utils.JsonValue;

// yes, yes - not a compiler, and transpiler is (maybe) not a word,
// but the vocabulary makes things a bit easier to navigate.
public class JsonTranspiler {
	private final SymbolTableOld symbols = new SymbolTableOld();
	private final GlobalComponentContext components = new GlobalComponentContext();
	private final NodeFactoryOld factory = new NodeFactoryOld(symbols);
	private final MutationGraph mutationGraph = new MutationGraph(symbols);

	public void compile(JsonValue json) {
		boolean newTypesRegistered = components.register(json);
		if (newTypesRegistered) {
			for (Class<?> type : components.types()) {
				symbols.register(type);
			}
		}

//		List<EntityData> entityNodes = parseEntityData(json, factory);

		EntityDataOld entityData = new EntityDataOld(json, components, factory);
		for (EntityDataOld.Entry data : entityData.entities) {
			for (NodeOld n : data.components) {
				mutationGraph.add(null, n);
			}
		}

		NodePoet poet = new NodePoet(symbols);

		TargetFabricator prefab = new TargetFabricator("SamplePrefab");
		prefab.add(new SuperClassGenerator(CompiledPrefab.class));
		prefab.add(new TransmuterFieldGenerator(json, components));
		prefab.add(new ComponentMapperGenerator(components));
		prefab.add(new EntityCreateGenerator(entityData.entities, poet, symbols));

		TargetFabricator globalUtil = new TargetFabricator("GlobalUtil");
		globalUtil.add(new GlobalUtilGenerator(mutationGraph));
		String global = globalUtil.generate();

		String generate = prefab.generate();
		System.out.println(generate);

//		factory.
//		components.types()
//		factory.create()
	}





}
