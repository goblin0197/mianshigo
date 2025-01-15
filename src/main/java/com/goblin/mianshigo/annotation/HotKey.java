package com.goblin.mianshigo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HotKey {
    /**
     * key名称
     * @return
     */
    String key() default "";

    /**
     * 是否开启前缀
     * @return
     */
    boolean prefix() default false;
}

