package com.jayyin.jaytomcat.catalina;

import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.util.Constant;
import com.jayyin.jaytomcat.util.ServerXMLUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主机
 *
 * Host的构建将会
 *      1.扫描并注册该Host下所有的项目Context（暂时只有一个主机，所以扫描所有Context）
 */
public class Host {
    private String name;
    private Map<String, Context> contextMap;  // 项目集合
    // 新建Host会扫描所有项目
    public Host(String name) {
        this.name = name;
        this.contextMap = new HashMap<>();
        // 先通过解析xml加载
        scanContextsInServerXML();
        scanContextsInWebapps();
    }


    // 取出指定的Context
    public Context getContext(String path) {
        return contextMap.get(path);
    }

    // 扫描server.xml配置文件加载项目
    private void scanContextsInServerXML() {
        List<Context> contexts = ServerXMLUtil.getContexts(this);
        for (Context context : contexts) {
            contextMap.put(context.getPath(), context);
        }
    }

    // 扫描webapps下的所有项目文件夹并加载
    private void scanContextsInWebapps() {
        File[] folders = Constant.webappsFolder.listFiles();
        for (File folder : folders) {
            if (folder.isDirectory()) {
                loadContext(folder);
            }
        }
    }
    // 通过路径加载web应用
    private void loadContext(File folder) {
        String path = "/" + folder.getName();  // web应用根路径
        // 没有被加载过则加载
        if (contextMap.get(path) == null) {
            String docBase = folder.getAbsolutePath();
            Context context = new Context(path, docBase, false, this);
            contextMap.put(context.getPath(), context);
        }
    }

    // 重新加载Context
    public void reload(Context context) {
        LogFactory.get().info("Reloading Context {} has started", context.getPath());

        String path = context.getPath();
        String docBase = context.getDocBase();
        boolean reloadable = context.isReloadable();
        context.stop();
        contextMap.remove(path);
        // 重新加载
        Context newContext = new Context(path, docBase, reloadable, this);
        contextMap.put(path, newContext);

        LogFactory.get().info("Reloading Context {} has completed", context.getPath());
    }


    //

    public String getName() {
        return name;
    }

}
