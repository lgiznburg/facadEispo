<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h4>
  Load organization IDs from file:
</h4>
<form action="<c:url value="/loadOids.htm"/>" method="post"  enctype="multipart/form-data">
  <fieldset>

    <div  class="form-group row">
      <div class="col-5">
        <label>
          <span>Select a file:</span>
          <input type="file" name="oidsFile" class="form-control-file"/>
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

