package com.artemis.compile

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import net.onedaybeard.transducers.Transducer
import net.onedaybeard.transducers.filter
import net.onedaybeard.transducers.map
import net.onedaybeard.transducers.mapcat
import java.lang.reflect.Field
import java.lang.reflect.Modifier.STATIC
import java.lang.reflect.Modifier.TRANSIENT
import kotlin.reflect.KClass

private val jsonReader = CoreJsonReader()

val objectToType  = map { t: Any -> t.javaClass.kotlin }
val kotlinToJava  = map { t: KClass<*> -> t.java }
val classToFields = mapcat { t: Class<*> -> t.declaredFields.asIterable() }
val withParents = mapcat { t: Class<*> ->
	val types = mutableListOf(t)

	var current = t.superclass
	while (current != null) {
		types += current
		current = current.superclass
	}

	types
}

val validFields: Transducer<Field, Field> =
	filter { 0 == (it.modifiers and (STATIC or TRANSIENT)) }

val allFields: Transducer<Field, KClass<*>> =
	kotlinToJava + withParents + classToFields

fun toJson(json: Json): Transducer<String, Any>
	= map { json.prettyPrint(it) }


val toJsonValue: Transducer<JsonValue, String>
	= map { JsonReader().parse(it) }

fun asSymbolsOf(owner: KClass<*>) = map { f: Field -> Symbol(owner.java, f.name, f.type) }

fun asSymbolsOf() = map { f: Pair<KClass<*>, Field> ->
    Symbol(f.first.java,
           f.second.name,
           f.second.type).apply(::println)
}


fun symbolToNode(json: JsonValue) : Transducer<Node, Symbol> {
	return map { symbol: Symbol ->
		if (isBuiltInType(symbol)) {
			val payload = jsonReader.read(symbol.type, json[symbol.field])
			Node(symbol.type, symbol.field, payload)
		} else {
			toNode(symbol.type, json[symbol.field], symbol.field)
		}
	}
}
