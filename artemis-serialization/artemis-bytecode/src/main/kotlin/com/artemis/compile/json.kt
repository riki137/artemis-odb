package com.artemis.compile

import com.artemis.Component
import com.badlogic.gdx.utils.JsonValue
import net.onedaybeard.transducers.map

data class Archetype(val id: Int, val types: List<Class<out Component>>)

val jsonValueToString = map(JsonValue::asString)
val stringToClass = map { s: String -> Class.forName(s) }

fun jsonToArchetype(nameToComponent: Map<String, Class<out Component>>) = map { json: JsonValue ->
	val toComponent = map { name: String -> nameToComponent[name] }

	val components = intoList(xf = jsonValueToString + toComponent,
	                          input = json.child.asIterable())

	@Suppress("UNCHECKED_CAST")
	Archetype(json.name.toInt(), components as List<Class<Component>>)
}

fun jsonToComponent(type: Class<*>, json: JsonValue) {

}

fun jsonToEntityData(nameToComponent: Map<String, Class<out Component>>) = map { json: JsonValue ->
	EntityData(entityId   = json.name.toInt(),
	           archetype  = json["archetype"].asInt(),
	           tag        = json["tag"]?.asString(),
	           groups     = TODO("groups"),
	           components = TODO("components"))
}

@Suppress("UNCHECKED_CAST")
val componentMapping = map { json: JsonValue ->
	json.asString() to (Class.forName(json.name) as Class<Component>)
}

fun entitiesOf(json: JsonValue,
               nameToComponent: Map<String, Class<Component>> = componentIdentifiers(json)) : List<EntityData> {

	return intoList(xf = jsonToEntityData(nameToComponent),
	                input = json["entities"])
}

fun archetypesOf(json: JsonValue,
                 nameToComponent: Map<String, Class<Component>> = componentIdentifiers(json)) : List<Archetype> {

	return intoList(xf = jsonToArchetype(nameToComponent),
	                input = json["archetypes"])
}

fun componentIdentifiers(json: JsonValue) : Map<String, Class<Component>> {
	return intoList(xf = componentMapping,
	                input = json["componentIdentifiers"]).toMap()
}


fun componentsOf(components: JsonValue, symbols: Map<String, Class<*>>) : List<Node> {
	return listOf()
}