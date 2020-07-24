<%--
  Created by IntelliJ IDEA.
  User: leonid
  Date: 13.07.2020
  Time: 18:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="container">

  <c:if test="${not empty errors}">
    <div class="row">
      <div class="col">
        <c:forEach items="${errors}" var="errorInfo">
          <p class="text-info"> ${errorInfo}</p>
        </c:forEach>
      </div>
    </div>
  </c:if>

  <form action="<c:url value="/mailingToList.htm"/>"  method="post" class="form-inline">
    <fieldset>
    <div  class="form-group row">
      <div class="col-sm-4">
        Список номеров дел
      </div>
      <div class="col-sm-8">
        <input type="text" name="incomingList" placeholder="Список номеров дел" class="form-control"/>
      </div>
    </div>
      <div  class="form-group row">
        <div class="col-sm-4">
          Тип письма
        </div>
        <div class="col-sm-8">
          <select name="emailType" class="form-control">
            <c:forEach items="${emailsList}" var="emailType">
            <option value="${emailType}">${emailType}</option>
            </c:forEach>
          </select>
        </div>
      </div>
      <div  class="form-group row">
        <div class="col-sm-8 offset-4">
          <input type="submit" value="Отправить письма" class="btn btn-primary"/>
        </div>
      </div>
    </fieldset>
  </form>

</div>