package com.artemis.compile.poet;

import com.squareup.javapoet.TypeSpec;

public interface TypeGenerator {
	void generate(TypeSpec.Builder builder);
}
