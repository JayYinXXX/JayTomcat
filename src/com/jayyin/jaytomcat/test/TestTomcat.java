package com.jayyin.jaytomcat.test;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import com.jayyin.jaytomcat.util.MiniBrowser;
import org.jsoup.select.Evaluator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Struct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestTomcat {
    private static int port = 18080;
    private static String ip = "127.0.0.1";

    //@BeforeClass
    //public static void beforeClass() {
    //    if (NetUtil.isUsableLocalPort(port)) {
    //        System.err.println("端口 " + port + "，JayTomcat未启动");
    //        System.exit(1);
    //    }
    //    else {
    //        System.out.println("JayTomcat已启动，开始单元测试");
    //    }
    //}

    //
    @Test
    public void testJayTomcat() {
        String html = getContentString("/");
        Assert.assertEquals("Hello JayTomcat from jayyin.com", html);
    }

    // /a.html
    @Test
    public void testRootAHtml() {
        String html = getContentString("/a.html");
        Assert.assertEquals("Hello JayTomcat from a.html", html);
    }

    // /a.txt
    @Test
    public void testRootATxt() {
        String response = getHttpString("/a.txt");
        containAssert("Content-Type: text/plain", response);
    }

    // /timeConsume.html
    @Test
    public void testRootTimeConsumeHtml() throws InterruptedException {
        // 线程池
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20, 20, 60, TimeUnit.SECONDS,
                                                                    new LinkedBlockingQueue<Runnable>(10));
        TimeInterval timeInterval = DateUtil.timer();
        // 3个任务
        for (int i=0; i < 3; ++i) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        threadPool.shutdown();;
        threadPool.awaitTermination(1, TimeUnit.HOURS);

        long duration = timeInterval.intervalMs();
        Assert.assertTrue(duration < 3000);
    }

    // /app1/index.html
    @Test
    public void testApp1IndexHtml() {
        String html = getContentString("/app1");
        Assert.assertEquals("Hello JayTomcat from /app1/index.html", html);
    }

    // /logo.png
    @Test
    public void testPNG() {
        byte[] bytes = getContentBytes("/logo.png");
        int length = 1672;
        Assert.assertEquals(length, bytes.length);
    }

    // /etf.pdf
    @Test
    public void testPDF() {
        byte[] bytes = getContentBytes("/etf.pdf");
        int length = 3590775;
        Assert.assertEquals(length, bytes.length);
    }

    // 404
    @Test
    public void test404() {
        String response = getHttpString("/not_exist.html");
        containAssert("HTTP/1.1 404 Not Found", response);
    }

    // 500
    @Test
    public void test500() {
        String response = getHttpString("/500.html");
        containAssert("HTTP/1.1 500 Internal Server Error", response);
    }

    // app1/HelloServlet
    @Test
    public void testHelloServlet() {
        String html = getContentString("/app1/hello");
        Assert.assertEquals("Hello JayTomcat from HelloServlet", html);
    }

    // javaweb/HelloServlet
    @Test
    public void testJavawebHelloServlet() {
        String html = getContentString("/javaweb/hello");
        containAssert("Hello DIY Tomcat from HelloServlet@javaweb", html);
    }

    // javaweb/HelloServlet 单例测试，比较两次访问中输出的servlet对象散列码是否一致
    @Test
    public void testSingleton() {
        String html1 = getContentString("/javaweb/hello");
        String html2 = getContentString("/javaweb/hello");
        Assert.assertEquals(html1, html2);
    }

    // app1/ParamServlet 测试GET方法传入参数
    @Test
    public void testGetParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("name","JayYin");
        String html = getContentString("/app1/param", params, null, "GET");

        Assert.assertEquals( "get方式 name:JayYin", html);
    }

    // app1/HeaderServlet 测试POST方法传入参数
    @Test
    public void testPostParam() {
        Map<String, Object> params = new HashMap<>();
        params.put("name","JayYin");
        String html = getContentString("/app1/param", params, null, "POST");

        Assert.assertEquals( "post方式 name:JayYin", html);
    }

    // app1/HeaderServlet 测试首部解析
    @Test
    public void testHeader() {
        String html = getContentString("/app1/header");
        Assert.assertEquals("MiniBrowser / java1.8", html);
    }

    // app1/SetCookieServlet 测试cookie的创建
    @Test
    public void testSetCookie() {
        String html = getHttpString("/app1/setCookie");
        containAssert("Set-Cookie: name=cookieValue;expires=", html);
    }

    // app1/GetCookieServlet 测试cookie的接收
    @Test
    public void testGetCookie() {
        // 设置cookie
        Map<String, String> cookies = new HashMap<>();
        cookies.put("cookieName","cookieValue");
        String html = getContentString("/app1/getCookie", null, cookies, "GET");

        Assert.assertEquals("cookieName:cookieValue", html);
    }

    // app1/SetSessionServlet 测试session创建-接收
    @Test
    public void testSession() {
        // 设置session
        String jsessionid1 = getContentString("/app1/setSession");

        // 接收session
        Map<String, String> cookies = new HashMap<>();
        cookies.put("JSESSIONID", jsessionid1);
        String jsessionid2 = getContentString("/app1/getSession", null, cookies, "GET");

        Assert.assertEquals(jsessionid1, jsessionid2);
    }

    // /index.jsp 测试jsp
    @Test
    public void testJsp() {
        String html = getContentString("/index.jsp");
        Assert.assertEquals("<% System.out.println(\"编译并执行jsp ROOT/index.jsp\"); %>", html);
    }

    // /app1/ClientJumpServlet 客户端跳转
    @Test
    public void testClientJump() {
        String html = getHttpString("/app1/client_jump");
        containAssert("HTTP/1.1 302 Moved Temporarily", html);
    }

    // /app1/ServletJumpServlet 服务端带参数跳转
    @Test
    public void testServerJump() {
        String html = getContentString("/app1/server_jump");
        Assert.assertEquals("Hello JayTomcat from HelloServlet\r\nname:jay", html);
    }

    // /javaweb2/hello 测试Filter
    @Test
    public void testJavaweb2Hello() {
        String html = getContentString("/javaweb2/hello");
        containAssert("Hello DIY Tomcat from HelloServlet@javaweb", html);
    }

    // 测试nio
    @Test
    public void testNio2() {
        byte[] bytes = getContentBytes("/etf.pdf");
        int length = 3590775;
        Assert.assertEquals(length, bytes.length);
    }




    // 访问目标uri，返回响应全部信息
    private String getHttpString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);

        return MiniBrowser.getHttpString(url);
    }
    private String getHttpString(String uri, Map<String, Object> params, Map<String, String> cookies, String method) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);

        return MiniBrowser.getHttpString(url, params, cookies, method, false);
    }
    private byte[] getHttpBytes(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);

        return MiniBrowser.getHttpBytes(url, null, null, "GET", false);
    }
    // 访问目标uri，返回响应主体内容
    private String getContentString(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);

        return MiniBrowser.getContentString(url);
    }
    private String getContentString(String uri, Map<String, Object> params, Map<String, String> cookies, String method) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);

        return MiniBrowser.getContentString(url, params, cookies, method, false);
    }
    private byte[] getContentBytes(String uri) {
        String url = StrUtil.format("http://{}:{}{}", ip, port, uri);

        return MiniBrowser.getContentBytes(url, null, null, "GET", false);
    }


    // 断言，html中是否包含str
    private void containAssert(String str, String html) {
        boolean match = StrUtil.containsAny(html, str);

        Assert.assertTrue(match);
    }

}
