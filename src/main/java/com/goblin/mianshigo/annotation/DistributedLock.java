package com.goblin.mianshigo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    /**
     * 锁名称
     * @return
     */
    String key() ;

    /**
     * 持锁时间，默认30秒
     * @return
     */
    long leaseTime() default 30000;

    /**
     * 等待时间，默认10秒
     * @return
     */
    long waitTime() default 10000;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;
}
