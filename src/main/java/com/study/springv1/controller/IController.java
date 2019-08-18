package com.study.springv1.controller;

import com.study.annotation.MyAutowired;
import com.study.annotation.MyController;
import com.study.annotation.MyRequestMapping;
import com.study.annotation.MyRequestParam;
import com.study.springv1.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Huzi114
 * @ClassName: IController
 * @Description:
 * @date 2019/8/13 15:01
 */
@MyController
@MyRequestMapping("test")
public class IController {

    @MyAutowired
    private IService iService;

    @MyRequestMapping("invokeService")
    public void invokeService(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("name") String name) {
        String s = iService.printName(name);
        try {
            response.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @MyRequestMapping("add")
    public void add(HttpServletRequest request, HttpServletResponse response, @MyRequestParam("a") Integer a, @MyRequestParam("b") Integer b) {
        Integer c = a + b;
        try {
            response.getWriter().write(""+c.intValue());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
