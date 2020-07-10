package com.jayyin.jaytomcat.util;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.http.StandardSession;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * session管理
 */
public class SessionManager {
    private static Map<String, StandardSession> sessionMap = new HashMap<>();  // id-session
    private static int defaultTimeout = WebXMLUtil.getTimeout();  // 默认过期时间（min）
    static {
        startSessionOutDateCheckThread();
    }

    private static void startSessionOutDateCheckThread() {
        new Thread() {
            @Override
            public void run() {
                // 循环检测
                while (true) {
                    checkOutDateSession();
                    // 挂起30s
                    try {
                        sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        }.start();
    }

    // 检测session的过期情况，删除过期session
    private static void checkOutDateSession() {
        // 过期的session
        List<String> outDateJsessionids = new ArrayList<>();
        Set<String> jsessionids = sessionMap.keySet();
        for (String jsessionid : jsessionids) {
            StandardSession session = sessionMap.get(jsessionid);
            // 上次访问到现在的时间差
            long interval = System.currentTimeMillis() - session.getLastAccessedTime();
            if (interval > session.getMaxInactiveInterval() * 60 * 1000) {
                outDateJsessionids.add(jsessionid);
            }
        }
        // 移除
        for (String id : outDateJsessionids) {
            sessionMap.remove(id);
        }
    }

    // 新建session
    // 生成id
    public static synchronized String createSessionId() {
        byte[] bytes = RandomUtil.randomBytes(16);
        String result = new String(bytes);
        result = SecureUtil.md5(result).toUpperCase();
        return result;
    }
    // 创建session
    public static HttpSession createSession(Request request) {
        String id = createSessionId();
        ServletContext servletContext = request.getServletContext();
        StandardSession session = new StandardSession(id, servletContext);
        session.setMaxInactiveInterval(defaultTimeout);
        sessionMap.put(id, session);
        return session;
    }
    // 获取session
    public static HttpSession getSession(String jsessionid, Request request) {
        // id为null或者id相应的session已经不存在，则新建session
        if (jsessionid == null) {
            return createSession(request);
        }
        else {
            StandardSession session = sessionMap.get(jsessionid);
            if (session == null) {
                return createSession(request);
            }
            // 成功取得session，更新访问时间
            else {
                session.setLastAccessedTime(System.currentTimeMillis());
                return session;
            }
        }
    }

    // 将session写入cookie
    public static void createCookieBySession(HttpSession session, Request request, Response response) {
        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setMaxAge(session.getMaxInactiveInterval() * 60);
        cookie.setPath(request.getContext().getPath());
        response.addCookie(cookie);
    }
}
