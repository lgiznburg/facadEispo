<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="container">

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
            <td>${entrant.caseNumber}</td>
            <td>${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</td>
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
