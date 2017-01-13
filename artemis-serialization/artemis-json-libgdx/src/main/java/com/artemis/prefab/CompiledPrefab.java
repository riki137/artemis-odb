package com.artemis.prefab;

import com.artemis.World;
import com.artemis.io.EntityPoolFactory;

public class CompiledPrefab {
	private EntityPoolFactory entityFactory;

	protected EntityPoolFactory entityFactory(World world, int count) {
		if (entityFactory == null) {
			entityFactory = new EntityPoolFactory(world);
		}

		entityFactory.configureWith(count);

		return entityFactory;
	}
}
