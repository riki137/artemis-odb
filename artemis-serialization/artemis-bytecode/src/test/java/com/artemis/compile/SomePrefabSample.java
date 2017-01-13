package com.artemis.compile;

import com.artemis.ComponentMapper;
import com.artemis.EntityTransmuter;
import com.artemis.World;
import com.artemis.annotations.AspectDescriptor;
import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.NameComponent;
import com.artemis.component.ReusedComponent;
import com.artemis.io.EntityPoolFactory;
import com.artemis.prefab.CompiledPrefab;

import static com.artemis.compile.FunctionSample.ComponentX_text;
import static com.artemis.compile.FunctionSample.ComponentY_text;
import static com.artemis.compile.FunctionSample.NameComponent_text;

public class SomePrefabSample extends CompiledPrefab {
	@AspectDescriptor(all = {
		ComponentX.class,
		ComponentY.class,
		ReusedComponent.class})
	private EntityTransmuter transmuter1;

	@AspectDescriptor(all = {
		ComponentX.class,
		ComponentY.class,
		ReusedComponent.class,
		NameComponent.class})
	private EntityTransmuter transmuter2;

	private ComponentMapper<ComponentX> componentXMapper;
	private ComponentMapper<NameComponent> nameComponentMapper;
	private ComponentMapper<ComponentY> componentYMapper;
	private ComponentMapper<ReusedComponent> reusedComponentMapper;

	public void compiledCreate(World world) {

		EntityPoolFactory factory = entityFactory(world, 3);
		int e;

		e = factory.createEntityId();
		transmuter1.transmute(e);
		{
			ComponentX c = componentXMapper.get(e);
			ComponentX_text(c, "hello");
		}
		{
			ComponentY c = componentYMapper.get(e);
			ComponentY_text(c, "whatever");
		}
		e = factory.createEntityId();
		transmuter2.transmute(e);
		{
			ComponentX c = componentXMapper.get(e);
			ComponentX_text(c, "hello 2");
		}
		{
			ComponentY c = componentYMapper.get(e);
			ComponentY_text(c, "whatever 2");
		}
		{
			NameComponent c = nameComponentMapper.get(e);
			NameComponent_text(c, "do i work?");
		}
		e = factory.createEntityId();
		transmuter1.transmute(e);
		{
			ComponentX c = componentXMapper.get(e);
			ComponentX_text(c, "hello 3");
		}
		{
			ComponentY c = componentYMapper.get(e);
			ComponentY_text(c, "whatever 3");
		}
	}
}
