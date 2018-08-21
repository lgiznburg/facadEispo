<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row">
  <c:if test="${not empty script}">
    <div><h4>Результаты:</h4><p>
      <c:forEach items="${script}" var="message" varStatus="indx">
        ${message}<br/>
      </c:forEach>
    </p></div>
  </c:if>
</div>
<h4>
  Create script from file:
</h4>
<form action="<c:url value="/createScoresScript.htm"/>" method="post"  enctype="multipart/form-data">
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
        <label>
          <span>Number of tabs:</span>
          <input type="text" name="tabNumber" class="form-control" value="2"/>
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
