package com.artemis.compile;

import com.artemis.component.*;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class SymbolTableTest {
	@Test
	public void test_register_type() {
		SymbolTable symbols = new SymbolTable();
		symbols.register(PrimitiveComponent.class);

		Class<?> type = PrimitiveComponent.class;
		for (Field f : type.getDeclaredFields()) {
			if (SymbolTable.isValid(f)) {
				assertNotNull(symbols.lookup(type, f.getName()));
			}
		}
	}
}