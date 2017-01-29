package com.artemis.compile

import com.artemis.compile.poet.generatePrefab
import com.artemis.component.ComponentX
import com.artemis.component.ComponentY
import com.artemis.component.NameComponent
import com.artemis.component.ReusedComponent
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonValue
import org.junit.Test

class JsonTest {
    @Test
    fun read_components_from_json() {
        val json = prefabJson()
        componentIdentifiers(json) assertEquals mapOf(
            "ComponentX" to ComponentX::class.java,
            "ComponentY" to ComponentY::class.java,
            "NameComponent" to NameComponent::class.java,
            "ReusedComponent" to ReusedComponent::class.java)
    }

    @Test
    fun read_archetypes_from_json() {
        val json = prefabJson()
        archetypesOf(json) assertEquals listOf(
            Archetype(1, listOf(ComponentX::class.java,
                                ComponentY::class.java,
                                ReusedComponent::class.java)),
            Archetype(2, listOf(ComponentX::class.java,
                                ComponentY::class.java,
                                ReusedComponent::class.java,
                                NameComponent::class.java)),
            Archetype(4, listOf(ComponentX::class.java,
                                ComponentY::class.java,
                                ReusedComponent::class.java)))
    }

    @Test
    fun read_entity_data_from_json() {
        val json = prefabJson()
        val entities: List<EntityData> = entitiesOf(json).toList()

        entities.size assertEquals 3
        entities[1].components assertEquals listOf(
            Node(ComponentX::class,
                 Node(String::class, "text", "hello 2")),
            Node(ComponentY::class,
                 Node(String::class, "text", "whatever 2")),
            Node(NameComponent::class,
                 Node(String::class, "name", "do i work?")))
    }

    @Test
    fun hello() {
        val json = prefabJson()
        val context = Context(json)


//        val mutated = usedSymbolsOf(context.entities, context.symbols)
        val prefab = generatePrefab("SomePrefab", context)
        println(prefab)
        TODO("something")
    }
}


fun prefabJson(): JsonValue {
    val reader = JsonTest::class.java.getResourceAsStream("/prefab/some_prefab.json")
    return JsonReader().parse(reader)
}
