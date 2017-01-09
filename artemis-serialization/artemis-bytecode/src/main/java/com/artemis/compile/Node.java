package com.artemis.compile;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Data structure for mapping json into an intermediate
 * data structure, linking the hierarchy back to the original
 * java types.</p>
 */
public final class Node {
	public final Meta meta;
	public final Object payload;
	private final List<Node> children = new ArrayList<>();

	Node(Class<?> type, String field, Object payload) {
		meta = new Meta(type, field);
		this.payload = payload;
	}

	public List<Node> children() {
		return children;
	}

	public boolean add(Node node) {
		return children.add(node);
	}

	public static Node node(Class<?> type,
	                        Node... children) {

		Node n = new Node(type, null, null);
		for (Node child : children)
			n.children.add(child);

		return n;
	}

	public static Node node(Class<?> type,
	                        String field,
	                        Node... children) {

		return node(type, field, null, children);
	}

	public static Node node(Class<?> type,
	                        String field,
	                        Object payload,
	                        Node... children) {

		Node n = new Node(type, field, payload);
		for (Node child : children)
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

		Node node = (Node) o;

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
		for (Node child : children)
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
