package com.artemis.compile.poet;

import com.artemis.compile.NodeOld;
import com.artemis.compile.SymbolTableOld;
import com.squareup.javapoet.TypeSpec;

import java.util.List;

public class UtilInvokerGenerator implements SourceGenerator {
	private final SymbolTableOld symbols;
	private final List<NodeOld> entitiyComponents;

	public UtilInvokerGenerator(SymbolTableOld symbols, List<NodeOld> entitiyComponents) {
		this.symbols = symbols;
		this.entitiyComponents = entitiyComponents;
	}

	@Override
	public void generate(TypeSpec.Builder builder) {

	}
}
