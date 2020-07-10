package com.jayyin.jaytomcat.classloader;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * web项目类加载器
 *
 * 创建时
 *      加载web应用的jar依赖，加载项目.class文件
 */
public class WebappClassLoader extends URLClassLoader {
    public WebappClassLoader(String docBase, ClassLoader commonClassLoader) {
        super(new URL[] {}, commonClassLoader);
        try {
            File webInfFolder = new File(docBase, "WEB-INF");
            File classesFolder = new File(webInfFolder, "classes");
            File libFolder = new File(webInfFolder, "lib");
            // classes
            // 注意url的格式，目录以 / 结尾
            URL url = new URL("file:" + classesFolder.getAbsolutePath() + "/");
            this.addURL(url);
            // lib
            List<File> jarFiles = FileUtil.loopFiles(libFolder);
            for (File file : jarFiles) {
                url = new URL("file:" + file.getAbsolutePath());
                this.addURL(url);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭资源
    public void stop() {
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
