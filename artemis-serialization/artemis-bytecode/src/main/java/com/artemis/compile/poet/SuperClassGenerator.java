package com.artemis.compile.poet;

import com.squareup.javapoet.TypeSpec;

public class SuperClassGenerator implements SourceGenerator {

	private Class<?> superClass;

	public SuperClassGenerator(Class<?> superClass) {
		this.superClass = superClass;
	}

	public void generate(TypeSpec.Builder builder) {
		builder.superclass(superClass);
	}
}
