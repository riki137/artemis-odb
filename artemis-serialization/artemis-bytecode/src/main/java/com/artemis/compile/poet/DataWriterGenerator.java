package com.artemis.compile.poet;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class DataWriterGenerator {
	private final TypeSpec.Builder builder;
	private List<TypeGenerator> generators = new ArrayList<>();

	public DataWriterGenerator(String name) {
		builder = TypeSpec.classBuilder("Prefab" + name);
		builder.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
	}

	public void add(TypeGenerator generator) {
		generators.add(generator);
	}

	public String generate() {
		for (TypeGenerator generator : generators) {
			generator.generate(builder);
		}

		JavaFile jf = JavaFile.builder("com.prefab", builder.build())
			.indent("\t")
			.build();

		return jf.toString();
	}
}
