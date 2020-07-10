<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        $("#addFormSingle").submit(function () {
            // 图片不为空才能提交
            if (checkEmpty("imgSingle", "图片文件"))
                return false;
            return true;
        });
        $("#addFormDetail").submit(function () {
            // 图片不为空才能提交
            if (checkEmpty("imgDetail", "图片文件"))
                return false;
            return true;
        });
    });
</script>

<title>产品图片管理</title>
<div class="workingArea">


    <div class="panel" style="height: 800px; ">
        <%--子导航--%>
        <ul class="breadcrumbs">
            <li><a href="admin_category_list">所有分类</a></li>
            <li class="unavailable"><a href="#">当前分类</a></li>
            <li><a href="admin_product_list?cid=${p.category.id}">产品列表</a></li>
            <li class="current">图片管理</li>
        </ul>
        <h3>${p.category.name}</h3>
        <h3>${p.name}</h3>

        <%--左侧图片列表--%>
        <div class="panel" style="width: 50%; float: left;">
            <form id="addFormSingle" action="admin_productImage_add" method="post" enctype="multipart/form-data">
                <input id="imgSingle" name="filepath" accept="image/*" type="file">
                <input type="hidden" name="pid" value="${p.id}">
                <input type="hidden" name="type" value="type_single">
                <button type="submit" class="button">提交</button>
            </form>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>产品单个缩略图</th>
                        <th>删除</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${pisSingle}" var="pi">
                        <tr>
                            <td>${pi.id}</td>
                            <td><img src="img/productSingle/${pi.id}.jpg" style="max-width:100px; max-height: 100px;"></td>
                            <td><a href="admin_productImage_delete?id=${pi.id}" deleteLink="true">删除</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <%--右侧图片列表--%>
        <div class="panel" style="width: 50%; float: right;">
            <form id="addFormDetail" action="admin_productImage_add" method="post" enctype="multipart/form-data">
                <input id="imgDetail" name="filepath" accept="image/*" type="file">
                <input type="hidden" name="pid" value="${p.id}">
                <input type="hidden" name="type" value="type_detail">
                <button type="submit" class="button">提交</button>
            </form>
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>产品详情缩略图</th>
                    <th>删除</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach items="${pisDetail}" var="pi">
                    <tr>
                        <td>${pi.id}</td>
                        <td><img src="img/productDetail/${pi.id}.jpg" style="max-width:100px; max-height: 100px;"></td>
                        <td><a href="admin_productImage_delete?id=${pi.id}" deleteLink="true">删除</a></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

    </div>


</div>
<%@include file="../include/admin/adminFooter.jsp"%>