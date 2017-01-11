package com.artemis.compile.poet;

import com.artemis.Component;
import com.artemis.ComponentMapper;
import com.artemis.compile.GlobalComponentContext;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import static com.artemis.compile.poet.SymbolUtil.mapperName;
import static javax.lang.model.element.Modifier.PRIVATE;

public class ComponentMapperGenerator implements SourceGenerator {
	private final GlobalComponentContext store;

	public ComponentMapperGenerator(GlobalComponentContext store) {
		this.store = store;
	}

	public void generate(TypeSpec.Builder builder) {
		for (Class<? extends Component> type : store.types()) {
			ParameterizedTypeName typeName = ParameterizedTypeName.get(ComponentMapper.class, type);

			FieldSpec fieldSpec = FieldSpec.builder(
				typeName, mapperName(type), PRIVATE).build();
			builder.addField(fieldSpec);
		}
	}
}
