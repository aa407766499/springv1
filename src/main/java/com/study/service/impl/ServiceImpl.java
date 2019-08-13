package com.study.service.impl;

import com.study.annotation.MyService;
import com.study.service.IService;

/**
 * @author Huzi114
 * @ClassName: ServiceImpl
 * @Description:
 * @date 2019/8/13 15:01
 */
@MyService
public class ServiceImpl implements IService {
    public String getName() {
        return "hello";
    }
}
