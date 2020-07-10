package com.jayyin.jaytomcat.catalina;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.net.BioEndpoint;
import com.jayyin.jaytomcat.net.NioEndpoint;
import com.jayyin.jaytomcat.util.Constant;
import com.jayyin.jaytomcat.util.ThreadPoolUtil;
import com.jayyin.jaytomcat.util.WebXMLUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 端口连接点，一个端口对应一个Connector
 *      监听客户端请求，处理客户端请求的入口
 */
public class Connector {
    private int port;
    private Service service;
    private String mode;  // io模式 BIO NIO
    //
    public Connector(Service service) {
        this.service = service;
    }

    // 启动服务
    public void start() {
        // bio
        if (mode.equals("BIO")) {
            try {
                 ServerSocket ss = new ServerSocket(port);
                 new BioEndpoint(this, ss).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // nio
        else if (mode.equals("NIO")) {
            new NioEndpoint(this).start();
        }
    }

    // 初始化
    public void init() {
        //
        LogFactory.get().info("Initializing Connector {} - mode:{}", port, mode);
    }


    //
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Service getService() {
        return service;
    }
}
