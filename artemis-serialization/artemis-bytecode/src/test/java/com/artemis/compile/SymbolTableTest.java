package com.artemis.compile;

import com.artemis.component.*;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class SymbolTableTest {
	@Test
	public void test_register_type() {
		SymbolTableOld symbols = new SymbolTableOld();
		symbols.register(PrimitiveComponent.class);

		Class<?> type = PrimitiveComponent.class;
		for (Field f : type.getDeclaredFields()) {
			if (SymbolTableOld.isValid(f)) {
				assertNotNull(symbols.lookup(type, f.getName()));
			}
		}
	}
}