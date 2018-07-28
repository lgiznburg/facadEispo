<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


    <h2>Изменить свойство</h2>

<form:form commandName="storedProperty" action="StoredPropertyEdit.htm" method="post" name="storedProperty">
  <form:hidden path="propertyName"/>
  <div class="form-group row">
    <div class="col-sm-2">${storedProperty.propertyName.groupName}</div>
    <div class="col-sm-2">${storedProperty.propertyName.name}</div>
    <div class="co-s-5">
      <form:input path="value" cssClass="form-control"/>
      <form:errors path="value" cssClass="text-danger"/>
    </div>
  </div>

  <div class="form-group row">
    <div class="col-sm-5">
      <a class="btn btn-outline-success" href="<c:url value="/storedProperties.htm"/>">Назад</a>
      <button type="submit" class="btn btn-primary">Сохранить</button>
    </div>
  </div>
</form:form>

