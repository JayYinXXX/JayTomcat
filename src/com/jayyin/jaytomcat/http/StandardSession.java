package com.jayyin.jaytomcat.http;

import sun.awt.SunHints;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * session
 */
public class StandardSession implements HttpSession {
    private String id;  // 唯一id
    private ServletContext servletContext;
    private long creationTime;  // 创建时间
    private long lastAccessedTime;  // 最后一次访问时间
    private int maxInactiveInterval;  // 最大持续时间（min）
    private Map<String, Object> attributesMap;
    //
    public StandardSession(String jsessionid, ServletContext servletContext) {
        this.id = jsessionid;
        this.creationTime = System.currentTimeMillis();
        this.lastAccessedTime = creationTime;
        this.attributesMap = new HashMap<>();
        this.servletContext = servletContext;
    }

    @Override
    public long getCreationTime() {
        return creationTime;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    @Override
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    @Override
    public Object getAttribute(String name) {
        return attributesMap.get(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(attributesMap.keySet());
    }

    @Override
    public void setAttribute(String name, Object value) {
        attributesMap.put(name, value);
    }


    @Override
    public void removeAttribute(String name) {
        attributesMap.remove(name);
    }

    // 清除属性
    @Override
    public void invalidate() {
        attributesMap.clear();
    }

    @Override
    public boolean isNew() {
        return creationTime == lastAccessedTime;
    }


    // 弃用
    @Override
    public void removeValue(String s) {

    }
    @Override
    public void putValue(String s, Object o) {

    }
    @Override
    public String[] getValueNames() {
        return new String[0];
    }
    @Override
    public Object getValue(String s) {
        return null;
    }
    @Override
    public HttpSessionContext getSessionContext() {
        return null;
    }
}
