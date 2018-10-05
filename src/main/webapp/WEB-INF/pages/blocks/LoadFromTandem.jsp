<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="row">
  <c:if test="${not empty messages}">
    <div><h4>Результаты:</h4><p>
      <c:forEach items="${messages}" var="message" varStatus="indx">
        ${message}<br/>
      </c:forEach>
    </p></div>
  </c:if>
</div>
<h4>
  Load Students from file:
</h4>
<form action="<c:url value="/loadFormTandem.htm"/>" method="post"  enctype="multipart/form-data">
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
          <span>Load date of test only:</span>
          <input type="checkbox" name="loadTestDate" class="form-check-input" value="1"/>
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

<h4>
  Load Scores and Achievements:
</h4>
<form action="<c:url value="/loadFormTandem.htm"/>" method="post"  enctype="multipart/form-data">
  <fieldset>
    <input type="hidden" name="achievements" value="1">
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


<h4>
  Load Enrollment:
</h4>
<form action="<c:url value="/loadFormTandem.htm"/>" method="post"  enctype="multipart/form-data">
  <fieldset>
    <input type="hidden" name="enrollment" value="1">
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
