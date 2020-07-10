<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        // 置顶导航
        $(function () {
            $("#navCategory").addClass("active");
        });
        // 表单提交
        $("#addForm").submit(function () {
            // 名称、图片均不为空才能提交
            if(checkEmpty("name", "分类名称"))
                return false;
            if (checkEmpty("categoryPic", "分类图片"))
                return false;
            return true;
        });
    });
</script>

<title>分类管理</title>

<div class="workingArea">

    <!--分类面板-->
    <div class="panel">
        <h3>分类管理</h3>
        <br>
        <div class="listDataTableDiv">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>图片</th>
                        <th>分类名称</th>
                        <th>属性管理</th>
                        <th>产品管理</th>
                        <th>编辑</th>
                        <th>删除</th>
                </tr>
                </thead>

                <tbody>
                    <c:forEach items="${thecs}" var="c">
                        <tr>
                            <td>${c.id}</td>
                            <td><img src="img/category/${c.id}.jpg" style="max-width:100px; max-height: 100px;"></td>
                            <td>${c.name}</td>
                            <td><a href="admin_property_list?cid=${c.id}">属性</a></td>
                            <td><a href="admin_product_list?cid=${c.id}">产品</a></td>
                            <td><a href="admin_category_edit?id=${c.id}">编辑</a></td>
                            <td><a href="admin_category_delete?id=${c.id}" deleteLink="true">删除</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <!--分页-->
        <div class="pageDiv">
            <%@include file="../include/admin/adminPage.jsp"%>
        </div>
    </div>

    <!--新增分类-->
    <div class="panel">
        <div>新增分类</div>
        <div>
            <form id="addForm" action="admin_category_add" method="post" enctype="multipart/form-data">
                <input id="name" name="name" type="text" placeholder="分类名称">
                <input id="categoryPic" name="filepath" accept="image/*" type="file">
                <button type="submit" class="button">提交</button>
            </form>
        </div>
    </div>

</div>
<%@include file="../include/admin/adminFooter.jsp"%>
