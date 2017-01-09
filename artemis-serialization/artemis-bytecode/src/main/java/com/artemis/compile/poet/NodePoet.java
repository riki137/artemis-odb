package com.artemis.compile.poet;

import com.artemis.compile.Node;
import com.artemis.compile.SymbolTable;

import java.util.HashMap;
import java.util.Map;

public class NodePoet {
	private Map<SymbolTable.Entry, NodeStatement> writers = new HashMap<>();

	public interface NodeStatement {
		void write(Node node);
	}
}
