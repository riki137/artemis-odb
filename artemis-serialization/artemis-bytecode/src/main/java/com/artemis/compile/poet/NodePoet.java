package com.artemis.compile.poet;

import com.artemis.compile.Node;
import com.artemis.compile.NodeFactory;
import com.artemis.compile.SymbolTable;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;

import java.util.HashMap;
import java.util.Map;

public class NodePoet {
	private Map<SymbolTable.Entry, NodeStatement> writers = new HashMap<>();
	public NodePoet() {
//		writers.put(float.class, new NodeStatement() {
//			@Override
//			public CodeBlock write(Node node) {
////				FieldSpec./
//				return null;
//			}
//		});
	}

	public String write(Node node) {


		return null;
	}

	public static class NodeWriter {
		public final Class<?> type;

		public NodeWriter(Class<?> type) {
			this.type = type;
		}

		public void write(Node node) {

		}
	}


//	public interface NodeWriter {
//		void write(Node node);
//	}

	public interface NodeStatement {
		CodeBlock write(Node node);
	}
}
