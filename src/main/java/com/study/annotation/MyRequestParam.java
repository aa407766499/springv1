package com.study.annotation;

import java.lang.annotation.*;

/**
 * @author Huzi114
 * @ClassName: MyRequestParam
 * @Description:
 * @date 2019/8/13 14:56
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRequestParam {
    String value() default "";
}
