package com.study;


import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.net.URL;
import java.util.*;

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        this.doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
    }

    @Override
    public void init(ServletConfig config) {

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
        }
    }

}
