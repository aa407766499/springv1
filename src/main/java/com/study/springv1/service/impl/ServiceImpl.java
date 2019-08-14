package com.study.springv1.service.impl;

import com.study.annotation.MyService;
import com.study.springv1.service.IService;

/**
 * @author Huzi114
 * @ClassName: ServiceImpl
 * @Description:
 * @date 2019/8/13 15:01
 */
@MyService
public class ServiceImpl implements IService {

    public String printName(String name) {
        return name;
    }

}
