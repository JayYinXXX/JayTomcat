package com.jayyin.jaytomcat.classloader;

import com.jayyin.jaytomcat.util.Constant;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * 公共类加载器（不依赖第三方包）
 */
public class CommonClassLoader extends URLClassLoader {
    public CommonClassLoader() {
        super(new URL[] {});
        try {
            // 扫描/lib中的依赖
            // File[] jarFiles = Constant.libFolder.listFiles();
            File workingFolder = new File(System.getProperty("user.dir"));
            File libFolder = new File(workingFolder, "lib");
            File[] jarFiles = libFolder.listFiles();

            if (jarFiles != null) {
                for (File file : jarFiles) {
                    if (file.getName().endsWith("jar")) {
                        URL url = new URL("file:" + file.getAbsolutePath());
                        this.addURL(url);
                    }
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
