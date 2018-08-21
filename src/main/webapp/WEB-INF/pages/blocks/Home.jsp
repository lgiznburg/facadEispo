<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container">
  <c:set var="activeNew" value=""/><c:set var="activeError" value=""/>
  <c:choose>
    <c:when test="${showType eq 'new'}"><c:set var="activeNew" value="active"/></c:when>
    <c:when test="${showType eq 'error'}"><c:set var="activeError" value="active"/></c:when>
    <c:when test="${showType eq 'search'}"><c:set var="activeSearch" value="active"/></c:when>
    <c:when test="${showType eq 'scores'}"><c:set var="activeScores" value="active"/></c:when>
  </c:choose>

  <ul class="nav nav-tabs">
    <li class="nav-item">
      <a class="nav-link ${activeNew}" href="<c:url value="/home.htm"/>">Новые</a>
    </li>
    <li class="nav-item">
      <a class="nav-link ${activeError}" href="<c:url value="/home.htm"><c:param name="variant" value="error"/></c:url>" >Полученные ошибки</a>
    </li>
    <li class="nav-item">
      <a class="nav-link ${activeSearch}" href="<c:url value="/home.htm"><c:param name="variant" value="search"/></c:url>" >Поиск</a>
    </li>
    <li class="nav-item">
      <a class="nav-link ${activeScores}" href="<c:url value="/home.htm"><c:param name="variant" value="scores"/></c:url>" >Проблемные баллы</a>
    </li>
  </ul>

  <h4>Поступающие</h4>

  <c:choose>
    <c:when test="${showType eq 'new'}">
      <div class="row">
        <div class="col">
          <a href="<c:url value="/createCsvScores.htm"/>" class="btn btn-outline-primary" target="_blank">Баллы</a>
        </div>
        <div class="col">
          <a href="<c:url value="/createScoresScript.htm"/>" class="btn btn-outline-primary">Скрипт для баллов</a>
        </div>
        <div class="col">
          <a href="<c:url value="/createCsvLogins.htm"/>" class="btn btn-outline-primary" target="_blank">Логины для теста</a>
        </div>
      </div>
      <table class="table table-hover">
        <thead>
        <tr>
          <th>Case #</th>
          <th>Name</th>
          <th>СНИЛС</th>
          <th>Test type</th>
          <th>OID</th>
          <th>Year</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${entrants}" var="entrant">
          <tr <c:if test="${not entrant.valid}">class="table-warning"</c:if> >
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.caseNumber}</a></td>
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</a></td>
            <td>${entrant.snilsNumber}</td>
            <td>${entrant.examInfo.type}</td>
            <td>${entrant.examInfo.organization}</td>
            <td>${entrant.examInfo.year}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>

    <c:when test="${showType eq 'error'}">
      <div class="row">
        <div class="col">
          <a href="<c:url value="/printErrors.htm"/>" class="btn btn-outline-primary" target="_blank">Печать</a>
          <a href="<c:url value="/createCsvErrors.htm"/>" class="btn btn-outline-primary" target="_blank">Файл отчета</a>
          <a href="<c:url value="/sendNotifications.htm"/>" class="btn btn-outline-primary">Рассылка E-mail</a>
          <a href="<c:url value="/updateErros.htm"/>" class="btn btn-outline-primary">Обновить</a>
        </div>
      </div>
      <table class="table table-hover">
        <thead>
        <tr>
          <th>Case # <br/>Name</th>
          <th>Phone<br>Email</th>
          <th>В заявлении</th>
          <th>Ошибка</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${entrants}" var="entrant">
          <tr <c:if test="${not entrant.valid}">class="table-warning"</c:if> >
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</a>
              <br><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.caseNumber}</a></td>
            <td>${entrant.phone} <br>${entrant.email}</td>
            <td>${entrant.examInfo.type}, ${entrant.examInfo.year}, ${entrant.examInfo.organization}</td>
            <td>${entrant.requests[0].response.response}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>

    </c:when>

    <c:when test="${showType eq 'search'}">

      <form:form action="home.htm?variant=search" commandName="searchForm" method="post" cssClass="form-inline">
        <div  class="form-group ">
          <fieldset>
              <form:input path="lastName" placeholder="Фамилия" cssClass="form-control"/>
              <form:input path="caseNumber" placeholder="№ дела" cssClass="form-control"/>
              <form:input path="snilsNumber" placeholder="СНИЛС" cssClass="form-control"/>
          </fieldset>
            <input type="submit" value="Найти" class="btn btn-primary"/>
        </div>
      </form:form>

      <table class="table table-hover">
        <thead>
        <tr>
          <th>Case # <br/> Name</th>
          <th>Phone<br>Email</th>
          <th>В заявлении</th>
          <th>Ошибка</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${entrants}" var="entrant">
          <tr <c:if test="${not entrant.valid}">class="table-warning"</c:if> >
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</a>
              <br><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.caseNumber}</a></td>
            <td>${entrant.phone} <br>${entrant.email}</td>
            <td>${entrant.examInfo.type}, ${entrant.examInfo.year}, ${entrant.examInfo.organization}</td>
            <td>${entrant.requests[0].response.response}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>
    </c:when>

    <c:when test="${showType eq 'scores'}">

      <div class="row">
        <div class="col">
          <a href="<c:url value="/createCsvScoresError.htm"/>" class="btn btn-outline-primary" target="_blank">Файл отчета</a>
        </div>
      </div>
      <table class="table table-hover">
        <thead>
        <tr>
          <th>Case # <br/> Name</th>
          <th>Phone<br>Email</th>
          <th>В заявлении</th>
          <th>Ошибка</th>
          <th>Ошибка баллов</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${entrants}" var="entrant">
          <tr <c:if test="${not entrant.valid}">class="table-warning"</c:if> >
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</a>
              <br><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.caseNumber}</a></td>
            <td>${entrant.phone} <br>${entrant.email}</td>
            <td>${entrant.examInfo.type}, ${entrant.examInfo.year}, ${entrant.examInfo.organization}</td>
            <td>${entrant.requests[0].response.response}</td>
            <td>${entrant.examInfo.response}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>

    </c:when>

  </c:choose>
</div>
