package com.jayyin.jaytomcat.util;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;

/**
 * 封装线程池
 */
public class ThreadPoolUtil {
    private static RejectedPolicy rejectedPolicy = new RejectedPolicy();
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 100, 60, TimeUnit.SECONDS,
                                                        new LinkedBlockingQueue<Runnable>(10), rejectedPolicy);

    // 加入任务
    public static void execute(Runnable r, Socket socket) {
        rejectedPolicy.setSocket(socket);
        threadPool.execute(r);
    }
    public static void execute(Runnable r, SocketChannel socketChannel) {
        rejectedPolicy.setSocketChannel(socketChannel);
        threadPool.execute(r);
    }

    // 拒绝处理程序，开额外的新线程处理超额任务
    private static class RejectedPolicy implements RejectedExecutionHandler {
        private Socket socket;  // BIO
        private SocketChannel socketChannel;  // NIO通道
        //
        RejectedPolicy() {
            super();
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public void setSocketChannel(SocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        // 拒绝处理策略
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            try {
                Thread thread = new Thread(r, "Temporary-task-thread");
                thread.start();
            } catch (Throwable e) {
                try {
                    // 关闭连接
                    if (socket != null) {
                        socket.close();
                    }
                    if (socketChannel != null) {
                        socketChannel.close();
                    }
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                throw new RejectedExecutionException("Failed to start a new thread", e);
            }
        }
    }
}
