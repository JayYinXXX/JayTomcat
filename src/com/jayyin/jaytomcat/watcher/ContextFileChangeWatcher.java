package com.jayyin.jaytomcat.watcher;

import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.catalina.Context;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * 包装监听器
 *      监听web应用文件的改变
 */
public class ContextFileChangeWatcher {
    private WatchMonitor monitor;  // 监听器
    private boolean stop = false;  // 监听标志
    //
    public ContextFileChangeWatcher(Context context) {
        // 新建监听器
        this.monitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {

            private void dealWith(WatchEvent<?> event) {
                // 加锁
                synchronized(ContextFileChangeWatcher.class) {
                    String fileName = event.context().toString();  // 触发事件文件名
                    if (stop) {
                        return;
                    }
                    if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".xml")) {
                        stop = true;
                        LogFactory.get().info(ContextFileChangeWatcher.this + " 检测到文件改动：{}", fileName);
                        context.reload();
                    }
                }
            }
            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }
        });
        // 设置守护线程
        this.monitor.setDaemon(true);
    }

    public void start() {
        monitor.start();
    }

    public void stop() {
        monitor.close();
    }
}
