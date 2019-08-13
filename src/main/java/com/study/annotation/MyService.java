package com.study.annotation;

import java.lang.annotation.*;

/**
 * @author Huzi114
 * @ClassName: MyService
 * @Description:
 * @date 2019/8/13 14:53
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyService {
    String value() default "";
}
