package com.study;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        InputStream is;
        is = this.getClass().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
        Properties contextConfig = new Properties();
        try {
            contextConfig.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String scanPackage = contextConfig.getProperty("scanPackage");
    }

}
