package com.study.annotation;

import java.lang.annotation.*;

/**
 * @author Huzi114
 * @ClassName: MyRequestMapping
 * @Description:
 * @date 2019/8/13 14:55
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestMapping {
    String value() default "";
}
