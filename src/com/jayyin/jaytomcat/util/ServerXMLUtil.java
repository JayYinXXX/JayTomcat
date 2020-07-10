package com.jayyin.jaytomcat.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.jayyin.jaytomcat.catalina.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * /conf/server.xml配置文件解析工具
 */
public class ServerXMLUtil {
    // Context
    // 解析配置文件中注册的项目，得到一个Context应用列表
    public static List<Context> getContexts(Host host) {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        List<Context> result = new ArrayList<>();
        Elements es = d.select("Context");
        for (Element e : es) {
            String path = e.attr("path");
            String docBase = e.attr("docBase");
            boolean reloadable = "true".equals(e.attr("reloadable"));
            Context context = new Context(path, docBase, reloadable, host);
            result.add(context);
        }
        return result;
    }


    // Host（暂时只有一个Host）
    // 获取所有Host
    public static List<Host> getHosts() {
        List<Host> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Host");
        for (Element e : es) {
            String name = e.attr("name");
            Host host = new Host(name);
            result.add(host);
        }
        return result;
    }


    // Engine（暂时默认只有一个引擎）
    // 获取默认Host（暂时为所有Host中的第一个）
    public static Host getDefaultHost() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Element e = d.select("Engine").first();
        if (e == null) {
            return null;
        }
        Host host = new Host(e.attr("name"));
        return host;
    }


    // Connector
    // 获取所有Connect
    public static List<Connector> getConnectors(Service service) {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Connector");

        List<Connector> result = new ArrayList<>();
        for (Element e : es) {
            int port = Convert.toInt(e.attr("port"));
            String mode = e.attr("mode");
            Connector connector = new Connector(service);
            connector.setPort(port);
            connector.setMode(mode);
            result.add(connector);
        }
        return result;
    }


    // Service（暂时默认一个Service）
    public static String getServiceName() {
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element e = d.select("Service").first();
        return e.attr("name");
    }
}
