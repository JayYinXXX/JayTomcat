package com.jayyin.jaytomcat.http;

import com.jayyin.jaytomcat.catalina.Context;

import java.io.File;
import java.util.*;
import org.apache.tomcat.InstanceManager;

/**
 * web应用上下文
 *      web应用全局唯一，用于servlet之间共享公共的参数等
 */
public class ApplicationContext extends BaseServletContext{
    private Map<String, Object> attributesMap;  // 属性集
    private Context context;
    //
    public ApplicationContext(Context context) {
        this.attributesMap = new HashMap<>();
        this.context = context;
    }


    @Override
    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributesMap.put(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        attributesMap.remove(name);
    }

    // 根据相对路径获取绝对路径
    @Override
    public String getRealPath(String path) {
        return new File(context.getDocBase(), path).getAbsolutePath();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> keys = attributesMap.keySet();
        return Collections.enumeration(keys);
    }

}
