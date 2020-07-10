<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        $("#editForm").submit(function () {
            if (checkEmpty("name", "分类名称"))
                return false;
            return true;
        });
    });
</script>

<title>分类管理</title>

<div class="workingArea">

    <div class="panel">
        <div>编辑分类</div>
        <div>
            <form id="editForm" action="admin_category_update" method="post" enctype="multipart/form-data">
                <input id="name" name="name" value="${c.name}" type="text">
                <input type="hidden" name="id" value="${c.id}">
                <input id="categoryPic" name="filepath" accept="image/*" type="file">
                <button type="submit" class="button">提交修改</button>
            </form>
        </div>
    </div>

</div>
<%@include file="../include/admin/adminFooter.jsp"%>