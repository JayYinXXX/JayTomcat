package com.jayyin.jaytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Server {
    private Service service;
    //
    public Server() {
        this.service = new Service();
    }


    // 启动服务器
    public void start() {
        TimeInterval timeInterval = DateUtil.timer();
        // logJVM();
        init();
        LogFactory.get().info("Server startup in {}ms", timeInterval.intervalMs());
    }

    // 初始化
    private void init() {
        service.start();
    }



    // JVM信息日志
    private static void logJVM() {
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version", "JayTomcat/1.0.1");
        infos.put("Server built", "2020-06-04 22:20:22");
        infos.put("Server number", "1.0.1");
        infos.put("OS Name", SystemUtil.get("os.name"));
        infos.put("OS Version", SystemUtil.get("os.version"));
        infos.put("Architecture", SystemUtil.get("os.arch"));
        infos.put("Java Home", SystemUtil.get("java.home"));
        infos.put("JVM Version", SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor", SystemUtil.get("java.vm.specification.vendor"));

        Set<String> keys = infos.keySet();
        for (String key : keys) {
            LogFactory.get().info(key + ":\t" + infos.get(key));
        }
    }
}
