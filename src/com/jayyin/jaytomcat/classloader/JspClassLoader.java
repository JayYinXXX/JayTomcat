package com.jayyin.jaytomcat.classloader;

import cn.hutool.core.util.StrUtil;
import com.jayyin.jaytomcat.catalina.Context;
import com.jayyin.jaytomcat.util.Constant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

/**
 * jsp类加载器
 *      一个.jsp文件对应一个加载器（以其对应的web项目类加载器为父加载器）
 */
public class JspClassLoader extends URLClassLoader {
    // jsp-加载器映射表，一个jsp文件对应一个加载器
    private static Map<String, JspClassLoader> map = new HashMap<>();
    //
    public JspClassLoader(Context context) {
        // 指定加载范围和父加载器
        super(new URL[]{}, context.getWebappClassLoader());

        try {
            String subFolder = StrUtil.subAfter(context.getPath(), "/", false);
            File folder = new File(Constant.workFolder, subFolder);
            URL url = null;
            url = new URL("file:" + folder.getAbsolutePath() + "/");
            this.addURL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    // 移除指定加载器
    public static void removeJspClassLoader(String uri, Context context) {
        String name = context.getPath() + "/" + uri;
        map.remove(name);
    }

    // 获取加载器
    public static JspClassLoader getJspClassLoader(String uri, Context context) {
        String name = context.getPath() + "/" + uri;
        JspClassLoader result = map.get(name);
        if (result == null) {
            result = new JspClassLoader(context);
            map.put(name, result);
        }
        return result;
    }

}
