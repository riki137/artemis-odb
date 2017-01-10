package com.artemis.compile.poet;

import com.squareup.javapoet.TypeSpec;

public interface SourceGenerator {
	void generate(TypeSpec.Builder builder);
}
