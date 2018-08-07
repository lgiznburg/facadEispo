<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<ul class="nav flex-column">  <%-- nav-tabs --%>
  <c:set var="uri" value="${pageContext.request.requestURI}"/>
  <%--<sec:authorize access="hasRole('ROLE_ADMIN')">--%>

  <li class="nav-item">
    <a class="nav-link <c:if test="${fn:contains(uri, '/storedProperties.htm')}">active</c:if>" href="<c:url value="/storedProperties.htm"/>">Настройки</a>
  </li>
  <li class="nav-item">
    <a class="nav-link <c:if test="${fn:contains(uri, '/loadFromTandem.htm')}">active</c:if>" href="<c:url value="/loadFormTandem.htm"/>">Загрузить из Тандема</a>
  </li>
  <%--<li class="nav-item dropdown">
    <a class="nav-link dropdown-toggle" data-toggle="dropdown" href="#" role="button" aria-haspopup="true" aria-expanded="false">Создать запрос в ЕИСПО</a>
    <div class="dropdown-menu">
      <a class="dropdown-item" href="<c:url value="/createApplicationRequest.htm"/>" target="_blank">Файл заявлений</a>
      <a class="dropdown-item" href="<c:url value="/createWithdrawalRequest.htm"/>" target="_blank">Файл отзыв заявлений</a>
      <a class="dropdown-item" href="<c:url value="/createScoresRequest.htm"/>" target="_blank">Файл запрос баллов</a>
    </div>
  </li>--%>

  <li class="nav-item">
    <a class="nav-link" href="<c:url value="/createApplicationRequest.htm"/>" target="_blank">Файл заявлений</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<c:url value="/createWithdrawalRequest.htm"/>" target="_blank">Отзыв заявлений</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<c:url value="/createScoresRequest.htm"/>" target="_blank">Запрос баллов аккредитации</a>
  </li>
  <li class="nav-item">
    <a class="nav-link" href="<c:url value="/createScoresRequest.htm"><c:param name="type" value="test"/></c:url>" target="_blank">Запрос баллов тестирование</a>
  </li>

  <li class="nav-item">
    <a class="nav-link <c:if test="${fn:contains(uri, '/loadApplicationResponse.htm')}">active</c:if>" href="<c:url value="/loadApplicationResponse.htm"/>">Загрузить ответ на заявления</a>
  </li>
  <%--</sec:authorize>--%>
<%--
  <sec:authorize access="hasAnyRole('ROLE_SERVICEMAN','ROLE_ADMIN')">
    <a class="nav-link <c:if test="${fn:contains(uri, '/admin/Statistics.htm')}">active</c:if>" href="<c:url value="/admin/Statistics.htm"/>">Статистика</a>
    <a class="nav-link <c:if test="${fn:contains(uri, '/admin/DayStats.htm')}">active</c:if>" href="<c:url value="/admin/DayStats.htm"/>">Данные на день</a>
    <a class="nav-link <c:if test="${fn:contains(uri, '/admin/CreateTodayAppointment.htm') or fn:contains(uri, '/admin/SelectCampaign.htm')}">active</c:if>" href="<c:url value="/admin/SelectCampaign.htm"/>">Записать на сегодня</a>
  </sec:authorize>
--%>
</ul>
