package com.jayyin.jaytomcat.net;

import com.jayyin.jaytomcat.catalina.Connector;
import com.jayyin.jaytomcat.http.HttpProcessor;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.util.ThreadPoolUtil;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * worker线程池执行任务，模拟阻塞
 */
public class NioExecutor implements Runnable{
    private final Connector connector;
    private final Selector selector;
    private final BlockingQueue<SocketWrapper> events;  // 事件队列
    private static final int EVENT_TIMEOUT = 1000;  // 队列操作超时
    private static final int WRITE_TIMEOUT = 10000;  // 等待可写超时
    private static final int READ_TIMEOUT = -1;  // 等待可读超时
    private static final int READ_COUNT = 10;  // 最多读取次数
    private boolean mark;
    //
    public NioExecutor(Connector connector) throws IOException {
        this.selector = Selector.open();
        this.connector = connector;
        this.events = new LinkedBlockingQueue<>();
    }

    // 模拟阻塞监听
    @Override
    public void run() {
        while (true) {
            try {
                // 注册队列中缓存的事件
                for (int i=0; i <= events.size()-1; ++i) {
                    SocketWrapper socketWrapper = events.poll();
                    if (socketWrapper != null) {
                        SocketChannel socketChannel = socketWrapper.getChannel();
                        if (socketChannel != null) {
                            socketWrapper.setKey(socketChannel.register(selector, 0, socketWrapper));
                            socketWrapper.countDownReadLatch();
                            System.out.println("SimBlocking-成功注册事件：" + socketChannel);
                        }
                    }
                }

                selector.select();
                // 处理发生的事件
                Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isValid() && key.isReadable()) {
                        key.interestOps(0);
                        SocketWrapper socketWrapper = (SocketWrapper) key.attachment();
                        socketWrapper.countDownReadLatch();
                        System.out.println("SimBlocking-监听到Read事件" + socketWrapper.readLatch);
                    }
                    else if (key.isValid() && key.isWritable()) {
                        key.interestOps(0);
                        SocketWrapper socketWrapper = (SocketWrapper) key.attachment();
                        socketWrapper.countDownWriteLatch();
                        System.out.println("SimBlocking-监听到Write事件" + socketWrapper.writeLatch);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 将连接交给线程池处理
    public void execute(SocketChannel socketChannel) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                // 将连接加入队列，等待注册
                SocketWrapper socketWrapper = new SocketWrapper(socketChannel);
                try {
                    socketWrapper.startReadLatch(1);
                    events.offer(socketWrapper, EVENT_TIMEOUT, TimeUnit.MILLISECONDS);
                    selector.wakeup();
                    // 注册完成唤醒，继续执行（如果注册没有完成，超时继续，read()会出错，这里应该抛出异常中断连接）
                    if (socketWrapper.awaitReadLatch(2*1000, TimeUnit.MILLISECONDS)) {
                        socketWrapper.countDownReadLatch();
                    }
                    else {
                        throw new IOException("SimBlocking-注册超时");
                    }


                    // 读
                    String requestString = read(socketWrapper);

                    // 处理请求文本requestString
                    Socket s = socketChannel.socket();
                    Request request = new Request(s, requestString, connector.getService());
                    Response response = new Response();
                    // 处理
                    HttpProcessor httpProcessor = new HttpProcessor();
                    httpProcessor.execute(s, request, response);

                    // 写
                    byte[] responseBytes = response.getResponseBytes();
                    if (responseBytes != null) {
                        write(socketWrapper, responseBytes);
                    }

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    // 关闭连接和已注册的选择键
                    try {
                        SelectionKey key = socketWrapper.getKey();
                        if (key != null) {
                            key.cancel();
                        }
                        String channel = "" + socketChannel;
                        socketChannel.close();
                        System.out.println("连接关闭：" + channel);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        };

        ThreadPoolUtil.execute(r, socketChannel);
    }

    // 读取请求
    // 当read为0时，数据并不一定完整的被接收到了，需要多次读取或者设置合适的超时时间来解决并包问题
    public String read(SocketWrapper socketWrapper) throws IOException {
        SocketChannel socketChannel = socketWrapper.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder requestString = new StringBuilder();
        int readCount = 0;
        while (true) {
            buffer.clear();
            int read = socketChannel.read(buffer);
            if (read < 0) {
                break;
            }
            else if (read == 0) {
                if (readCount >= READ_COUNT) {
                    break;
                }

                socketWrapper.startReadLatch(1);

                // 设置监听事件
                SelectionKey key = socketWrapper.getKey();
                if (key !=null && key.isValid() && (key.interestOps() & SelectionKey.OP_READ) == 0) {
                    key.interestOps(SelectionKey.OP_READ);
                    selector.wakeup();
                }

                //
                // System.out.println("等待可读事件：" + readCount);
                try {
                    socketWrapper.awaitReadLatch(READ_TIMEOUT, TimeUnit.MILLISECONDS);
                    socketWrapper.countDownReadLatch();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                readCount++;
                // System.out.println(readCount);
            }
            else {
                requestString.append(new String(buffer.array(), 0, buffer.position(), "utf-8"));
            }
            // System.out.println("读取数据 " + read);
        }
        return requestString.toString();
    }

    // 响应写回客户端
    public void write(SocketWrapper socketWrapper, byte[] responseBytes) throws IOException {
        SocketChannel socketChannel = socketWrapper.getChannel();
        ByteBuffer outBuffer = ByteBuffer.wrap(responseBytes);
        SelectionKey key = socketWrapper.getKey();
        while (outBuffer.hasRemaining()) {
            int data = socketChannel.write(outBuffer);
            if (data == 0) {
                socketWrapper.startWriteLatch(1);

                // 设置监听事件
                if(key.isValid() && (key.interestOps() & SelectionKey.OP_WRITE) == 0) {
                    key.interestOps(SelectionKey.OP_WRITE);
                    selector.wakeup();
                }

                //
                System.out.println("等待可写事件");
                try {
                    socketWrapper.awaitWriteLatch(WRITE_TIMEOUT, TimeUnit.MILLISECONDS);
                    socketWrapper.countDownWriteLatch();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("写入数据 " + data);
        }
    }

    // SocketWrapper 封装连接channel，附加到选择键上
    public class SocketWrapper {
        private SocketChannel channel;
        private SelectionKey key;
        private CountDownLatch readLatch = null;
        private CountDownLatch writeLatch = null;
        //
        public SocketWrapper(SocketChannel channel) {
            this.channel = channel;
        }

        // 新建计数锁
        protected CountDownLatch startLatch(CountDownLatch latch, int cnt) {
            if ( latch == null || latch.getCount() == 0 ) {
                return new CountDownLatch(cnt);
            }
            else {
                throw new IllegalStateException("Latch must be at count 0 or null.");
            }
        }
        public void startReadLatch(int cnt) {
            readLatch = startLatch(readLatch,cnt);
        }
        public void startWriteLatch(int cnt) {
            writeLatch = startLatch(writeLatch,cnt);
        }

        // 阻塞线程直到计数归零或超时
        protected boolean awaitLatch(CountDownLatch latch, long timeout, TimeUnit unit) throws InterruptedException {
            if ( latch == null ) {
                throw new IllegalStateException("Latch cannot be null");
            }
            return latch.await(timeout,unit);
        }
        public boolean awaitReadLatch(long timeout, TimeUnit unit) throws InterruptedException {
            return awaitLatch(readLatch,timeout,unit);
        }
        public boolean awaitWriteLatch(long timeout, TimeUnit unit) throws InterruptedException {
            return awaitLatch(writeLatch,timeout,unit);
        }

        // 计数
        public void countDown(CountDownLatch latch) {
            if ( latch == null ) {
                throw new IllegalStateException("Latch cannot be null");
            }
            latch.countDown();
        }
        public void countDownReadLatch() {
            countDown(readLatch);
        }
        public void countDownWriteLatch() {
            countDown(writeLatch);
        }

        //
        public SocketChannel getChannel() {
            return channel;
        }

        public SelectionKey getKey() {
            return key;
        }

        public void setKey(SelectionKey key) {
            this.key = key;
        }
    }
}
