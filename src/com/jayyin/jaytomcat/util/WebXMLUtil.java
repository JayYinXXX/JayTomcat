package com.jayyin.jaytomcat.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import com.jayyin.jaytomcat.catalina.Context;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.lang.annotation.Documented;
import java.util.HashMap;
import java.util.Map;

/**
 * /conf/web.xml配置文件解析工具
 */
public class WebXMLUtil {
    // Content-Type参数映射关系
    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    // 初始化MimeType映射
    public static void initMimeTypeMapping() {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("mime-mapping");
        for (Element e : es) {
            String key = e.select("extension").text();
            String value = e.select("mime-type").text();
            mimeTypeMapping.put(key, value);
        }
    }

    // 获取MimeType
    public static synchronized String getMimeType(String extension) {
        // 第一次使用初始化
        if (mimeTypeMapping.isEmpty()) {
            initMimeTypeMapping();
        }
        String mimeType = mimeTypeMapping.get(extension);
        // 默认格式
        if (mimeType == null) {
            return "text/html;charset=utf-8;";
        }
        return mimeType;
    }

    // 获取指定项目的默认访问页面名
    public static String getWelcomeFile(Context context) {
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);

        // 扫描默认设置的欢迎页面列表，如果项目中有匹配则直接返回，否则设为index.html
        Elements es = d.select("welcome-file");
        for (Element e : es) {
            String fileName = e.text();
            File file = new File(context.getDocBase(), fileName);
            if (file.exists()) {
                return fileName;
            }
        }
        return "index.html";
    }

    // session配置
    // session超时时间
    public static int getTimeout() {
        int defaultTime = 30;
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("session-config session-timeout");
        if (es.isEmpty()) {
            return defaultTime;
        }
        return Convert.toInt(es.first().text());
    }
}
