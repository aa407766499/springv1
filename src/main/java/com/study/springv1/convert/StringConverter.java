package com.study.springv1.convert;

/**
 * Created by ASUS on 2019/8/18.
 */
public class StringConverter implements Converter<String> {

    @Override
    public String convert(String value) {
        return value;
    }

}
