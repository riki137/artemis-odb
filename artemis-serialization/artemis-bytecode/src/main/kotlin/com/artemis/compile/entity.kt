package com.artemis.compile

import com.artemis.Component
import com.badlogic.gdx.utils.JsonValue

data class EntityData(val entityId: Int,
                      val archetype: Int,
                      val tag: String? = null,
                      val groups: List<String>,
                      val components: List<Node>) {

	override fun toString(): String {
		val sb = StringBuilder()

		sb.append("EntityData[id=$entityId cId=$archetype")
		if (tag != null)
			sb.append(" tag=" + tag)

		if (groups.isNotEmpty())
			sb.append(" groups=" + groups.joinToString { it } )

		sb.append("]\n")
		components.forEach { sb.append(it) }

		return sb.toString()
	}
}


