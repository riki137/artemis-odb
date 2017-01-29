package com.artemis.compile

import com.artemis.Component
import com.badlogic.gdx.utils.JsonValue

data class Context(
        val json: JsonValue,
        val componentLookup: Map<String, Class<Component>> = componentIdentifiers(json),
        val entities: List<EntityData> = entitiesOf(json, componentLookup),
        val symbols: List<Symbol> = symbolsOf(componentLookup.values))
