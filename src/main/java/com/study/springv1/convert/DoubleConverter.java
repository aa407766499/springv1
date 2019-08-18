package com.study.springv1.convert;

/**
 * Created by ASUS on 2019/8/18.
 */
public class DoubleConverter implements Converter<Double> {

    @Override
    public Double convert(String value) {
        return Double.valueOf(value);
    }

}
