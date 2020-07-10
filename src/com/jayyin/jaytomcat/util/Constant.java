package com.jayyin.jaytomcat.util;

import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * 保存一些常量
 */
public class Constant {
    // 响应码
    public static final int CODE_200 = 200;
    public static final int CODE_302 = 302;
    public static final int CODE_404 = 404;
    public static final int CODE_500 = 500;

    // response响应 首部信息格式化模板
    // 200
    public final static String response_head_200 = "HTTP/1.1 200 OK\r\n" +
                                                    "Content-Type: {}{}" + "\r\n\r\n";;
    // 302
    public final static String response_head_302 = "HTTP/1.1 302 Moved Temporarily\r\n" +
                                                    "Location: {}" + "\r\n\r\n";
    // 404
    public final static String response_head_404 = "HTTP/1.1 404 Not Found\r\n" +
                                                    "Content-Type: text/html" + "\r\n\r\n";;
    // 500
    public final static String response_head_500 = "HTTP/1.1 500 Internal Server Error\r\n" +
                                                    "Content-Type: text/html" + "\r\n\r\n";;

    // 依赖
    // /lib
    public final static File libFolder = new File(SystemUtil.get("user.dir"), "lib");

    // 项目目录
    // /webapps
    public final static File webappsFolder = new File(SystemUtil.get("user.dir"), "webapps");
    // ROOT项目 /webapps/ROOT
    public final static File rootFolder = new File(webappsFolder, "ROOT");

    // 配置文件目录
    // /conf
    public final static File confFolder = new File(SystemUtil.get("user.dir"), "conf");
    // 服务配置文件 /conf/server.xml
    public final static File serverXmlFile = new File(confFolder, "server.xml");
    // 保存默认配置的文件 /conf/web.xml
    public final static File webXmlFile = new File(confFolder, "web.xml");
    // 项目配置文件 /conf/context.xml
    public static final File contextXmlFile = new File(confFolder, "context.xml");

    // jsp编译文件目录
    public final static String workFolder = SystemUtil.get("user.dir") + File.separator + "work";

    // 默认项目
    public final static String defaultWebPath = "/ROOT";
}
