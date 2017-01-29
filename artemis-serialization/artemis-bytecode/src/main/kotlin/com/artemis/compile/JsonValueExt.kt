package com.artemis.compile

import com.badlogic.gdx.utils.JsonValue

fun JsonValue.asIterable() = Iterable {
    object : Iterator<JsonValue> {
        var current: JsonValue? = this@asIterable

        override fun next(): JsonValue {
            val node = current!!
            current = current?.next
            return node
        }

        override fun hasNext() = current != null
    }
}