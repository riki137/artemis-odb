package com.artemis.compile;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public final class Node {
	public final Class<?> type;
	public final String field;
	public final Object payload;
	private final List<Node> children = new ArrayList<>();

	public Node(Class<?> type, String field, Object payload) {
		this.type = type;
		this.field = field;
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
		if (payload instanceof Integer)
			return payload;
		if (type == Character.class || type == char.class)
			return new Integer(((Character) payload).charValue());
		if (type == Short.class || type == Byte.class)
			return new Integer((int) payload);
		if (type == short.class || type == byte.class)
			return new Integer((int) payload);

		return payload;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Node node = (Node) o;

		if (!type.equals(node.type))
			return false;

		if (field != null ? !field.equals(node.field) : node.field != null)
			return false;

		if (!children.equals(node.children))
			return false;

		return payload != null
			? widenPayload().equals(node.widenPayload())
			: node.payload == null;
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + (field != null ? field.hashCode() : 0);
		result = 31 * result + children.hashCode();
		result = 31 * result + (payload != null ? payload.hashCode() : 0);
		return result;
	}

	private StringBuilder toStringBuilder(StringBuilder sb, String prepend) {
		String format =
			(field == null && payload == null) ? "(%2$s)" :
			(field == null && payload != null) ? "(%2$-10s %3$s)" :
		    (field != null && payload != null) ? "(%-10s %-10s %s)"
		                                       : "(%-10s %s)";

		format = prepend + format + '\n';
		sb.append(format(format, field, type.getSimpleName(), payload));

		prepend += "    ";
		for (Node child : children)
			child.toStringBuilder(sb, prepend);

		return sb;
	}

	@Override
	public String toString() {
		return toStringBuilder(new StringBuilder(), "").toString();
	}
}
