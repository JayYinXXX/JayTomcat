<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        $("#addForm").submit(function () {
            if (checkEmpty("name", "属性名称"))
                return false;
            return true;
        });
    });
</script>

<title>属性管理</title>

<div class="workingArea">

    <div class="panel">
        <ul class="breadcrumbs">
            <li><a href="admin_category_list">所有分类</a></li>
            <li class="unavailable"><a href="#">当前分类</a></li>
            <li class="current">属性列表</li>
        </ul>
        <h3>${c.name}</h3>

        <div class="listDataTableDiv">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>属性名称</th>
                        <th>编辑</th>
                        <th>删除</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${ps}" var="p">
                        <tr>
                            <td>${p.id}</td>
                            <td>${p.name}</td>
                            <td><a href="admin_property_edit?id=${p.id}">编辑</a></td>
                            <td><a href="admin_property_delete?id=${p.id}" deleteLink="true">删除</a></td>
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

    <div class="panel">
        <div>新增属性</div>
        <div>
            <form id="addForm" action="admin_property_add" method="post">
                <input id="name" name="name" type="text" placeholder="属性名称">
                <input type="hidden" name="cid" value="${c.id}">
                <button type="submit" class="button">提交</button>
            </form>
        </div>
    </div>

</div>
<%@include file="../include/admin/adminFooter.jsp"%>