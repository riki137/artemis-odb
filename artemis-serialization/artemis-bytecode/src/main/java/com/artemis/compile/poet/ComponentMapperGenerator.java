package com.artemis.compile.poet;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.compile.ComponentStore;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;

public class ComponentMapperGenerator implements TypeGenerator {
	private final ComponentStore store;

	public ComponentMapperGenerator(ComponentStore store) {
		this.store = store;
	}

	public void generate(TypeSpec.Builder builder) {
		for (Class<? extends Component> type : store.types()) {
			ParameterizedTypeName typeName = ParameterizedTypeName.get(ComponentMapper.class, type);

			String name = "mapper" + type.getSimpleName();
			FieldSpec fieldSpec = FieldSpec.builder(typeName, name, Modifier.PRIVATE).build();

			builder.addField(fieldSpec);
		}
	}
}
