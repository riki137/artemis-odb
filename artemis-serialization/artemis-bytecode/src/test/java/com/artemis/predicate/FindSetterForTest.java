package com.artemis.predicate;

import com.artemis.compile.SymbolTableOld;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FindSetterForTest {
	private SymbolTableOld symbols;

	@Before
	public void init() {
		symbols = new SymbolTableOld() {};
	}

	@Test
	public void testSimpleSetters() throws Exception {
		symbols.register(FooA.class);

		FindSetterFor x = new FindSetterFor(symbols.lookup(FooA.class, "x"));
		assertTrue(x.apply(FooA.class.getDeclaredMethod("setX", int.class)));

		FindSetterFor y = new FindSetterFor(symbols.lookup(FooA.class, "y"));
		assertFalse(y.apply(FooA.class.getDeclaredMethod("setY", int.class)));

		FindSetterFor z = new FindSetterFor(symbols.lookup(FooA.class, "z"));
		assertTrue(z.apply(FooA.class.getDeclaredMethod("z", int.class)));
	}

	public static class FooA {
		private int x;
		private int y;
		private int z;

		public void z(int z) {
			this.z = z;
		}

		public void setX(int x) {
			this.x = x;
		}

		private void setY(int y) {
			this.y = y;
		}
	}
}