<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<script>

  $( function(){
    var from =$("#deception.birthDate").datepicker();
    $("#deception.birthDate").datepicker("option", $.datepicker.regional[ "ru" ]);

  });
</script>


<h4>Редактирование №${entrant.caseNumber}  ${entrant.lastName} ${entrant.firstName} ${entrant.middleName}</h4>

<form:form commandName="entrant" action="editEntrant.htm" method="post" name="entrant">
  <form:hidden path="id"/>
  <div class="form-group row">
    <form:label path="deception.lastName" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Фамилия</form:label>
    <div class="col-sm-5">
      <form:input path="deception.lastName"  cssClass="form-control"/>
      <form:errors path="deception.lastName" cssClass="text-danger" element="span"/>
    </div>
    <div class="col-sm-2">${entrant.lastName}</div>
  </div>
  <div class="form-group row">
    <form:label path="deception.firstName" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Имя</form:label>
    <div class="col-sm-5">
      <form:input path="deception.firstName"  cssClass="form-control"/>
      <form:errors path="deception.firstName" cssClass="text-danger" element="span"/>
    </div>
    <div class="col-sm-2">${entrant.firstName}</div>
  </div>
  <div class="form-group row">
    <form:label path="deception.middleName" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Отчество</form:label>
    <div class="col-sm-5">
      <form:input path="deception.middleName"  cssClass="form-control"/>
      <form:errors path="deception.middleName" cssClass="text-danger" element="span"/>
    </div>
    <div class="col-sm-2">${entrant.middleName}</div>
  </div>
  <div class="form-group row">
    <form:label path="deception.birthDate" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Дата рождения</form:label>
    <div class="col-sm-5">
      <form:input path="deception.birthDate"   cssClass="form-control"/>
      <form:errors path="deception.birthDate" cssClass="text-danger" element="span"/>
    </div>
    <div class="col-sm-2"><fmt:formatDate value="${entrant.birthDate}" pattern="dd.MM.yyyy"/> </div>
  </div>

  <div class="form-group row">
    <form:label path="status" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Status</form:label>
    <div class="col-sm-5">
      <form:select path="status"  cssClass="form-control">
        <form:options items="${entrantStatuses}"/>
      </form:select>
    </div>
  </div>

  <c:forEach items="${entrant.requests}" var="request" varStatus="indx">
    <div class="row">
      <div class="col-sm-2">${indx.index}</div>
      <div class="col-sm-5">${request.speciality}, ${request.financing}, целевое - ${request.targetRequest}</div>
      <div class="col-sm-5">${request.response.response}</div>
    </div>
    <div class="form-group row">
      <form:label path="requests[${indx.index}].status" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Status</form:label>
      <div class="col-sm-5">
        <form:select path="requests[${indx.index}].status"  cssClass="form-control">
          <form:options items="${requestStatuses}"/>
        </form:select>
      </div>
    </div>
  </c:forEach>

  <div class="form-group row">
    <form:label path="examInfo.organization" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Organization</form:label>
    <div class="col-sm-5">
      <form:input path="examInfo.organization"  cssClass="form-control"/>
      <form:errors path="examInfo.organization" cssClass="text-danger" element="span"/>
    </div>
  </div>
  <div class="form-group row">
    <form:label path="examInfo.type" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Имя</form:label>
    <div class="col-sm-5">
      <form:input path="examInfo.type"  cssClass="form-control"/>
      <form:errors path="examInfo.type" cssClass="text-danger" element="span"/>
    </div>
  </div>
  <div class="form-group row">
    <form:label path="examInfo.year" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Имя</form:label>
    <div class="col-sm-5">
      <form:input path="examInfo.year"  cssClass="form-control"/>
      <form:errors path="examInfo.year" cssClass="text-danger" element="span"/>
    </div>
  </div>

<%--
  <div class="form-group row">
    <div class="col">${entrant.examInfo.organization} </div>
    <div class="col">${entrant.examInfo.type}</div>
    <div class="col">${entrant.examInfo.year}</div>
  </div>
--%>

  <div class="form-group row">
    <%--<form:label path="examInfo.score" cssClass="col-sm-2 col-form-label" cssErrorClass="col-sm-2 col-form-label text-danger">Баллы</form:label>
    <div class="col-sm-5">
      <form:input path="examInfo.score"  cssClass="form-control"/>
      <form:errors path="examInfo.score" cssClass="text-danger" element="span"/>
    </div>--%>
      <div class="col-sm-4">Баллы: ${entrant.examInfo.score}</div>
      <div class="col-sm-5">${entrant.examInfo.response}</div>
  </div>

  <div class="form-group row">
    <div class="col-sm-7">
      <a class="btn btn-outline-success" href="<c:url value="/home.htm"/>">Назад</a>
      <button type="submit" class="btn btn-primary">Сохранить</button>
    </div>
  </div>
</form:form>
