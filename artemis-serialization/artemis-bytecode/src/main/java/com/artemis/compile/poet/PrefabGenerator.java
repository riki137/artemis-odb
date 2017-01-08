package com.artemis.compile.poet;

import com.artemis.compile.CompiledPrefab;
import com.artemis.compile.poet.TypeGenerator;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class PrefabGenerator {
	private final TypeSpec.Builder builder;
	private List<TypeGenerator> generators = new ArrayList<>();

	public PrefabGenerator(String name) {
		builder = TypeSpec.classBuilder("Prefab" + name)
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL)
			.superclass(ClassName.get(CompiledPrefab.class));
	}

	public void add(TypeGenerator generator) {
		generators.add(generator);
	}

	public String generate() {
		for (TypeGenerator generator : generators) {
			generator.generate(builder);
		}

		JavaFile jf = JavaFile.builder("com.artemis", builder.build())
			.indent("\t")
			.build();

		return jf.toString();
	}
}
