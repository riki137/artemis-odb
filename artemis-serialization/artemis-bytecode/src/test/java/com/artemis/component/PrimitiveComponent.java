package com.artemis.component;

import com.artemis.Component;
import com.artemis.compile.NodeOld;

import static com.artemis.compile.NodeOld.node;

public class PrimitiveComponent extends Component {
	public String text = "zero";
	public long aLong = 1;
	public int aInt = 2;
	public short aShort = 3;
	public char aChar = '4';
	public boolean aBool = true;
	public byte aByte = 6;
	public float aFloat = 7f;
	public double aDouble = 8.;

	public static NodeOld expected =
		node(PrimitiveComponent.class,
			node(String.class,  "text",    "zero"),
			node(long.class,    "aLong",   1L),
			node(int.class,     "aInt",    2),
			node(short.class,   "aShort",  3),
			node(char.class,    "aChar",   '4'),
			node(boolean.class, "aBool",   true),
			node(byte.class,    "aByte",   6),
			node(float.class,   "aFloat",  7f),
			node(double.class,  "aDouble", 8.));
}
