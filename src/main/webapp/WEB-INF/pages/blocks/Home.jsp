<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="container">
  <c:set var="activeNew" value=""/><c:set var="activeError" value=""/>
  <c:choose>
    <c:when test="${showType eq 'new'}"><c:set var="activeNew" value="active"/></c:when>
    <c:when test="${showType eq 'error'}"><c:set var="activeError" value="active"/></c:when>
  </c:choose>

  <ul class="nav nav-tabs">
    <li class="nav-item">
      <a class="nav-link ${activeNew}" href="<c:url value="/home.htm"/>">Новые</a>
    </li>
    <li class="nav-item">
      <a class="nav-link ${activeError}" href="<c:url value="/home.htm"><c:param name="variant" value="error"/></c:url>" >Полученные ошибки</a>
    </li>
  </ul>

  <h4>Поступающие</h4>

  <c:choose>
    <c:when test="${showType eq 'new'}">
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
            <td>${entrant.caseNumber}</td>
            <td>${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</td>
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
          <a href="<c:url value="/printErrors.htm"/>" class="btn btn-outline-primary" target="_blank">Печатать список</a>
          <a href="<c:url value="/createCsvErrors.htm"/>" class="btn btn-outline-primary" target="_blank">Создать файл отчета</a>
        </div>
      </div>
      <table class="table table-hover">
        <thead>
        <tr>
          <th>Case #</th>
          <th>Name</th>
          <th>Phone</th>
          <th>Email</th>
          <th>Message</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${entrants}" var="entrant">
          <tr <c:if test="${not entrant.valid}">class="table-warning"</c:if> >
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.caseNumber}</a></td>
            <td><a href="<c:url value="/editEntrant.htm"><c:param name="id" value="${entrant.id}"/></c:url>">${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</a></td>
            <td>${entrant.phone}</td>
            <td>${entrant.email}</td>
            <td>${entrant.requests[0].response.response}</td>
          </tr>
        </c:forEach>
        </tbody>
      </table>

    </c:when>
  </c:choose>
</div>
