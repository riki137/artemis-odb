package com.artemis.compile.poet;

import com.artemis.Component;
import com.artemis.EntityTransmuter;
import com.artemis.annotations.AspectDescriptor;
import com.artemis.compile.TransmuterStore;
import com.badlogic.gdx.utils.IntMap;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.List;

public class TransmuterFieldGenerator implements TypeGenerator {
	private final TransmuterStore store;

	public TransmuterFieldGenerator(TransmuterStore store) {
		this.store = store;
	}

	public void generate(TypeSpec.Builder builder) {
		ClassName transmuterType = ClassName.get(EntityTransmuter.class);

		for (IntMap.Entry<List<Class<? extends Component>>> entry : store.transmuters) {
			String name = "transmuter" + entry.key;
			FieldSpec field = FieldSpec.builder(transmuterType, name, Modifier.PRIVATE)
				.addAnnotation(annotation(entry.value))
				.build();

			builder.addField(field);
		}
	}

	private AnnotationSpec annotation(List<Class<? extends Component>> types) {
		String delim = "";
		String format = " {";
		for (Class<? extends Component> type : types) {
			format += delim + "$T.class";
			delim = ", ";
		}
		format += " }";

		CodeBlock code = CodeBlock.builder()
			.addStatement(format, (Class[]) types.toArray(new Class[0]))
			.build();

		return AnnotationSpec.builder(AspectDescriptor.class)
			.addMember("all", code)
			.build();
	}
}
