package com.study;

import com.study.annotation.MyAutowired;
import com.study.annotation.MyController;
import com.study.annotation.MyRequestMapping;
import com.study.annotation.MyService;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Huzi114
 * @ClassName: MyDispatchServlet
 * @Description:
 * @date 2019/8/13 14:47
 */
public class MyDispatchServlet extends HttpServlet {

    private Map<String, Object> mappings = new HashMap<String, Object>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        Map<String, String[]> parameterMap = req.getParameterMap();
        String url = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
        Method method = (Method) mappings.get(url);
        try {
            method.invoke(mappings.get(method.getDeclaringClass().getName()), req, resp, parameterMap.get("name")[0]);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init(ServletConfig config) {
        InputStream is = null;
        is = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
        Properties contextConfig = new Properties();
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String scanPackage = contextConfig.getProperty("scanPackage");
        doScan(scanPackage);
        for (String className : mappings.keySet()) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService myService = clazz.getAnnotation(MyService.class);
                    String beanName = myService.value();
                    if (beanName == "") {
                        beanName = clazz.getName();
                        mappings.put(beanName, clazz.newInstance());
                    }
                    mappings.put(beanName, clazz.newInstance());
                    for (Class<?> aClass : clazz.getInterfaces()) {
                        mappings.put(aClass.getName(), clazz.newInstance());
                    }
                }
                if (clazz.isAnnotationPresent(MyController.class)) {
                    MyController myController = clazz.getAnnotation(MyController.class);
                    String beanName = myController.value();
                    if (beanName == "") {
                        beanName = clazz.getName();
                        mappings.put(beanName, clazz.newInstance());
                    }
                    mappings.put(beanName, clazz.newInstance());
                    for (Field field : clazz.getDeclaredFields()) {
                        if (!field.isAnnotationPresent(MyAutowired.class)) {
                            continue;
                        }
                        MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                        String name = myAutowired.value();
                        if (name == "") {
                            Object instance = mappings.get(field.getType().getName());
                        }
                        Object instance = mappings.get(name);
                        field.setAccessible(true);
                        field.set(mappings.get(beanName),instance);
                    }
                    String baseUrl = "";
                    if (clazz.isAnnotationPresent(MyRequestMapping.class)) {
                        MyRequestMapping classMapping = clazz.getAnnotation(MyRequestMapping.class);
                        baseUrl = ("/"+classMapping.value()).replaceAll("/+","/");
                    }
                    for (Method method : clazz.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(MyRequestMapping.class)) {
                            MyRequestMapping methodMapping = method.getAnnotation(MyRequestMapping.class);
                            baseUrl = (baseUrl + "/" + methodMapping.value()).replaceAll("/+", "/");
                        }
                        mappings.put(baseUrl, method);
                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    private void doScan(String scanPackage) {
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classFile = new File(url.getFile());
        for (File file : classFile.listFiles()) {
            if (file.isDirectory()) {
                doScan(scanPackage + "." + file.getName());
            }
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            String className = scanPackage + "." + file.getName().replaceAll(".class", "");
            mappings.put(className, null);
        }
    }

}
