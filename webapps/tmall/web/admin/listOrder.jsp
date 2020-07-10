<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        $("#navOrder").addClass("active");
    });
</script>

<title>订单管理</title>

<div class="workingArea">

    <!--订单面板-->
    <div class="panel">
        <h3>订单管理</h3>
        <br>
        <div class="listDataTableDiv">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>订单状态</th>
                    <th>金额</th>
                    <th>商品数量</th>
                    <th>买家名称</th>
                    <th>创建时间</th>
                    <th>支付时间</th>
                    <th>发货时间</th>
                    <th>确认收货时间</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach items="${os}" var="o">
                    <tr>
                        <td>${o.id}</td>
                        <td>${o.statusDesc}</td>
                        <td>￥<fmt:formatNumber type="number" value="${o.total}" minFractionDigits="2"/></td>
                        <td>${o.totalNumber}</td>
                        <td>${o.user.name}</td>
                        <td><fmt:formatDate value="${o.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <td><fmt:formatDate value="${o.payDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <td><fmt:formatDate value="${o.deliveryDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
                        <td><fmt:formatDate value="${o.confirmDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
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


</div>
<%@include file="../include/admin/adminFooter.jsp"%>
