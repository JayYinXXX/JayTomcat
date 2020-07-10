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

<title>属性编辑</title>

<div class="workingArea">
    <div class="panel">
        <ul class="breadcrumbs">
            <li><a href="admin_category_list">所有分类</a></li>
            <li class="unavailable"><a href="#">当前分类</a></li>
            <li class="unavailable"><a href="#">属性列表</a></li>
            <li class="current">编辑属性</li>
        </ul>
        <h3>${p.category.name}</h3>
        <h3>${p.name}</h3>

        <div>编辑属性</div>
        <div>
            <form id="addForm" action="admin_property_update" method="post">
                <input id="name" name="name" type="text" value="${p.name}">
                <input type="hidden" name="id" value="${p.id}">
                <input type="hidden" name="cid" value="${p.category.id}">
                <button type="submit" class="button">提交修改</button>
            </form>
        </div>
    </div>

</div>
<%@include file="../include/admin/adminFooter.jsp"%>