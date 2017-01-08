package com.artemis.compile;

import com.artemis.component.ComponentX;
import com.artemis.component.ComponentY;
import com.artemis.component.NameComponent;

public class FunctionSample {
	public static void ComponentX_text(ComponentX c, String s) {
		c.text = s;
	}

	public static void ComponentY_text(ComponentY c, String s) {
		c.text = s;
	}

	public static void NameComponent_text(NameComponent c, String s) {
		c.name = s;
	}
}
