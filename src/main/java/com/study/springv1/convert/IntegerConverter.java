package com.study.springv1.convert;

/**
 * Created by ASUS on 2019/8/18.
 */
public class IntegerConverter implements Converter<Integer> {

    @Override
    public Integer convert(String value) {
        return Integer.valueOf(value);
    }

}
