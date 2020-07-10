<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<%--这里使用AJAX实现属性值的编辑修改--%>
<script>
    $(function () {
        $("input.pvValue").keyup(function () {
            let value = $(this).val();
            let page = "admin_product_updatePropertyValue";
            let pvid = $(this).attr("pvid");
            let parentTd = $(this).parent("td");
            parentTd.css("border","1px solid yellow");
            $.post(
                page,
                {"value":value, "pvid":pvid},
                function (result) {
                    if (result == "success")
                        parentTd.css("border","1px solid green");
                    else
                        parentTd.css("border","1px solid red");
                }
            );
        });
    });
</script>

<title>产品属性管理</title>
<div class="workingArea">


    <div class="panel">
        <%--子导航--%>
        <ul class="breadcrumbs">
            <li><a href="admin_category_list">所有分类</a></li>
            <li class="unavailable"><a href="#">当前分类</a></li>
            <li><a href="admin_product_list?cid=${p.category.id}">产品列表</a></li>
            <li class="current">属性管理</li>
        </ul>
        <h3>${p.category.name}</h3>
        <h3>${p.name}</h3>

        <%--属性列表--%>
        <table>
            <thead>
                <tr>
                    <th>属性名</th>
                    <th>属性值</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach items="${pvs}" var="pv">
                    <tr>
                        <td>${pv.property.name}</td>
                        <td class="pvValue"><input class="pvValue" pvid="${pv.id}" value="${pv.value}" type="text"></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

    </div>


</div>
<%@include file="../include/admin/adminFooter.jsp"%>