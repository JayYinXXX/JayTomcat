<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"
        isELIgnored="false"%>
<!--分页-->
<!--不需要使用额外参数param的时候，page.param默认为null，不影响-->
<div class="pagination-centered">
    <ul class="pagination">

        <li class="arrow"><a href="?page.start=0${page.param}">&laquo;</a></li>

        <li <c:if test="${!page.hasPre}">class="unavailable"</c:if> >
            <a href="?page.start=${page.start-page.count}${page.param}">&lsaquo;</a>
        </li>

        <c:forEach begin="0" end="${page.totalPage-1}" varStatus="st" >
            <!--对于count=5，显示当前页左3页右3页，一共7页-->
            <c:if test="${st.index*page.count-page.start>=-15 && st.index*page.count-page.start<=15}">
                <li <c:if test="${st.index*page.count == page.start}">class="current"</c:if> >
                    <a href="?page.start=${st.index*page.count}${page.param}">${st.count}</a>
                </li>
            </c:if>
        </c:forEach>

        <li <c:if test="${!page.hasNext}">class="unavailable"</c:if> >
            <a href="?page.start=${page.start+page.count}${page.param}">&rsaquo;</a>
        </li>

        <li class="arrow"><a href="?page.start=${page.last}${page.param}">&raquo;</a></li>

    </ul>
</div>