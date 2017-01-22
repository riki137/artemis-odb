package com.artemis.compile

import com.badlogic.gdx.utils.JsonValue
import net.onedaybeard.transducers.Transducer
import net.onedaybeard.transducers.map
import net.onedaybeard.transducers.transduce
import kotlin.reflect.KClass

/**
 * Intermediate object graph representing mapped json and/or objects.
 */
data class Node(val type: Class<*> = Any::class.java,
                val field: String? = null,
                val payload: Any? = null,
                val children: MutableList<Node> = mutableListOf()) {

	constructor(type: KClass<*>,
	            field: String? = null,
	            payload: Any? = null,
	            vararg nodes : Node) : this(type.java, field, payload) {

		children.addAll(nodes)
	}

	constructor(type: KClass<*>,
	            field: String? = null,
	            vararg nodes : Node) : this(type.java, field, null) {

		children.addAll(nodes)
	}

	constructor(type: KClass<*>, vararg nodes : Node) : this(type.java) {
		children.addAll(nodes)
	}

	fun add(node: Node): Boolean {
		return children.add(node)
	}

	operator fun get(index: Int) : Node {
		return children[index]
	}

	override fun toString(): String {
		return format().toString()
	}

	private fun format(sb: StringBuilder = StringBuilder(), indent: String = "") : StringBuilder {
		sb.append("$indent${formatted()}\n")
		children.forEach { it.format(sb, "$indent    ") }

		return sb
	}

	private fun formatted(): String {
		val p = payload ; val f = field

		val name = type.simpleName
		return if (f == null && p == null) "($name)"
		  else if (f == null && p != null) "(${name.padEnd(10)} $p ${p.javaClass.simpleName})"
		  else if (f != null && p != null) "(${f.padEnd(10)} ${name.padEnd(10)} $p ${p.javaClass.simpleName})"
		  else                             "(${f!!.padEnd(10)} ${type.simpleName})"
	}
}


private val sanitizeNodes: Transducer<Node, Node> = map { n ->
	if (n.type == Short::class.java) {
		Node(n.type,
		     n.field,
		     (n.payload as? Integer)?.toShort() ?: n.payload)
	} else if (n.type == Byte::class.java) {
		Node(n.type,
		     n.field,
		     (n.payload as? Integer)?.toByte() ?: n.payload)
	} else {
		n
	}
}

fun symbolToNode(owner: Any) : Transducer<Node, Symbol> {
	return map { symbol: Symbol ->
		val value = owner.readField(symbol)
		if (isBuiltInType(symbol)) {
			Node(symbol.type, symbol.field, value)
		} else {
			toNode(value!!, symbol.field)
		}
	}
}

fun Any.readField(symbol: Symbol): Any? {
	return this.javaClass.getField(symbol.field).get(this)
}

fun toNode(obj: Any, name: String? = null): Node {

	return transduce(xf = symbolToNode(obj),
	                 rf = buildNodeStep,
	                 init = Node(type = obj.javaClass, field = name),
	                 input = symbolsOf(obj))
}

fun toNode(type: Class<*>,
           json: JsonValue,
           name: String? = null): Node {

	return transduce(xf = symbolToNode(json) + sanitizeNodes,
	                 rf = buildNodeStep,
	                 init = Node(type = type, field = name),
	                 input = symbolsOf(type.kotlin))
}

fun isBuiltInType(symbol: Symbol): Boolean {
	return symbol.type.isPrimitive || symbol.type == String::class.java
}


private val buildNodeStep = { result: Node, input: Node -> result.apply { add(input) } }
