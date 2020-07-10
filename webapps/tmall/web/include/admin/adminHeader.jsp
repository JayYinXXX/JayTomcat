<!DOCTYPE html>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"
        isELIgnored="false" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix='fmt' %>

<html>

<head>
    <!-- css 文件 -->
    <link rel="stylesheet" href="http://cdn.static.runoob.com/libs/foundation/5.5.3/css/foundation.min.css">
    <!-- jQuery 库 -->
    <script src="http://cdn.static.runoob.com/libs/jquery/2.1.1/jquery.min.js"></script>
    <!-- JavaScript 文件 -->
    <script src="http://cdn.static.runoob.com/libs/foundation/5.5.3/js/foundation.min.js"></script>
    <!-- modernizr.js 文件 -->
    <script src="http://cdn.static.runoob.com/libs/foundation/5.5.3/js/vendor/modernizr.js"></script>

    <!-- 初始化 Foundation JS -->
    <script>
        $(document).ready(function() {
            $(document).foundation();
        })
    </script>

    <!-- 一些通用方法 -->
    <script>
        // 给定id，判断元素的value属性是否为空
        function checkEmpty(id, name) {
            let e = $("#" + id);
            if (e.val().length == 0) {
                alert(name + "不能为空");
                e.focus();
                return true;
            }
            return false;
        }
        // 判断value属性的值是否是 数字
        function checkNumber(id, name) {
            let e = $("#" + id);
            let value = e.val();
            if (value == 0) {
                alert(name + "不能为空");
                e.focus();
                return false;
            }
            if (isNaN(value)) {
                alert(name + "必须是数字");
                e.focus();
                return false;
            }
            return true;
        }
        // 判断value属性的值是否是 整数
        function checkInt(id, name) {
            let e = $("#" + id);
            let value = e.val();
            if (value == 0) {
                alert(name + "不能为空");
                e.focus();
                return false;
            }
            if (parseInt(value) != value) {  // value为String
                alert(name + "必须是整数");
                e.focus();
                return false;
            }
            return true;
        }
        // 对所有超链标签<a>设置一个点击事件的监听，如果是删除功能的超链则弹出确认窗口
        $(function () {
            $("a").click(function () {
                let deleteLink = $(this).attr("deleteLink");
                console.log(deleteLink);
                if (deleteLink === "true") {
                    let confirmDelete = confirm("确认删除？");
                    return confirmDelete;
                }
            });
        });
    </script>
</head>

<body>