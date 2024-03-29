<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html>
<head>
  <title>Print Errors</title>
</head>
<body  style="font-size: large">

<table width="800" style="font-size: large">
  <thead>
  <tr>
    <th>Дело #</th>
    <th>Имя</th>
    <th>Тел</th>
    <th>Данные РНИМУ</th>
    <th>Описание</th>
  </tr>
  </thead>

  <tbody>
  <c:forEach items="${entrants}" var="entrant">
    <tr>
      <td>${entrant.caseNumber}</td>
      <td>${entrant.lastName}<br/> ${entrant.firstName}<br/> ${entrant.middleName}<br/>
          <fmt:formatDate value="${entrant.birthDate}" pattern="dd.MM.yyyy"/> </td>
      <td>${entrant.phone}<br>${entrant.email}</td>
      <td>${entrant.examInfo.type}, ${entrant.examInfo.year}, ${entrant.examInfo.organization}</td>
      <td>${entrant.errorInfo}</td>
    </tr>
  </c:forEach>
  </tbody>
</table>

</body>
</html>