package com.jayyin.jaytomcat.net;

import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.catalina.Connector;
import com.jayyin.jaytomcat.http.HttpProcessor;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.Response;
import com.jayyin.jaytomcat.util.MiniBrowser;
import com.jayyin.jaytomcat.util.ThreadPoolUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO 同步阻塞io
 */
public class BioEndpoint implements Runnable {
    private Connector connector;
    private ServerSocket ss;
    //
    public BioEndpoint(Connector connector, ServerSocket ss){
        this.connector = connector;
        this.ss = ss;
    }

    public void start() {
        // 开一个线程启动服务
        new Thread(this).start();
    }

    public void init() {
        // bio（同步阻塞，一个连接对应一个线程）
        LogFactory.get().info("Initializing ProtocolHandler [http-bio]");
    }

    // 解析请求体
    private String getRequestString(Socket s) throws IOException {
        InputStream is = s.getInputStream();
        byte[] bytes = MiniBrowser.readBytes(is, false);
        return new String(bytes, "utf-8");
    }

    @Override
    public void run() {
        try {
            // 检查端口，新建ServerSocket
//            if(!NetUtil.isUsableLocalPort(port)) {
//                System.out.println(port + " 端口已经被占用了！");
//                return;
//            }

            // 监听连接，处理Socket
            while (true) {
                Socket s =  ss.accept();

                // 新建任务并加入线程池
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // 请求整体文本
                            String requestString = getRequestString(s);

                            // 处理请求request
                            Request request = new Request(s, requestString, connector.getService());
                            // 创建响应response
                            Response response = new Response();
                            // 处理响应
                            HttpProcessor httpProcessor = new HttpProcessor();
                            httpProcessor.execute(s, request, response);

                            // 响应发回客户端
                            byte[] responseBytes = response.getResponseBytes();
                            if (responseBytes != null) {
                                OutputStream os = s.getOutputStream();
                                os.write(responseBytes);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            // 最后关闭Socket
                            try {
                                if (!s.isClosed()) {
                                    s.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                };
                ThreadPoolUtil.execute(r, s);
            }
        } catch (IOException e) {
            LogFactory.get().error(e);
            e.printStackTrace();
        }
    }
}
