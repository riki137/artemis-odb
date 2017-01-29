package com.artemis.compile

data class EntityData(val entityId: Int,
                      val archetype: Int,
                      val key: String? = null,
                      val tag: String? = null,
                      val groups: List<String>,
                      val components: List<Node>) {

	override fun toString(): String {
		val sb = StringBuilder()

		sb.append("EntityData[id=$entityId cId=$archetype")
		if (key != null)
			sb.append(" key=" + tag)

        if (tag != null)
			sb.append(" tag=" + tag)

		if (groups.isNotEmpty())
			sb.append(" groups=" + groups.joinToString { it } )

		sb.append("]\n")
		components.forEach { sb.append(it) }

		return sb.toString()
	}
}


