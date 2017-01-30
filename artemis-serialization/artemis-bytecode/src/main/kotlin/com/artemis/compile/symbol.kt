package com.artemis.compile

import net.onedaybeard.transducers.map
import kotlin.reflect.KClass

data class Symbol (val owner: Class<*>, val field: String, val type: Class<*>) {
	override fun toString(): String {
		return "(${owner.simpleName}::$field ${type.simpleName})"
	}
}

fun symbolsOf(obj: Any): List<Symbol> {
    return symbolsOf(obj.javaClass)
}

fun symbolsOf(type: Class<*>): List<Symbol> {
    return intoList(xf = javaToKotlin + allFields + validFields + asSymbolsOf(type.kotlin),
                    input = listOf(type))
}

fun symbolsOf(types: Iterable<Class<*>>): List<Symbol> {
    val toFields = javaToKotlin + allFields + validFields
    return mutableListOf<Symbol>().apply {
        types.forEach { into(toFields + asSymbolsOf(it.kotlin),
                             listOf(it))
        }
    }
}

@Deprecated("not working as intended when count(V) !+ count(K)")
fun symbolsOf(vararg types: KClass<*>): List<Symbol> {

    val fields = makePair(left = map { input: KClass<*> -> input },
                          right = allFields + validFields)

	return intoList(xf = fields + asSymbolsOf(),
	                input = types.asIterable())
}
