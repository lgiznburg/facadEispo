<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<nav class="nav flex-column">
  <c:set var="uri" value="${pageContext.request.requestURI}"/>
  <%--<sec:authorize access="hasRole('ROLE_ADMIN')">--%>

    <a class="nav-link <c:if test="${fn:contains(uri, '/storedProperties.htm')}">active</c:if>" href="<c:url value="/storedProperties.htm"/>">Настройки</a>
  <a class="nav-link <c:if test="${fn:contains(uri, '/loadFromTandem.htm')}">active</c:if>" href="<c:url value="/loadFormTandem.htm"/>">Загрузить из Тандема</a>
  <a class="nav-link" href="<c:url value="/createApplicationRequest.htm"/>" target="_blank">Создать файл заявлений</a>
  <a class="nav-link" href="<c:url value="/createWithdrawalRequest.htm"/>" target="_blank">Создать файл отзыв заявлений</a>
  <a class="nav-link <c:if test="${fn:contains(uri, '/loadApplicationResponse.htm')}">active</c:if>" href="<c:url value="/loadApplicationResponse.htm"/>">Загрузить ответ на заявления</a>
  <%--</sec:authorize>--%>
<%--
  <sec:authorize access="hasAnyRole('ROLE_SERVICEMAN','ROLE_ADMIN')">
    <a class="nav-link <c:if test="${fn:contains(uri, '/admin/Statistics.htm')}">active</c:if>" href="<c:url value="/admin/Statistics.htm"/>">Статистика</a>
    <a class="nav-link <c:if test="${fn:contains(uri, '/admin/DayStats.htm')}">active</c:if>" href="<c:url value="/admin/DayStats.htm"/>">Данные на день</a>
    <a class="nav-link <c:if test="${fn:contains(uri, '/admin/CreateTodayAppointment.htm') or fn:contains(uri, '/admin/SelectCampaign.htm')}">active</c:if>" href="<c:url value="/admin/SelectCampaign.htm"/>">Записать на сегодня</a>
  </sec:authorize>
--%>
</nav>
