package com.artemis.compile

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

//fun symbolsOf(vararg types: KClass<*>): List<Symbol> {
//
//	val fields = allFields + validFields
//
//	makePair(left = { input: KClass<*> -> input },
//	         right = { input: KClass<*> -> input })
//
//	return intoList(xf = makePair({input: KClass<*> -> input}) + allFields + validFields + asSymbolsOf(type),
//	                input = types.asIterable())
//}
