package com.artemis.compile;

import com.artemis.Component;
import com.badlogic.gdx.utils.JsonValue;

import java.util.*;

/**
 */
public class EntityData {
	public final List<Entry> entities;

	protected EntityData(JsonValue json,
	                     GlobalComponentContext components,
	                     NodeFactory factory) {

		entities = parseEntityData(json, components, factory);
	}

	private List<Entry> parseEntityData(JsonValue json,
	                                    GlobalComponentContext components,
	                                    NodeFactory factory) {

		List<Entry> entities = new ArrayList<>();
		JsonValue entityIt = json.get("entities").child;
		do {
			int entityId = Integer.parseInt(entityIt.name);
			int archetype = entityIt.get("archetype").asInt();
			String tag = tag(entityIt);
			List<String> groups = groups(entityIt);
			List<Node> nodes = components(entityIt, factory, components);

			entities.add(new Entry(entityId, archetype, tag, groups, nodes));
		} while ((entityIt = entityIt.next) != null);

		return entities;
	}

	private List<Node> components(JsonValue entityIt,
	                              NodeFactory factory,
	                              GlobalComponentContext components) {

		JsonValue it = entityIt.get("components").child;
		if (it == null)
			return Collections.emptyList();

		List<Node> nodes = new ArrayList<>();
		do {
			Class<? extends Component> type = components.get(it.name);
			Node componentNode = factory.create(type, it, null);
			nodes.add(componentNode);
		} while ((it = it.next) != null);

		return nodes;
	}

	private List<String> groups(JsonValue json) {
		JsonValue group = json.get("groups");
		if (group == null)
			return Collections.emptyList();

		List<String> groups = new ArrayList<>();
		JsonValue it = group.child;
		do {
			groups.add(group.asString());
		} while ((it = it.next) != null);

		return groups;
	}

	private String tag(JsonValue json) {
		JsonValue tag = json.get("tag");
		return tag != null ? tag.asString() : null;
	}

	public static final class Entry {
		public final int entityId;
		public final int archetype;
		public final String tag;
		public final List<String> groups;
		public final List<Node> components;

		public Entry(int entityId,
		             int archetype,
		             String tag,
		             List<String> groups,
		             List<Node> components) {

			this.entityId = entityId;
			this.archetype = archetype;
			this.tag = tag;
			this.groups = groups;
			this.components = components;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("EntityData[id=" + entityId + " cId=" + archetype);
			if (tag != null)
				sb.append(" tag=" + tag);

			if (groups.size() > 0)
				sb.append(" groups=" + groups);

			sb.append("]\n");
			for (Node c : components) {
				sb.append(c);
			}

			return sb.toString();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Entry entry = (Entry) o;

			if (entityId != entry.entityId)
				return false;
			if (archetype != entry.archetype)
				return false;
			if (tag != null ? !tag.equals(entry.tag) : entry.tag != null)
				return false;
			if (!groups.equals(entry.groups))
				return false;

			return components.equals(entry.components);
		}

		@Override
		public int hashCode() {
			int result = entityId;
			result = 31 * result + archetype;
			result = 31 * result + (tag != null ? tag.hashCode() : 0);
			result = 31 * result + groups.hashCode();
			result = 31 * result + components.hashCode();
			return result;
		}
	}
}
