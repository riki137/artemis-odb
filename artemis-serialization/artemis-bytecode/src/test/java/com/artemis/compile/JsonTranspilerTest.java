package com.artemis.compile;

import com.badlogic.gdx.utils.*;
import org.junit.Ignore;
import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

public class JsonTranspilerTest {
	@Test
	public void test_register_components() throws Exception {
		InputStream in = JsonTranspilerTest.class.getResourceAsStream("/prefab/some_prefab.json");
		JsonValue json = new JsonReader().parse(in);

		JsonTranspiler transpiler = new JsonTranspiler();
		transpiler.compile(json);

		fail("not impl");
	}
}