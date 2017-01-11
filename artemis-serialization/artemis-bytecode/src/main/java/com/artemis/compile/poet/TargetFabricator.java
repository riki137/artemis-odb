package com.artemis.compile.poet;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TargetFabricator {
	private final TypeSpec.Builder builder;
	private List<SourceGenerator> generators = new ArrayList<>();

	public TargetFabricator(String name) {
		builder = TypeSpec.classBuilder(name)
			.addModifiers(Modifier.PUBLIC, Modifier.FINAL);
	}

	public void add(SourceGenerator generator) {
		generators.add(generator);
	}

	public String generate() {
		for (SourceGenerator generator : generators) {
			generator.generate(builder);
		}

		JavaFile jf = JavaFile.builder("com.artemis.generated", builder.build())
			.indent("\t")
			.build();

		return jf.toString();
	}
}
