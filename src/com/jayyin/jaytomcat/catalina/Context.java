package com.jayyin.jaytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.jayyin.jaytomcat.classloader.WebappClassLoader;
import com.jayyin.jaytomcat.exception.WebConfigDuplicationException;
import com.jayyin.jaytomcat.http.ApplicationContext;
import com.jayyin.jaytomcat.http.Request;
import com.jayyin.jaytomcat.http.StandardServletConfig;
import com.jayyin.jaytomcat.util.ContextXMLUtil;
import com.jayyin.jaytomcat.watcher.ContextFileChangeWatcher;
import org.apache.jasper.JspC;
import org.apache.jasper.compiler.JspRuntimeContext;
import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.util.*;


/**
 * web项目对象
 *
 * 实例化时将会
 *      1.保存项目地址
 *      2.解析项目配置文件 WEB-INF/web.xml
 *          1.建立uri与Servlet的映射关系
 */
public class Context {
    private String path;  // uri根路径 "/项目名" （访问默认的ROOT项目，可以省去项目名，path设置为 "/"）
    private String docBase;  // 项目根文件夹在系统中的绝对路径
    private boolean reloadable;  // 热加载标志
    private Host host;
    private ContextFileChangeWatcher contextFileChangeWatcher;  // 热加载监听器
    private ServletContext servletContext;
    private File contextWebXmlFile;
    private WebappClassLoader webappClassLoader;  // web应用独有类加载器
    // Servlet配置信息
    private Map<Class<?>, HttpServlet> servletPool;  // servlet池，保存各个servlet类的单例
    private List<String> loadOnStartupServletClassNames;  // 自启动的servlet
    private Map<String, String> servletName_servletClassName;  // servlet别名_servlet类全名
    private Map<String, String> servletClassName_servletName;
    private Map<String, String> url_servletName;
    private Map<String, String> url_servletClassName;
    private Map<String, Map<String, String>> servletName_initParams;  // 初始化参数
    // Filter配置信息
    private Map<String, Filter> filterPool;  // filterName - Filter
    private Map<String, String> filterName_filterClassName;  // filter别名_filter类全名
    private Map<String, String> filterClassName_filterName;
    private Map<String, List<String>> url_filterNames;  // url_filter别名
    private Map<String, List<String>> url_filterClassNames;
    private Map<String, Map<String, String>> filterName_initParams;
    // Listener监听配置
    private List<ServletContextListener> listeners;
    //
    public Context(String path, String docBase, boolean reloadable, Host host) {
        this.path = path;
        this.docBase = docBase;
        this.reloadable = reloadable;
        this.servletContext = new ApplicationContext(this);
        this.host = host;
        this.contextWebXmlFile = new File(docBase, ContextXMLUtil.getWatchedResource());
        // 以commonClassLoader为父加载器 新建一个本web应用独有的加载器
        ClassLoader commonClassLoader = Thread.currentThread().getContextClassLoader();
        this.webappClassLoader = new WebappClassLoader(docBase, commonClassLoader);
        //
        this.servletPool = new HashMap<>();
        this.loadOnStartupServletClassNames = new ArrayList<>();
        this.servletName_servletClassName = new HashMap<>();
        this.servletClassName_servletName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.url_servletClassName = new HashMap<>();
        this.servletName_initParams = new HashMap<>();
        //
        this.filterPool = new HashMap<>();
        this.filterName_filterClassName = new HashMap<>();
        this.filterClassName_filterName = new HashMap<>();
        this.url_filterNames = new HashMap<>();
        this.url_filterClassNames = new HashMap<>();
        this.filterName_initParams = new HashMap<>();
        //
        this.listeners = new ArrayList<>();

        deploy();
    }

    // 部署
    private void deploy() {
        TimeInterval timeInterval = DateUtil.timer();
        LogFactory.get().info("Deploying web application directory {}", this.docBase);

        // 根据配置文件初始化
        init();
        // 开启热加载
        if (reloadable) {
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();
        }
        // jsp编译配置
        JspC c = new JspC();
        new JspRuntimeContext(servletContext, c);

        LogFactory.get().info("Deployment of web application directory {} has finished in {}ms", this.docBase, timeInterval.intervalMs());
    }

    // 初始化，解析web.xml配置文件
    private void init() {
        fireEvent("init");

        // 读取xml
        if (!contextWebXmlFile.exists()) {
            return;
        }
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);
        // 1. 检查重复
        try {
            checkDuplicated(d);
        } catch (WebConfigDuplicationException e) {
            e.printStackTrace();
            return;
        }
        // 2. 解析servlet
        parseServletMapping(d);
        // 3. 解析初始化参数
        parseServletInitParams(d);
        // 4. 加载设置为自启动的servlet
        parseLoadOnStartUp(d);

        // 5. 解析filter
        parseFilterMapping(d);
        // 6. 解析filter初始化参数
        parseFilterInitParams(d);
        // 7. 加载filter
        loadFilters();

        // 8. 加载listener
        parseAndLoadListeners(d);
    }

    // 结束context，释放相关资源
    public void stop() {
        fireEvent("destroy");

        webappClassLoader.stop();
        contextFileChangeWatcher.stop();
        destroyServlets();
    }

    // 重新加载这个Context
    public void reload() {
        host.reload(this);
    }

    // 1. 检查Servlet配置文件中元素是否有重复
    private void checkDuplicated(Document d) throws WebConfigDuplicationException {
        String msg = "web.xml Servlet映射配置重复";
        checkDuplicated(d, "servlet-mapping url-pattern", msg);
        checkDuplicated(d, "servlet servlet-name", msg);
        checkDuplicated(d, "servlet servlet-class", msg);
    }
    private void checkDuplicated(Document d, String selector, String msg) throws WebConfigDuplicationException {
        Elements elements = d.select(selector);
        List<String> list = new ArrayList<>();
        for (Element e : elements) {
            list.add(e.text());
        }
        // 测试list中元素是否有重复
        Set<String> set = new HashSet<>(list);
        if (set.size() != list.size()) {
            throw new WebConfigDuplicationException(msg);
        }
    }

    // 2. 根据web.xml建立url与Servlet的映射关系
    private void parseServletMapping(Document d) {
        // servletName_servletClass / servletClass_servletName
        Elements servlets = d.select("servlet");
        for (Element servlet : servlets) {
            String servletName = servlet.select("servlet-name").text();
            String servletClass = servlet.select("servlet-class").text();
            servletName_servletClassName.put(servletName, servletClass);
            servletClassName_servletName.put(servletClass, servletName);
        }
        // url_servletName / url_servletClass
        Elements servletMappings = d.select("servlet-mapping");
        for (Element servletMapping : servletMappings) {
            String servletName = servletMapping.select("servlet-name").text();
            String urlPattern = servletMapping.select("url-pattern").text();
            url_servletName.put(urlPattern, servletName);
            url_servletClassName.put(urlPattern, servletName_servletClassName.get(servletName));
        }
    }

    // 3. 解析web.xml中servlet的初始化参数
    public void parseServletInitParams(Document d) {
        Elements servlets = d.select("servlet");
        for (Element servlet : servlets) {
            String servletName = servlet.select("servlet-name").text();
            Elements initParams = servlet.select("init-param");
            Map<String, String> name_value = new HashMap<>();
            for (Element initParam : initParams) {
                String name = initParam.select("param-name").text();
                String value = initParam.select("param-value").text();
                name_value.put(name, value);
            }
            servletName_initParams.put(servletName, name_value);
        }
    }

    // 4. 解析自启动配置并实例化对应servlet
    public void parseLoadOnStartUp(Document d) {
        Elements elements = d.select("load-on-startup");
        for (Element e : elements) {
            String servletClassName = e.parent().select("servlet-class").text();
            loadOnStartupServletClassNames.add(servletClassName);
        }

        // 加载
        try {
            for (String servletClassName : loadOnStartupServletClassNames) {
                Class<?> clazz = this.webappClassLoader.loadClass(servletClassName);
                getServlet(clazz);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | ServletException e) {
            e.printStackTrace();
        }

    }

    // 获取uri对应的Servlet类名
    public String getServletClassName(String uri) {
        return url_servletClassName.get(uri);
    }

    // 获取servlet池中的实例（如果不存在则需要新建，所以需要同步）
    public synchronized HttpServlet getServlet(Class<?> clazz) throws IllegalAccessException, InstantiationException, ServletException {
        HttpServlet servlet = servletPool.get(clazz);
        // 不存在则创建（懒汉式单例，因为在初始化阶段没有全部创建）
        if (servlet == null) {
            servlet = (HttpServlet) clazz.newInstance();
            ServletContext servletContext = this.getServletContext();
            // 配置servlet实例
            String className = clazz.getName();
            String servletName = servletClassName_servletName.get(className);
            Map<String, String> initParameters = servletName_initParams.get(servletName);
            ServletConfig servletConfig = new StandardServletConfig(servletContext, servletName, initParameters);
            servlet.init(servletConfig);
            servletPool.put(clazz, servlet);
        }
        return servlet;
    }

    // servlet实例运行结束需要执行的方法
    private void destroyServlets() {
        Collection<HttpServlet> servlets = servletPool.values();
        for (HttpServlet servlet : servlets) {
            servlet.destroy();
        }
    }

    // 5. 解析filter
    public void parseFilterMapping(Document d) {
        // filterName_filterClassName / filterClassName_filterName
        Elements filters = d.select("filter");
        for (Element e : filters) {
            String filterName = e.select("filter-name").text();
            String filterClassName = e.select("filter-class").text();
            filterName_filterClassName.put(filterName, filterClassName);
            filterClassName_filterName.put(filterClassName, filterName);
        }
        // url_filterName / url_filterClassName
        Elements filterMappings = d.select("filter-mapping");
        for (Element e : filterMappings) {
            String filterName = e.select("filter-name").text();
            String urlPattern = e.select("url-pattern").text();

            if (url_filterNames.get(urlPattern) == null) {
                List<String> filterNames = new ArrayList<>();
                filterNames.add(filterName);
                url_filterNames.put(urlPattern, filterNames);
            }
            else {
                url_filterNames.get(urlPattern).add(filterName);
            }

            if (url_filterClassNames.get(urlPattern) == null) {
                List<String> filterClassNames = new ArrayList<>();
                filterClassNames.add(filterName_filterClassName.get(filterName));
                url_filterClassNames.put(urlPattern, filterClassNames);
            }
            else {
                url_filterClassNames.get(urlPattern).add(filterName_filterClassName.get(filterName));
            }

        }
    }
    // 6. 解析filter初始化参数
    public void parseFilterInitParams(Document d) {
        Elements es = d.select("filter");
        for (Element e : es) {
            String filterName = e.select("filter-name").text();
            Elements initParams = e.select("init-param");
            Map<String, String> name_value = new HashMap<>();
            for (Element initParam : initParams) {
                String name = initParam.select("param-name").text();
                String value = initParam.select("param-value").text();
                name_value.put(name, value);
            }
            filterName_initParams.put(filterName, name_value);
        }
    }
    // 7. 加载filter
    public void loadFilters() {
        try {
            for (String filterClassName : filterClassName_filterName.keySet()) {
                Class<?> clazz = this.getWebappClassLoader().loadClass(filterClassName);
                String filterName = filterClassName_filterName.get(filterClassName);
                Map<String, String> initParams = filterName_initParams.get(filterName);
                FilterConfig filterConfig = new StandardFilterConfig(filterName, servletContext, initParams);

                Filter filter = filterPool.get(filterName);
                if (filter == null) {
                    filter = (Filter) clazz.newInstance();
                    filter.init(filterConfig);  // 新建时执行初始化
                    filterPool.put(filterName, filter);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取指定uri的匹配filters链
    public List<Filter> getMatchedFilters(String uri) {
        List<Filter> filters = new ArrayList<>();

        for (String pattern : url_filterNames.keySet()) {
            if (match(pattern, uri)) {
                // 加入属于匹配pattern的所有实例
                for (String filterName : url_filterNames.get(pattern)) {
                    filters.add(filterPool.get(filterName));
                }
            }
        }

        return filters;
    }
    // filter url匹配
    public boolean match(String pattern, String uri) {
        // 全部模式
        if (pattern.equals("/*")) {
            return true;
        }
        // 完全匹配
        if (pattern.equals(uri)) {
            return true;
        }
        // 指定资源类型匹配
        if (pattern.startsWith("/*.")) {
            String uriExtName = StrUtil.subAfter(uri, ".", false);
            String patternExtName = StrUtil.subAfter(pattern, ".", false);
            if (patternExtName.equals(uriExtName)) {
                return true;
            }
        }
        return false;
    }

    // 8. 解析并加载Listener监听器
    public void parseAndLoadListeners(Document d) {
        try {
            Elements es = d.select("listener listener-class");
            for (Element e : es) {
                String listenerClassName = e.text();
                Class<?>  clazz = this.getWebappClassLoader().loadClass(listenerClassName);
                ServletContextListener listener = (ServletContextListener) clazz.newInstance();
                listeners.add(listener);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException classNotFoundException) {
            classNotFoundException.printStackTrace();
        }
    }
    // 执行监听事件
    public void fireEvent(String type) {
        ServletContextEvent event = new ServletContextEvent(servletContext);
        for (ServletContextListener listener : listeners) {
            if ("init".equals(type)) {
                listener.contextInitialized(event);
            }
            else if ("destroy".equals(type)) {
                listener.contextDestroyed(event);
            }
        }
    }


    //
    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }

    public boolean isReloadable() {
        return reloadable;
    }

    public WebappClassLoader getWebappClassLoader() {
        return webappClassLoader;
    }

    public ServletContext getServletContext() {
        return servletContext;
    }


}
