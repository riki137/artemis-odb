package com.artemis.compile

import net.onedaybeard.transducers.map
import kotlin.reflect.KClass

data class Symbol (val owner: Class<*>, val field: String, val type: Class<*>) {
	override fun toString(): String {
		return "(${owner.simpleName}::$field ${type.simpleName})"
	}
}

fun symbolsOf(obj: Any): List<Symbol> {
	return symbolsOf(obj.javaClass.kotlin)
}

fun symbolsOf(type: KClass<*>): List<Symbol> {
	return intoList(xf = allFields + validFields + asSymbolsOf(type),
	                input = listOf(type))
}

@Deprecated("not working as intended when count(V) !+ count(K)")
fun symbolsOf(vararg types: KClass<*>): List<Symbol> {

    val fields = makePair(left = map { input: KClass<*> -> input },
                          right = allFields + validFields)

	return intoList(xf = fields + asSymbolsOf(),
	                input = types.asIterable())
}
