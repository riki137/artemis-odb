package com.artemis.compile;

import com.artemis.World;
import com.artemis.io.EntityPoolFactory;

public class CompiledPrefab {
	private EntityPoolFactory entityFactory;

	protected EntityPoolFactory entityFactory(World world) {
		if (entityFactory == null) {
			entityFactory = new EntityPoolFactory(world);
		}

		return entityFactory;
	}
}
