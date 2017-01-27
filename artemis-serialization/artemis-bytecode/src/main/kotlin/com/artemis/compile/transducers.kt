package com.artemis.compile

import com.badlogic.gdx.utils.Json
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import net.onedaybeard.transducers.*
import java.lang.reflect.Field
import java.lang.reflect.Modifier.STATIC
import java.lang.reflect.Modifier.TRANSIENT
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

private val jsonReader = CoreJsonReader()

@Suppress("UNCHECKED_CAST")
fun <R : MutableList<A>, A, B> intoList(xf: Transducer<A, B>,
                                        init: R = mutableListOf<A>() as R,
                                        input: Iterable<B>): R {
	return transduce(xf, object : ReducingFunction<R, A> {
		override fun apply(result: R,
		                   input: A,
		                   reduced: AtomicBoolean): R {
			result.add(input)
			return result
		}
	}, init, input)
}


@Suppress("UNUSED")
class Key<K> // <-- hah!!! type infer it below!

@Suppress("UNCHECKED_CAST")
fun <R, A, B, K> intoMap(xf: Transducer<A, B>,
                         init: R = mutableMapOf<K, A>() as R,
                         keyType: Key<K> = Key<K>(),
                         input: Iterable<B>): R
		where R : Map<K, A> {

	val rf = object : ReducingFunction<R, A> {
		override fun apply(result: R,
		                   input: A,
		                   reduced: AtomicBoolean): R = result
	}

	return transduce(xf, rf, init, input)
}

fun <A, K> groupBy(f: (A) -> K) = object : Transducer<Map<K, Iterable<A>>, A> {
	override fun <R> apply(rf: ReducingFunction<R, Map<K, Iterable<A>>>): ReducingFunction<R, A> {
		return object : ReducingFunction<R, A> {
			override fun apply(): R = rf.apply()

			override fun apply(result: R,
			                   input: A,
			                   reduced: AtomicBoolean): R {

				val key: K = f(input)

				@Suppress("UNCHECKED_CAST")
				val group = result as MutableMap<K, MutableList<A>>
				val list = group[key] ?: mutableListOf<A>().apply { group[key] = this }
				list += input

				return result
			}
		}
	}
}


operator fun <A, B, C> Transducer<B, C>.plus(right: Transducer<A, in B>): Transducer<A, C>
	= this.comp(right)

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
