package com.artemis.prefab;

import com.artemis.World;
import com.artemis.annotations.PrefabData;
import com.artemis.io.SaveFileFormat;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;

@PrefabData("prefab/some_prefab.json")
public class SomePrefab extends Prefab {
	protected SomePrefab(World world) {
		super(world, new InternalFileHandleResolver());
	}

	public void someCreate() {
		SaveFileFormat l = create();
	}
}
