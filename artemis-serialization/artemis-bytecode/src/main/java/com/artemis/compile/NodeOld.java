package com.artemis.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * Intermediate object graph representing mapped json and/or objects.
 */
public final class NodeOld {
	public final Meta meta;
	public final Object payload;
	private final List<NodeOld> children = new ArrayList<>();

	NodeOld(Class<?> type, String field, Object payload) {
		meta = new Meta(type, field);
		this.payload = payload;
	}

	public List<NodeOld> children() {
		return children;
	}

	public boolean add(NodeOld node) {
		return children.add(node);
	}

	public static NodeOld node(Class<?> type,
	                           NodeOld... children) {

		NodeOld n = new NodeOld(type, null, null);
		for (NodeOld child : children)
			n.children.add(child);

		return n;
	}

	public static NodeOld node(Class<?> type,
	                           String field,
	                           NodeOld... children) {

		return node(type, field, null, children);
	}

	public static NodeOld node(Class<?> type,
	                           String field,
	                           Object payload,
	                           NodeOld... children) {

		NodeOld n = new NodeOld(type, field, payload);
		for (NodeOld child : children)
			n.children.add(child);

		return n;
	}

	private Object widenPayload() {
		Class<?> type = meta.type;
		if (payload instanceof Integer)
			return payload;
		if (type == Character.class || type == char.class)
			return new Integer(((Character) payload).charValue());
		if (type == Short.class || type == Byte.class)
			return Integer.parseInt(payload.toString());
		if (type == short.class || type == byte.class)
			return Integer.parseInt(payload.toString());

		return payload;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		NodeOld node = (NodeOld) o;

		if (!meta.equals(node.meta))
			return false;

		if (!children.equals(node.children))
			return false;

		return payload != null
			? widenPayload().equals(node.widenPayload())
			: node.payload == null;
	}

	@Override
	public int hashCode() {
		int result = meta.hashCode();
		result = 31 * result + children.hashCode();
		result = 31 * result + (payload != null ? payload.hashCode() : 0);
		return result;
	}

	private StringBuilder toStringBuilder(StringBuilder sb, String prepend) {
		String field = meta.field;
		String format =
			(field == null && payload == null) ? "(%2$s)" :
			(field == null && payload != null) ? "(%2$-10s %3$s)" :
		    (field != null && payload != null) ? "(%-10s %-10s %s)"
		                                       : "(%-10s %s)";

		format = prepend + format + '\n';
		sb.append(String.format(format, field, meta.type.getSimpleName(), payload));

		prepend += "    ";
		for (NodeOld child : children)
			child.toStringBuilder(sb, prepend);

		return sb;
	}

	@Override
	public String toString() {
		return toStringBuilder(new StringBuilder(), "").toString();
	}

	public final static class Meta {
		public final Class<?> type;
		public final String field;

		public Meta(Class<?> type, String field) {
			this.type = type;
			this.field = field;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			Meta meta = (Meta) o;

			if (!type.equals(meta.type)) return false;
			return field != null ? field.equals(meta.field) : meta.field == null;
		}

		@Override
		public int hashCode() {
			int result = type.hashCode();
			result = 31 * result + (field != null ? field.hashCode() : 0);
			return result;
		}
	}
}
