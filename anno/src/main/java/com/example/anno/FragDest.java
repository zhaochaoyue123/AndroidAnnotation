package com.example.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * init component
 * Created by zhaochaoyue on 2020/06/09.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Inherited
public @interface FragDest {
    String pageUri();
    boolean isStarter() default false;
}
