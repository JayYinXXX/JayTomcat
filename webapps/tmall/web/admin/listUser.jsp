<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false" import="java.util.*"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>

<script>
    $(function () {
        $("#navUser").addClass("active");
    });
</script>

<title>用户管理</title>

<div class="workingArea">

    <!--用户面板-->
    <div class="panel">
        <h3>用户管理</h3>
        <br>
        <div class="listDataTableDiv">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>用户名</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${us}" var="u">
                        <tr>
                            <td>${u.id}</td>
                            <td>${u.name}</td>
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
