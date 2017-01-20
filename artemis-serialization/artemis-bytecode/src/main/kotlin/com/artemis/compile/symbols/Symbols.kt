package com.artemis.compile.symbols

import net.onedaybeard.transducers.ReducingFunction
import net.onedaybeard.transducers.StepFunction
import java.lang.reflect.Field
import java.util.concurrent.atomic.AtomicBoolean

data class Symbol (val owner: Class<*>, val field: String, val type: Class<*>) {
	override fun toString(): String {
		return "(${owner.simpleName}::$field ${type.simpleName})"
	}
}

//fun fieldsOf() = object : Transducer<Class<*>, MutableList<Field>> {
//	override fun <R> apply(rf: ReducingFunction<R, Class<*>>): ReducingFunction<R, MutableList<Field>> {
//
//	}
//}

fun fieldsOf() = object : StepFunction<MutableList<Field>, Class<*>> {
	override fun apply(result: MutableList<Field>,
	                   input: Class<*>,
	                   reduced: AtomicBoolean): MutableList<Field> {

		result.addAll(input.fields.toList<Field>())
		return result
	}
}