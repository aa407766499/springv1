package com.study.springv1.convert;

/**
 * Created by ASUS on 2019/8/18.
 */
public interface Converter<T> {

    T convert(String value);

}
