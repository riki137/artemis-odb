package com.artemis.compile.poet;

import com.artemis.compile.Node;
import com.artemis.compile.SymbolTable;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.artemis.compile.poet.SymbolUtil.isWritable;
import static com.artemis.compile.poet.SymbolUtil.method;
import static com.artemis.predicate.Predicates.findSetterFor;

public class UtilInvokerGenerator implements SourceGenerator {
	private final SymbolTable symbols;
	private final List<Node> entitiyComponents;

	public UtilInvokerGenerator(SymbolTable symbols, List<Node> entitiyComponents) {
		this.symbols = symbols;
		this.entitiyComponents = entitiyComponents;
	}

	@Override
	public void generate(TypeSpec.Builder builder) {

	}
}
