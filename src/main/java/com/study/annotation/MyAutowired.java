package com.study.annotation;

import java.lang.annotation.*;

/**
 * @author Huzi114
 * @ClassName: MyAutowired
 * @Description:
 * @date 2019/8/13 15:09
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}
