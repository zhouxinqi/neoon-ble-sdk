package com.neoon.blesdk.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作者:东芝(2019/12/27).
 * 功能:
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.ANNOTATION_TYPE})
public @interface IntDef {
	int[] value() default {};

	boolean flag() default false;

	boolean open() default false;
}
