package com.study.springv1.convert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ASUS on 2019/8/18.
 */
public class ConverterStrategy {

    private static Map<String, Converter> converterMap = new HashMap<>();

    static {
        converterMap.put(Integer.class.getName(), new IntegerConverter());
        converterMap.put(Double.class.getName(), new DoubleConverter());
        converterMap.put(String.class.getName(), new StringConverter());
    }
    private ConverterStrategy() {
    }

    public static Converter getConverter(Class<?> type) {
        return converterMap.get(type.getName());
    }

}
