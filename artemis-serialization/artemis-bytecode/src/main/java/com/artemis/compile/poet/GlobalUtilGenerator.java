package com.artemis.compile.poet;

import com.artemis.compile.MutationGraph;
import com.artemis.compile.SymbolTable;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.artemis.compile.poet.SymbolUtil.isAccessible;
import static com.artemis.compile.poet.SymbolUtil.method;
import static com.artemis.predicate.Predicates.findSetterFor;

public class GlobalUtilGenerator implements SourceGenerator {
	private final List<MethodFactory> factories = new ArrayList<>();
	private final MutationGraph mutationGraph;

	public GlobalUtilGenerator(MutationGraph mutationGraph) {
		this.mutationGraph = mutationGraph;
		factories.add(new FieldMethod());
		factories.add(new SetterMethod());
	}

	protected List<MethodSpec> generate(List<SymbolTable.Entry> entries) {
		List<MethodSpec> methods = new ArrayList<>();
		for (SymbolTable.Entry entry : entries) {
			methods.add(generate(entry));
		}

		return methods;
	}

	protected MethodSpec generate(SymbolTable.Entry entry) {
		for (MethodFactory factory : factories) {
			if (factory.check(entry)) {
				return factory.generate(entry);
			}
		}

		throw new RuntimeException("Failed generation: " + entry);
	}

	@Override
	public void generate(TypeSpec.Builder builder) {
		List<MethodSpec> generated = generate(mutationGraph.getRegistered());
		builder.addMethods(generated);
	}

	private static class FieldMethod implements MethodFactory {
		@Override
		public boolean check(SymbolTable.Entry entry) {
			return isAccessible(entry);
		}

		@Override
		public MethodSpec generate(SymbolTable.Entry entry) {
			String name = entry.owner.getSimpleName() + "_" + entry.field;

			return MethodSpec.methodBuilder(name)
				.addModifiers(Modifier.STATIC, Modifier.PUBLIC)
				.addParameter(entry.owner, "owner")
				.addParameter(entry.type, "value")
				.addStatement("$N.$N = $L", "owner", entry.field, "value")
				.build();
		}
	}

	private static class SetterMethod implements MethodFactory {
		@Override
		public boolean check(SymbolTable.Entry entry) {
			return !isAccessible(entry)
				&& method(entry, findSetterFor(entry)) != null;
		}

		@Override
		public MethodSpec generate(SymbolTable.Entry entry) {
			String name = entry.owner.getSimpleName() + "_" + entry.field;

			Method method = method(entry, findSetterFor(entry));
			return MethodSpec.methodBuilder(name)
				.addModifiers(Modifier.STATIC, Modifier.PUBLIC)
				.addParameter(entry.owner, "owner")
				.addParameter(entry.type, "value")
				.addStatement("$N.$N($L)", "owner", method.getName(), "value")
				.build();
		}
	}

	public interface MethodFactory {
		boolean check(SymbolTable.Entry entry);
		MethodSpec generate(SymbolTable.Entry entry);
	}
}
