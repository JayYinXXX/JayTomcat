/*
 * Generated by the Jasper component of Apache Tomcat
 * Version: Apache Tomcat/7.0.64
 * Generated at: 2020-02-28 16:10:33 UTC
 * Note: The last modified time of this file was set to
 *       the last modified time of the source file after
 *       generation to assist with modification tracking.
 */
package org.apache.jsp.admin;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;

public final class editProductValue_jsp extends org.apache.jasper.runtime.HttpJspBase
    implements org.apache.jasper.runtime.JspSourceDependent {

  private static final javax.servlet.jsp.JspFactory _jspxFactory =
          javax.servlet.jsp.JspFactory.getDefaultFactory();

  private static java.util.Map<java.lang.String,java.lang.Long> _jspx_dependants;

  static {
    _jspx_dependants = new java.util.HashMap<java.lang.String,java.lang.Long>(3);
    _jspx_dependants.put("/admin/../include/admin/adminHeader.jsp", Long.valueOf(1582796363064L));
    _jspx_dependants.put("/admin/../include/admin/adminFooter.jsp", Long.valueOf(1582721685832L));
    _jspx_dependants.put("/admin/../include/admin/adminNavigator.jsp", Long.valueOf(1582735097386L));
  }

  private org.apache.jasper.runtime.TagHandlerPool _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvar_005fitems;

  private javax.el.ExpressionFactory _el_expressionfactory;
  private org.apache.tomcat.InstanceManager _jsp_instancemanager;

  public java.util.Map<java.lang.String,java.lang.Long> getDependants() {
    return _jspx_dependants;
  }

  public void _jspInit() {
    _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvar_005fitems = org.apache.jasper.runtime.TagHandlerPool.getTagHandlerPool(getServletConfig());
    _el_expressionfactory = _jspxFactory.getJspApplicationContext(getServletConfig().getServletContext()).getExpressionFactory();
    _jsp_instancemanager = org.apache.jasper.runtime.InstanceManagerFactory.getInstanceManager(getServletConfig());
  }

  public void _jspDestroy() {
    _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvar_005fitems.release();
  }

  public void _jspService(final javax.servlet.http.HttpServletRequest request, final javax.servlet.http.HttpServletResponse response)
        throws java.io.IOException, javax.servlet.ServletException {

    final javax.servlet.jsp.PageContext pageContext;
    javax.servlet.http.HttpSession session = null;
    final javax.servlet.ServletContext application;
    final javax.servlet.ServletConfig config;
    javax.servlet.jsp.JspWriter out = null;
    final java.lang.Object page = this;
    javax.servlet.jsp.JspWriter _jspx_out = null;
    javax.servlet.jsp.PageContext _jspx_page_context = null;


    try {
      response.setContentType("text/html; charset=UTF-8");
      pageContext = _jspxFactory.getPageContext(this, request, response,
      			null, true, 8192, true);
      _jspx_page_context = pageContext;
      application = pageContext.getServletContext();
      config = pageContext.getServletConfig();
      session = pageContext.getSession();
      out = pageContext.getOut();
      _jspx_out = out;

      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!DOCTYPE html>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<html>\r\n");
      out.write("\r\n");
      out.write("<head>\r\n");
      out.write("    <!-- css 文件 -->\r\n");
      out.write("    <link rel=\"stylesheet\" href=\"http://cdn.static.runoob.com/libs/foundation/5.5.3/css/foundation.min.css\">\r\n");
      out.write("    <!-- jQuery 库 -->\r\n");
      out.write("    <script src=\"http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js\"></script>\r\n");
      out.write("    <!-- JavaScript 文件 -->\r\n");
      out.write("    <script src=\"http://cdn.static.runoob.com/libs/foundation/5.5.3/js/foundation.min.js\"></script>\r\n");
      out.write("    <!-- modernizr.js 文件 -->\r\n");
      out.write("    <script src=\"http://cdn.static.runoob.com/libs/foundation/5.5.3/js/vendor/modernizr.js\"></script>\r\n");
      out.write("\r\n");
      out.write("    <!-- 初始化 Foundation JS -->\r\n");
      out.write("    <script>\r\n");
      out.write("        $(document).ready(function() {\r\n");
      out.write("            $(document).foundation();\r\n");
      out.write("        })\r\n");
      out.write("    </script>\r\n");
      out.write("\r\n");
      out.write("    <!-- 一些通用方法 -->\r\n");
      out.write("    <script>\r\n");
      out.write("        // 给定id，判断元素的value属性是否为空\r\n");
      out.write("        function checkEmpty(id, name) {\r\n");
      out.write("            let e = $(\"#\" + id);\r\n");
      out.write("            if (e.val().length == 0) {\r\n");
      out.write("                alert(name + \"不能为空\");\r\n");
      out.write("                e.focus();\r\n");
      out.write("                return true;\r\n");
      out.write("            }\r\n");
      out.write("            return false;\r\n");
      out.write("        }\r\n");
      out.write("        // 判断value属性的值是否是 数字\r\n");
      out.write("        function checkNumber(id, name) {\r\n");
      out.write("            let e = $(\"#\" + id);\r\n");
      out.write("            let value = e.val();\r\n");
      out.write("            if (value == 0) {\r\n");
      out.write("                alert(name + \"不能为空\");\r\n");
      out.write("                e.focus();\r\n");
      out.write("                return false;\r\n");
      out.write("            }\r\n");
      out.write("            if (isNaN(value)) {\r\n");
      out.write("                alert(name + \"必须是数字\");\r\n");
      out.write("                e.focus();\r\n");
      out.write("                return false;\r\n");
      out.write("            }\r\n");
      out.write("            return true;\r\n");
      out.write("        }\r\n");
      out.write("        // 判断value属性的值是否是 整数\r\n");
      out.write("        function checkInt(id, name) {\r\n");
      out.write("            let e = $(\"#\" + id);\r\n");
      out.write("            let value = e.val();\r\n");
      out.write("            if (value == 0) {\r\n");
      out.write("                alert(name + \"不能为空\");\r\n");
      out.write("                e.focus();\r\n");
      out.write("                return false;\r\n");
      out.write("            }\r\n");
      out.write("            if (parseInt(value) != value) {  // value为String\r\n");
      out.write("                alert(name + \"必须是整数\");\r\n");
      out.write("                e.focus();\r\n");
      out.write("                return false;\r\n");
      out.write("            }\r\n");
      out.write("            return true;\r\n");
      out.write("        }\r\n");
      out.write("        // 对所有超链标签<a>设置一个点击事件的监听，如果是删除功能的超链则弹出确认窗口\r\n");
      out.write("        $(function () {\r\n");
      out.write("            $(\"a\").click(function () {\r\n");
      out.write("                let deleteLink = $(this).attr(\"deleteLink\");\r\n");
      out.write("                console.log(deleteLink);\r\n");
      out.write("                if (deleteLink === \"true\") {\r\n");
      out.write("                    let confirmDelete = confirm(\"确认删除？\");\r\n");
      out.write("                    return confirmDelete;\r\n");
      out.write("                }\r\n");
      out.write("            });\r\n");
      out.write("        });\r\n");
      out.write("    </script>\r\n");
      out.write("</head>\r\n");
      out.write("\r\n");
      out.write("<body>");
      out.write('\r');
      out.write('\n');
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!--置顶导航-->\r\n");
      out.write("<nav class=\"top-bar\" data-topbar>\r\n");
      out.write("    <!--标题logo区-->\r\n");
      out.write("    <ul class=\"title-area\">\r\n");
      out.write("        <li class=\"name\">\r\n");
      out.write("            <h1><a href=\"#\">光合作用</a></h1>\r\n");
      out.write("        </li>\r\n");
      out.write("        <!-- 小屏幕上的折叠按钮 -->\r\n");
      out.write("        <li class=\"toggle-topbar menu-icon\"><a href=\"#\"><span>Menu</span></a></li>\r\n");
      out.write("    </ul>\r\n");
      out.write("\r\n");
      out.write("    <section class=\"top-bar-section\">\r\n");
      out.write("        <ul class=\"left\">\r\n");
      out.write("            <li><a href=\"#\">后台主页</a></li>\r\n");
      out.write("            <li class=\"active\"><a href=\"admin_category_list\">分类管理</a></li>\r\n");
      out.write("            <li><a href=\"#\">用户管理</a></li>\r\n");
      out.write("            <li><a href=\"#\">订单管理</a></li>\r\n");
      out.write("        </ul>\r\n");
      out.write("    </section>\r\n");
      out.write("</nav>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<script>\r\n");
      out.write("    $(function () {\r\n");
      out.write("        $(\"input.pvValue\").keyup(function () {\r\n");
      out.write("            let value = $(this).val();\r\n");
      out.write("            let page = \"admin_product_updatePropertyValue\";\r\n");
      out.write("            let pvid = $(this).attr(\"pvid\");\r\n");
      out.write("            let parentTd = $(this).parent(\"td\");\r\n");
      out.write("            parentTd.css(\"border\",\"1px solid yellow\");\r\n");
      out.write("            $.post(\r\n");
      out.write("                page,\r\n");
      out.write("                {\"value\":value, \"pvid\":pvid},\r\n");
      out.write("                function (result) {\r\n");
      out.write("                    if (result == \"success\")\r\n");
      out.write("                        parentTd.css(\"border\",\"1px solid green\");\r\n");
      out.write("                    else\r\n");
      out.write("                        parentTd.css(\"border\",\"1px solid red\");\r\n");
      out.write("                }\r\n");
      out.write("            );\r\n");
      out.write("        });\r\n");
      out.write("    });\r\n");
      out.write("</script>\r\n");
      out.write("\r\n");
      out.write("<title>产品属性管理</title>\r\n");
      out.write("<div class=\"workingArea\">\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("    <div class=\"panel\">\r\n");
      out.write("        ");
      out.write("\r\n");
      out.write("        <ul class=\"breadcrumbs\">\r\n");
      out.write("            <li><a href=\"admin_category_list\">所有分类</a></li>\r\n");
      out.write("            <li class=\"unavailable\"><a href=\"#\">当前分类</a></li>\r\n");
      out.write("            <li><a href=\"admin_product_list?cid=");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${p.category.id}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("\">产品列表</a></li>\r\n");
      out.write("            <li class=\"current\">属性管理</li>\r\n");
      out.write("        </ul>\r\n");
      out.write("        <h3>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${p.category.name}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("</h3>\r\n");
      out.write("        <h3>");
      out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${p.name}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
      out.write("</h3>\r\n");
      out.write("\r\n");
      out.write("        ");
      out.write("\r\n");
      out.write("        <table>\r\n");
      out.write("            <thead>\r\n");
      out.write("                <tr>\r\n");
      out.write("                    <th>属性名</th>\r\n");
      out.write("                    <th>属性值</th>\r\n");
      out.write("                </tr>\r\n");
      out.write("            </thead>\r\n");
      out.write("            <tbody>\r\n");
      out.write("                ");
      if (_jspx_meth_c_005fforEach_005f0(_jspx_page_context))
        return;
      out.write("\r\n");
      out.write("            </tbody>\r\n");
      out.write("        </table>\r\n");
      out.write("\r\n");
      out.write("    </div>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("<!--页脚部分-->\r\n");
      out.write("<div class=\"footer\" style=\"margin-top: 200px; background-color: aqua;\">\r\n");
      out.write("    <p>页脚</p>\r\n");
      out.write("</div>\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("\r\n");
      out.write("</body>\r\n");
      out.write("</html>");
    } catch (java.lang.Throwable t) {
      if (!(t instanceof javax.servlet.jsp.SkipPageException)){
        out = _jspx_out;
        if (out != null && out.getBufferSize() != 0)
          try {
            if (response.isCommitted()) {
              out.flush();
            } else {
              out.clearBuffer();
            }
          } catch (java.io.IOException e) {}
        if (_jspx_page_context != null) _jspx_page_context.handlePageException(t);
        else throw new ServletException(t);
      }
    } finally {
      _jspxFactory.releasePageContext(_jspx_page_context);
    }
  }

  private boolean _jspx_meth_c_005fforEach_005f0(javax.servlet.jsp.PageContext _jspx_page_context)
          throws java.lang.Throwable {
    javax.servlet.jsp.PageContext pageContext = _jspx_page_context;
    javax.servlet.jsp.JspWriter out = _jspx_page_context.getOut();
    //  c:forEach
    org.apache.taglibs.standard.tag.rt.core.ForEachTag _jspx_th_c_005fforEach_005f0 = (org.apache.taglibs.standard.tag.rt.core.ForEachTag) _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvar_005fitems.get(org.apache.taglibs.standard.tag.rt.core.ForEachTag.class);
    _jspx_th_c_005fforEach_005f0.setPageContext(_jspx_page_context);
    _jspx_th_c_005fforEach_005f0.setParent(null);
    // /admin/editProductValue.jsp(55,16) name = items type = java.lang.Object reqTime = true required = false fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fforEach_005f0.setItems((java.lang.Object) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${pvs}", java.lang.Object.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
    // /admin/editProductValue.jsp(55,16) name = var type = java.lang.String reqTime = false required = false fragment = false deferredValue = false expectedTypeName = null deferredMethod = false methodSignature = null
    _jspx_th_c_005fforEach_005f0.setVar("pv");
    int[] _jspx_push_body_count_c_005fforEach_005f0 = new int[] { 0 };
    try {
      int _jspx_eval_c_005fforEach_005f0 = _jspx_th_c_005fforEach_005f0.doStartTag();
      if (_jspx_eval_c_005fforEach_005f0 != javax.servlet.jsp.tagext.Tag.SKIP_BODY) {
        do {
          out.write("\r\n");
          out.write("                    <tr>\r\n");
          out.write("                        <td>");
          out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${pv.property.name}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
          out.write("</td>\r\n");
          out.write("                        <td class=\"pvValue\"><input class=\"pvValue\" pvid=\"");
          out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${pv.id}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
          out.write("\" value=\"");
          out.write((java.lang.String) org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate("${pv.value}", java.lang.String.class, (javax.servlet.jsp.PageContext)_jspx_page_context, null, false));
          out.write("\" type=\"text\"></td>\r\n");
          out.write("                    </tr>\r\n");
          out.write("                ");
          int evalDoAfterBody = _jspx_th_c_005fforEach_005f0.doAfterBody();
          if (evalDoAfterBody != javax.servlet.jsp.tagext.BodyTag.EVAL_BODY_AGAIN)
            break;
        } while (true);
      }
      if (_jspx_th_c_005fforEach_005f0.doEndTag() == javax.servlet.jsp.tagext.Tag.SKIP_PAGE) {
        return true;
      }
    } catch (java.lang.Throwable _jspx_exception) {
      while (_jspx_push_body_count_c_005fforEach_005f0[0]-- > 0)
        out = _jspx_page_context.popBody();
      _jspx_th_c_005fforEach_005f0.doCatch(_jspx_exception);
    } finally {
      _jspx_th_c_005fforEach_005f0.doFinally();
      _005fjspx_005ftagPool_005fc_005fforEach_0026_005fvar_005fitems.reuse(_jspx_th_c_005fforEach_005f0);
    }
    return false;
  }
}
