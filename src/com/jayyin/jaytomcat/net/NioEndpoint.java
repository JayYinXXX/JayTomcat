package com.jayyin.jaytomcat.net;

import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.catalina.Connector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.*;

/**
 * NIO
 */
public class NioEndpoint {
    private final Connector connector;
    private ServerSocketChannel serverSocketChannel;
    private Poller poller;
    private Acceptor acceptor;
    private NioExecutor nioExecutor;
    private boolean mark;
    private BlockingQueue<SocketChannel> events;  // 事件队列
    //
    public NioEndpoint(Connector connector){
        this.connector = connector;
    }


    public void init() {
        try {
            events = new LinkedBlockingQueue<>();

            nioExecutor = new NioExecutor(connector);

            poller = new Poller();

            acceptor = new Acceptor();
            acceptor.init();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        init();

        // 模拟阻塞线程
        Thread blockingThread = new Thread(nioExecutor, "thread-blocking");
        blockingThread.setDaemon(true);
        blockingThread.start();

        // 开启poller线程
        Thread pollerThread = new Thread(poller, "thread-poller");
        pollerThread.setDaemon(true);
        pollerThread.start();

        // 开启acceptor线程
        Thread acceptThread = new Thread(acceptor, "thread-acceptor");
        // acceptThread.setDaemon(true);
        acceptThread.start();

    }

    // Acceptor
    public class Acceptor implements Runnable {
        @Override
        public void run() {
            while (true) {
                System.out.println("Acceptor-accept()启动，等待连接");
                try {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    try {
                        // 接收事件放入队列
                        events.offer(socketChannel, 10000, TimeUnit.MILLISECONDS);
                        getPoller().selector.wakeup();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Accepted: " + socketChannel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void init() {
            try {
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.configureBlocking(true);
                serverSocketChannel.bind(new InetSocketAddress(connector.getPort()));
                LogFactory.get().info("Acceptor 初始化");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Poller
    public class Poller implements Runnable {
        private Selector selector;
        public Poller() throws IOException {
            this.selector = Selector.open();
        }

        @Override
        public void run() {
            int count = 0;
            while (true) {
                System.out.println("Poller-select()启动，等待事件");
                try {

                    // 注册队列中缓存的事件
                    for (int i=0; i <= events.size()-1; ++i) {
                        SocketChannel socketChannel = events.poll();
                        if (socketChannel != null) {
                            socketChannel.register(selector, SelectionKey.OP_READ);
                        }
                    }

                    selector.select();
                    // 处理发生的事件
                    Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();
                        if (key.isReadable()) {
                            read(key);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void read(SelectionKey key) {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            key.cancel();
            // 将连接交给线程池处理
            nioExecutor.execute(socketChannel);
            System.out.println("Selected: " + socketChannel);
        }

        public Selector getSelector() {
            return this.selector;
        }
    }


    // 加锁register()/select()，存在问题，没有使用，用阻塞队列代替实现
    public void register(SocketChannel socketChannel, Selector selector) throws ClosedChannelException {
        mark = true;
        selector.wakeup();
        socketChannel.register(selector, SelectionKey.OP_READ);
        mark = false;
        //latch.countDown();
    }
    public void select(Selector selector) throws IOException {
        while (true) {
            // System.out.println("select()");
            if (mark) {
                //latch = new CountDownLatch(1);
                //try {
                //    latch.await(-1, TimeUnit.MILLISECONDS);
                //} catch (InterruptedException e) {
                //    e.printStackTrace();
                //}
                continue;
            }
            if (selector.select() > 0) {
                return;
            }
        }
    }

    // getter/setter
    public Poller getPoller() throws IOException {
        return poller;
    }
}
