package com.jayyin.jaytomcat;

import com.jayyin.jaytomcat.classloader.CommonClassLoader;

import java.lang.reflect.Method;

/**
 *
 * 引导程序（不依赖第三方包）
 * 与Server解耦
 */
public class Bootstrap {

    public static void main(String[] args) throws Exception {
        // 新建公共加载器，设置为上下文加载器
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        Thread.currentThread().setContextClassLoader(commonClassLoader);

        // 加载器实例化Server，启动服务（需要将项目源代码的jar包放入lib目录中才能加载到）
        String serverClassName = "com.jayyin.jaytomcat.catalina.Server";
        Class<?> serverClazz = commonClassLoader.loadClass(serverClassName);
        Object server = serverClazz.newInstance();
        Method method = serverClazz.getMethod("start");
        method.invoke(server);

        // System.out.println(serverClazz + ": " + serverClazz.getClassLoader());
    }

}
