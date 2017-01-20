package com.artemis.compile.symbols

import com.artemis.Component
import com.artemis.component.CompC
import net.onedaybeard.transducers.*
import org.junit.Test
import java.lang.reflect.Field
import kotlin.reflect.KClass

class SymbolTest {




	@Test
	fun fieldsOfTest() {

		val kotlinToJava  = map { t: KClass<*> -> t.java }
		val classToFields = mapcat { t: Class<*> -> t.declaredFields.asIterable() }

		val filterComponents = filter { t: Class<*> ->
			Component::class.java.isAssignableFrom(t) && t != Component::class.java
		}

		val withParents = mapcat { t: Class<*> ->
			val types = mutableListOf(t)

			var current = t.superclass
			while (current != null) {
				types += current
				current = current.superclass
			}

			types
		}

		val allFieldsOf = kotlinToJava + withParents;
//		val symbolsTransducer =	kotlinToJava
//				.comp(withParents)
//				.comp(filterComponents)
//				.comp(classToFields)
//				.comp(asSymbolsOf(CompC::class))
		val symbolsTransducer =	allFieldsOf + filterComponents + classToFields + asSymbolsOf(CompC::class)
//				.comp(withParents)
//				.comp(filterComponents)
//				.comp(classToFields)
//				.comp(asSymbolsOf(CompC::class))

		val symbols = transduce(xf = symbolsTransducer,
		                        rf = { result, input -> result.apply { add(input) } },
		                        init = mutableListOf<Symbol>(),
		                        input = listOf(CompC::class))

		val oldThing = transduce(xf = map { s: Symbol -> s.copy(owner = CompC::class.java) },
		                         rf = { result, input -> result.apply { add(input) } },
		                         init = mutableListOf<Symbol>(),
		                         input = symbols)




		symbols assertEquals oldThing
//		fields assertEquals symbols
//		fields assertEquals PrimitiveComponent::class.java.fields.toList<Field>()
//		parents assertEquals PrimitiveComponent::class.java.fields.toList<Field>()

//		fieldsOf<PrimitiveComponent>() assertEquals
//			listOf<Field>()
//			PrimitiveComponent::class.java.fields.toList<Field>()

		// old
		val parents = transduce(xf = kotlinToJava.comp(withParents),
		                       rf = { result, input -> result.apply { add(input) } },
		                       init = mutableListOf<Class<*>>(),
		                       input = listOf(CompC::class))

		val fields = transduce(xf = kotlinToJava.comp(withParents).comp(filterComponents).comp(classToFields),
		                       rf = { result, input -> result.apply { add(input) } },
		                       init = mutableListOf<Field>(),
		                       input = listOf(CompC::class))
	}

	private fun asSymbolsOf(owner: KClass<*>) = map { f: Field -> Symbol(owner.java, f.name, f.type) }

	private infix fun <T> T.assertEquals(expected: T) = kotlin.test.assertEquals(expected, this)

	operator fun <A, B, C> Transducer<B, C>.plus(right: Transducer<A, in B>): Transducer<A, C>
		= this.comp(right)

//	private operator fun Transducer<B, C>.plus(Transducer<C, D>) = kotlin.test.assertEquals(expected, this)
}