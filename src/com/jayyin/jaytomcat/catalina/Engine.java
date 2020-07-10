package com.jayyin.jaytomcat.catalina;

import com.jayyin.jaytomcat.util.ServerXMLUtil;

import java.util.List;

/**
 * 引擎
 */
public class Engine {
    private Host defaultHost;  // 默认host
    private List<Host> hosts;  // 所有host
    //
    public Engine() {
        this.defaultHost = ServerXMLUtil.getDefaultHost();
        this.hosts = ServerXMLUtil.getHosts();
    }


    //
    public Host getDefaultHost() {
        return defaultHost;
    }

    public List<Host> getHosts() {
        return hosts;
    }
}
