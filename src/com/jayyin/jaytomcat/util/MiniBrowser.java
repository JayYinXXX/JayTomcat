package com.jayyin.jaytomcat.util;

import cn.hutool.http.HttpUtil;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 模拟客户端发送请求
 * 指定url，作为客户端发送访问请求
 */
public class MiniBrowser {
    public static void main(String[] args) {
        // 测试网站
        String url = "http://static.how2j.cn/diytomcat.html";

        System.out.println(getHttpString(url));
        System.out.println(getContentString(url));
    }


    // http内容主体
    public static String getContentString(String url) {
        return getContentString(url, null, null, "GET", false);
    }

    public static String getContentString(String url, Map<String, Object> params, Map<String, String> cookies, String method, boolean gzip) {
        byte[] result = getContentBytes(url, params, cookies, method, gzip);
        if (result == null) {
            return null;
        }
        try {
            return new String(result, "utf-8").trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public static byte[] getContentBytes(String url) {
        return getContentBytes(url, null, null, "GET", false);
    }

    public static byte[] getContentBytes(String url, Map<String, Object> params, Map<String, String> cookies, String method, boolean gzip) {
        byte[] response = getHttpBytes(url, params, cookies, method, gzip);
        byte[] doubleReturn = "\r\n\r\n".getBytes();
        // 找到双回车换行的起始位置
        int pos = -1;
        for (int i=0; i < response.length-doubleReturn.length; ++i) {
            byte[] temp = Arrays.copyOfRange(response, i, i+doubleReturn.length);
            if (Arrays.equals(temp, doubleReturn)) {
                pos = i;
                break;
            }
        }
        if (pos == -1) {
            return null;
        }
        pos += doubleReturn.length;

        // 双回车换行之后为内容
        byte[] result = Arrays.copyOfRange(response, pos, response.length);
        return result;
    }


    // http全部信息
    public static String getHttpString(String url) {
        return getHttpString(url, null, null, "GET", false);
    }

    public static String getHttpString(String url, Map<String, Object> params, Map<String, String> cookies, String method, boolean gzip) {
        byte[] bytes = getHttpBytes(url, params, cookies, method, gzip);
        return new String(bytes).trim();
    }


    // 访问目标url，返回response的字节数组
    // gzip（是否压缩）
    public static byte[] getHttpBytes(String url, Map<String, Object> params, Map<String, String> cookies, String method, boolean gzip) {
        byte[] result = null;

        try {
            // 解析url
            URL u = new URL(url);
            Socket client = new Socket();
            int port = u.getPort();
            if (port == -1) {
                port = 80;
            }
            InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(), port);
            client.connect(inetSocketAddress, 1000);
            // 资源路径path
            String path = u.getPath();
            if (path.length() == 0) {
                path = "/";
            }

            // 1. 请求第一行
            // 加入参数
            String paramsString = HttpUtil.toParams(params);
            if (params != null && method.equals("GET")) {
                path = path + "?" + paramsString;
            }
            String firstLine = method + " " + path + " HTTP/1.1\r\n";

            // 2. 请求头部
            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Host", u.getHost() + ":" + port);
            requestHeaders.put("Accept", "text/html");
            requestHeaders.put("Connection", "close");
            requestHeaders.put("User-Agent", "MiniBrowser / java1.8");
            if (gzip) {
                requestHeaders.put("Accept-Encoding", "gzip");
            }
            // Cookie
            if (cookies != null && !cookies.isEmpty()) {
                StringBuilder cookiesString = new StringBuilder();
                for (String key : cookies.keySet()) {
                    cookiesString.append(key).append("=").append(cookies.get(key)).append(";");
                }
                requestHeaders.put("Cookie", cookiesString.toString());
            }

            // 3. 请求主体内容
            String requestContents = "";
            if (params != null && method.equals("POST")) {
                requestContents += "\r\n" + paramsString;
            }

            // 构建request字符串，写入Socket的输出流
            StringBuffer httpRequestString = new StringBuffer(firstLine);
            Set<String> headers = requestHeaders.keySet();
            for (String header : headers) {
                String headerLine = header + ":" + requestHeaders.get(header) + "\r\n";
                httpRequestString.append(headerLine);
            }
            httpRequestString.append(requestContents);
            PrintWriter printWriter = new PrintWriter(client.getOutputStream(), true);
            printWriter.println(httpRequestString);

            // 读取Socket的输入流，即返回的response
            InputStream is = client.getInputStream();
            result = readBytes(is, true);
            client.close();

        } catch (IOException e) {
            e.printStackTrace();
            try {
                result = e.toString().getBytes("utf-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    // 读取输入流中的数据
    // fully，充分读取
    public static byte[] readBytes(InputStream is, boolean fully) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        while (true) {
            int length = is.read(buffer);
            if (length == -1) {
                break;
            }
            baos.write(buffer, 0, length);
            // 非完全读取
            if (!fully && length != bufferSize) {
                break;  // 数组没满，说明字节读完
            }
        }
        return baos.toByteArray();
    }
}
