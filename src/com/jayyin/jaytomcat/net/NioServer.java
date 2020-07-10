package com.jayyin.jaytomcat.net;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.jayyin.jaytomcat.util.Constant;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
    private Selector selector;
    //


    public void init() throws IOException {
        // 新建选择器
        this.selector = Selector.open();

        // 通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);

        serverSocketChannel.bind(new InetSocketAddress(18080));
        serverSocketChannel.register(this.selector, SelectionKey.OP_ACCEPT);

        System.out.println("NioServer start");
    }

    public void start() throws IOException {
        while (true) {
            this.selector.select();
            // 处理发生的事件
            Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();
                if (key.isAcceptable()) {
                    accept(key);
                }
                else if (key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    public void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(this.selector, SelectionKey.OP_READ);
    }

    public void read(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder requestString = new StringBuilder();
            // 输入
            while (true) {
                buffer.clear();
                int read = socketChannel.read(buffer);
                if (read <= 0) {
                    break;
                }
                requestString.append(new String(buffer.array(), 0, buffer.position(), "utf-8"));
            }
            System.out.println("客户端请求：\r\n" + requestString);

            // 输出
            //String response = "NioServer return";
            //byte[] responseByte = response.getBytes();
            String response_head_200 = "HTTP/1.1 200 OK\r\n" +
                                        "Content-Type: {}" + "\r\n\r\n";
            String contentType = "application/pdf";
            String headText = StrUtil.format(response_head_200, contentType);
            byte[] head = headText.getBytes();

            File file = new File(Constant.webappsFolder, "ROOT/etf.pdf");
            byte[] body = FileUtil.readBytes(file);

            // 组合首部+响应体
            byte[] responseBytes = new byte[head.length + body.length];
            ArrayUtil.copy(head, 0, responseBytes, 0, head.length);
            ArrayUtil.copy(body, 0, responseBytes, head.length, body.length);

            ByteBuffer outBuffer = ByteBuffer.wrap(responseBytes);
            // 保证完全输出
            while (outBuffer.hasRemaining()) {
                socketChannel.write(outBuffer);
            }
            // i标记数据等待读取的位置
            //int i = 0;
            //while (i <= responseBytes.length - 1) {
            //    buffer.clear();
            //    int dataCount = responseBytes.length - i;  // 数据字节数
            //    int remaining = buffer.remaining();  // 缓冲区可用字节数
            //    if (dataCount > remaining) {
            //        buffer.put(responseBytes, i, remaining);
            //        i += remaining;
            //    }
            //    else {
            //        buffer.put(responseBytes, i, dataCount);
            //        i += dataCount;
            //    }
            //    buffer.flip();
            //    int write = socketChannel.write(buffer);
            //    // 保证完全输出
            //    while (buffer.hasRemaining()) {
            //        socketChannel.write(buffer);
            //    }
            //}
            //System.out.println("输出字节数：" + i);

            // 关闭连接
            System.out.println("连接关闭");
            key.cancel();
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
            // 出异常关闭通道和选择键
            try {
                key.cancel();
                socketChannel.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        NioServer server = new NioServer();
        server.init();
        server.start();
    }
}
