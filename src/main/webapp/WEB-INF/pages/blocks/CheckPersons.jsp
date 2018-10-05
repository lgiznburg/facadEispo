<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:if test="${not empty entrants}">
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
      <tr <c:if test="${entrant.enrollment}">class="table-success"</c:if> >
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

</c:if>

<h4>
  Load persons:
</h4>
<form action="<c:url value="/checkPersons.htm"/>" method="post"  enctype="multipart/form-data">
  <fieldset>
    <div  class="form-group row">
      <div class="col-5">
        <label>
          <span>Select a file:</span>
          <input type="file" name="studentsFile" class="form-control-file"/>
        </label>
      </div>
    </div>

    <div  class="form-group row">
      <div class="col-5">
        <input type="submit" value="Submit" class="btn btn-primary"/>
      </div>
    </div>
  </fieldset>
</form>
