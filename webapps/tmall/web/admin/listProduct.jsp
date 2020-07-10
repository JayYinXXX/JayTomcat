<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        $("#addForm").submit(function () {
            if (checkEmpty("name", "产品名称")) return false;
            if (checkEmpty("subTitle", "小标题")) return false;
            if (!checkNumber("originalPrice", "原价格")) return false;
            if (!checkNumber("promotePrice", "优惠价格")) return false;
            if (!checkInt("stock", "库存")) return false;
            return true;
        });
    });
</script>

<title>产品管理</title>

<div class="workingArea">

    <div class="panel">
        <ul class="breadcrumbs">
            <li><a href="admin_category_list">所有分类</a></li>
            <li class="unavailable"><a href="#">当前分类</a></li>
            <li class="current">产品列表</li>
        </ul>
        <h3>${c.name}</h3>

        <div class="listDataTableDiv">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>图片</th>
                        <th>产品名称</th>
                        <th>产品小标题</th>
                        <th>原价格</th>
                        <th>优惠价格</th>
                        <th>库存数量</th>
                        <th>图片管理</th>
                        <th>属性管理</th>
                        <th>编辑</th>
                        <th>删除</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${ps}" var="p">
                        <tr>
                            <td>${p.id}</td>
                            <td>图</td>
                            <td>${p.name}</td>
                            <td>${p.subTitle}</td>
                            <td>${p.originalPrice}</td>
                            <td>${p.promotePrice}</td>
                            <td>${p.stock}</td>
                            <td><a href="admin_productImage_list?pid=${p.id}">图片管理</a></td>
                            <td><a href="admin_product_editPropertyValue?id=${p.id}">属性管理</a></td>
                            <td><a href="admin_product_edit?id=${p.id}">编辑</a></td>
                            <td><a href="admin_product_delete?id=${p.id}" deleteLink="true">删除</a></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <%--分页--%>
        <div class="pageDiv">
            <%@include file="../include/admin/adminPage.jsp"%>
        </div>
    </div>

    <div class="panel">
        <div>新增产品</div>
        <div>
            <form id="addForm" action="admin_product_add" method="post">
                <input id="name" name="name" type="text" placeholder="产品名称">
                <input id="subTitle" name="subTitle" type="text" placeholder="产品小标题">
                <input id="originalPrice" name="originalPrice" type="text" placeholder="原价格">
                <input id="promotePrice" name="promotePrice" type="text" placeholder="优惠价格">
                <input id="stock" name="stock" type="text" placeholder="库存">
                <input type="hidden" name="cid" value="${c.id}">
                <button type="submit" class="button">提交</button>
            </form>
        </div>
    </div>

</div>
<%@include file="../include/admin/adminFooter.jsp"%>