package com.study.annotation;

import java.lang.annotation.*;

/**
 * @author Huzi114
 * @ClassName: MyController
 * @Description:
 * @date 2019/8/13 14:54
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyController {
    String value() default "";
}
