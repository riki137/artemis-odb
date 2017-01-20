package com.artemis.component;

import com.artemis.Component;
import com.artemis.compile.NodeOld;
import com.badlogic.gdx.utils.IntArray;

import static com.artemis.compile.NodeOld.node;

public class ArrayComponent extends Component {
	public IntArray intArray = new IntArray();
	public int[] nakedIntArray = { 1, 2, 4, 5 };

	{
		intArray.add(10);
		intArray.add(20);
		intArray.add(30);
	}

	public static NodeOld expected =
		node(ArrayComponent.class,
			node(IntArray.class,  "intArray",
				node(int.class, null, 1),
				node(int.class, null, 2),
				node(int.class, null, 3),
				node(int.class, null, 4),
				node(int.class, null, 5)),
			node(IntArray.class,  "nakedIntArray",
				node(int.class, null, 10),
				node(int.class, null, 20),
				node(int.class, null, 30)));
}
