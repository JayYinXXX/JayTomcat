package com.jayyin.jaytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.util.ServerXMLUtil;

import java.util.List;

/**
 * 服务
 */
public class Service {
    private String name;
    private Engine engine;
    private List<Connector> connectors;
    //
    public Service() {
        this.name = ServerXMLUtil.getServiceName();
        this.engine = new Engine();
        this.connectors = ServerXMLUtil.getConnectors(this);
    }


    // 启动所有连接点的服务
    public void start() {
        init();
    }

    // 初始化
    public void init() {
        TimeInterval timeInterval = DateUtil.timer();
        for (Connector c : connectors) {
            c.init();
        }
        LogFactory.get().info("Initialization processed in {}ms", timeInterval.intervalMs());

        for (Connector c : connectors) {
            c.start();
        }
    }

    //
    public String getName() {
        return name;
    }

    public Engine getEngine() {
        return engine;
    }
}
