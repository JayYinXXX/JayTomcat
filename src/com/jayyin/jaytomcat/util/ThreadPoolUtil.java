package com.jayyin.jaytomcat.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 封装线程池
 */
public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS,
                                                        new LinkedBlockingQueue<Runnable>(10));

    // 加入任务
    public static void run(Runnable r) {
        threadPool.execute(r);
    }
}
