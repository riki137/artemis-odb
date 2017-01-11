package com.artemis.annotations;

import java.lang.annotation.*;


/**
 * Holds the path or identifier for <code>Prefab</code> types.
 * The value from this annotation is passed to the
 * corresponding <code>PrefabReader</code>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface PrefabData {
	String value();

	/**
	 * Internal marker annotation on {@code Prefabs}, set by the
	 * artemis-odb-plugin. The presence of this annotation on a
	 * prefab implies that the original json has been compiled into
	 * bytecode, eliminating the overhead from reflection and json
	 * parsing.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@interface Compiled {
		Class<?> value();
	}
}
