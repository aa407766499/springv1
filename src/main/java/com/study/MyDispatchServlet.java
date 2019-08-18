package com.study;


import cn.hutool.core.collection.CollectionUtil;
import com.study.annotation.*;
import com.study.springv1.convert.ConverterStrategy;
import org.apache.commons.lang3.StringUtils;

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
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Huzi114
 * @ClassName: MyDispatchServlet
 * @Description:
 * @date 2019/8/13 14:47
 */
public class MyDispatchServlet extends HttpServlet {

    //配置信息
    private Properties contextConfig = new Properties();

    //ioc容器
    private Map<String, Object> ioc = new HashMap<String, Object>();

    //类名列表
    private List<String> classNames = new ArrayList<>();

    //处理器映射
    private List<Handler> handlerMapping = new ArrayList<>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        Handler handler = getHandler(req);
        if (handler == null) {
        }
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, Integer> paramIndexMapping = handler.getParamIndexMapping();
        Parameter[] parameters = handler.getMethod().getParameters();
        Object[] paramValues = new Object[parameters.length];
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
            if (!paramIndexMapping.containsKey(entry.getKey())) {
                continue;
            }
            Integer index = paramIndexMapping.get(entry.getKey());
            paramValues[index] = ConverterStrategy.getConverter(parameters[index].getType()).convert(value);
        }
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getType() == HttpServletRequest.class) {
                paramValues[i] = req;
                continue;
            }
            if (parameters[i].getType() == HttpServletResponse.class) {
                paramValues[i] = resp;
            }
        }
        handler.handle(paramValues);
    }

    private Handler getHandler(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        uri = uri.replaceAll(contextPath, "").replaceAll("/+", "/");
        for (Handler handler : handlerMapping) {
            Matcher matcher = handler.getPattern().matcher(uri);
            if (matcher.matches()) {
                return handler;
            }
        }
        return null;
    }

    @Override
    public void init(ServletConfig config) {

        //加载配置信息
        doLoadContextConfig(config.getInitParameter("contextConfigLocation"));

        //扫描类文件
        doScanner(contextConfig.getProperty("scanPackage"));

        //初始化ioc，为DI做准备
        doInstance(classNames);

        //进行依赖注入
        doAutowired();

        //初始化HandlerMapping
        initHandlerMapping();

    }

    private void doLoadContextConfig(String contextConfigLocation) {
        if (StringUtils.isBlank(contextConfigLocation)) {
            return;
        }
        String contextConfigFile = contextConfigLocation.replaceAll("classpath:", "").trim();
        InputStream is = this.getClass().getResourceAsStream("/" + contextConfigFile);
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String scanPackage) {
        if (StringUtils.isBlank(scanPackage)) {
            return;
        }
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classFile = new File(url.getFile());
        for (File file : classFile.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            }
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            String className = scanPackage + "." + file.getName().replaceAll(".class", "");
            classNames.add(className);
        }
    }

    private void doInstance(List<String> classNames) {
        if (CollectionUtil.isEmpty(classNames)) {
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(MyService.class)) {
                    MyService myService = clazz.getAnnotation(MyService.class);
                    String beanName = myService.value();
                    if (beanName.trim().equals("")) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    ioc.put(beanName, clazz.newInstance());
                    for (Class<?> anInterface : clazz.getInterfaces()) {
                        if (ioc.containsKey(anInterface.getName())) {
                            continue;
                        }
                        ioc.put(anInterface.getName(), clazz.newInstance());
                    }
                }
                if (clazz.isAnnotationPresent(MyController.class)) {
                    MyController myController = clazz.getAnnotation(MyController.class);
                    String beanName = myController.value();
                    if (beanName.trim().equals("")) {
                        beanName = toLowerFirstCase(clazz.getSimpleName());
                    }
                    ioc.put(beanName, clazz.newInstance());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String toLowerFirstCase(String className) {
        char[] charArray = className.toCharArray();
        charArray[0] = (char) (charArray[0] + 32);
        return String.valueOf(charArray);
    }

    private void doAutowired() {
        if (CollectionUtil.isEmpty(ioc)) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(MyController.class)) {
                continue;
            }
            for (Field field : aClass.getDeclaredFields()) {
                if (!field.isAnnotationPresent(MyAutowired.class)) {
                    continue;
                }
                MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                String value = myAutowired.value();
                if (StringUtils.isBlank(value)) {
                    value = field.getType().getName();
                }
                field.setAccessible(true);
                try {
                    field.set(entry.getValue(), ioc.get(value));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void initHandlerMapping() {
        if (CollectionUtil.isEmpty(ioc)) {
            return;
        }
        for (Map.Entry<String, Object> entry : ioc.entrySet()) {
            Class<?> aClass = entry.getValue().getClass();
            if (!aClass.isAnnotationPresent(MyController.class)) {
                continue;
            }
            String url = "";
            if (aClass.isAnnotationPresent(MyRequestMapping.class)) {
                MyRequestMapping myRequestMapping = aClass.getAnnotation(MyRequestMapping.class);
                url = myRequestMapping.value();
            }
            for (Method method : aClass.getMethods()) {
                if (!method.isAnnotationPresent(MyRequestMapping.class)) {
                    continue;
                }
                MyRequestMapping myRequestMapping = method.getAnnotation(MyRequestMapping.class);
                String value = myRequestMapping.value();
                Pattern pattern = Pattern.compile(("/" + url + "/" + value).replaceAll("/+", "/"));
                Handler handler = new Handler(entry.getValue(), method, pattern);
                handlerMapping.add(handler);
            }
        }
    }

    private class Handler {

        private Object controller;

        private Method method;

        private Pattern pattern;

        private Map<String, Integer> paramIndexMapping;

        public Handler(Object controller, Method method, Pattern pattern) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            paramIndexMapping = new HashMap<>();
            putParamIndexMapping();
        }

        public void putParamIndexMapping() {
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                if (!parameters[i].isAnnotationPresent(MyRequestParam.class)) {
                    continue;
                }
                MyRequestParam myRequestParam = parameters[i].getAnnotation(MyRequestParam.class);
                String value = myRequestParam.value();
                if (!value.equals("")) {
                    paramIndexMapping.put(value, i);
                }
            }
        }

        public void handle(Object[] paramValues) {
            try {
                method.invoke(controller, paramValues);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        public Object getController() {
            return controller;
        }

        public void setController(Object controller) {
            this.controller = controller;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        public Map<String, Integer> getParamIndexMapping() {
            return paramIndexMapping;
        }

        public void setParamIndexMapping(Map<String, Integer> paramIndexMapping) {
            this.paramIndexMapping = paramIndexMapping;
        }

    }

}
