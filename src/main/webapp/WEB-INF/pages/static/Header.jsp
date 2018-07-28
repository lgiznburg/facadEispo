<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>


<div class="navbar navbar-expand-lg navbar-dark bg-dark" role="navigation">
  <a class="navbar-brand" href="<c:url value="/home.htm"/>">
    <img src="<c:url value="/resources/img/logoRNIMUwhite.svg"/>" width="120px">
  </a>

  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsReque" aria-controls="navbarsReque" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>

  <div class="collapse navbar-collapse" id="navbarsReque">
    <%--<sec:authorize access="isAnonymous()">
      <ul class="navbar-nav ml-auto">
        <li class="nav-item"><a class="nav-link" href="<c:url value="/Registration.htm"/>">Зарегистрироваться</a></li>
      </ul>

        <form action="<c:url value="/j_spring_security_check"/>" class="form-inline mt-2 mt-md-0" method="post">
          <input type="text" class="form-control mr-sm-2" placeholder="Email" name="j_username" >
          <input type="password" class="form-control mr-sm-2" placeholder="Пароль" name="j_password" >
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <button type="submit" class="btn btn-primary my-2 my-sm-0">Войти</button>
        </form>
    </sec:authorize>
    <sec:authorize access="isAuthenticated()">
        <ul class="navbar-nav ml-auto">
          <li class="nav-item dropdown">
            <a href="#" class="nav-link dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" id="userDropdown" aria-expanded="false">Добро пожаловать, ${user.firstName}</a>
            <div class="dropdown-menu bg-light" aria-labelledby="userDropdown">
              <sec:authorize access="hasAnyRole('ROLE_CLIENT','ROLE_ADMIN')">
                <c:choose>
                  <c:when test="${empty appointment}">
                    <a class="dropdown-item" href="<c:url value="SelectCampaign.htm"/> ">Предварительная запись</a>
                  </c:when>
                  <c:otherwise>
                    <a class="dropdown-item" href="<c:url value="/CreateAppointment.htm"><c:param name="id" value="${appointment.id}"/></c:url>">Изменить Вашу запись.</a>
                  </c:otherwise>
                </c:choose>
              </sec:authorize>
              <a class="dropdown-item" href="<c:url value="/ChangePassword.htm"/>">Изменить пароль</a>
              <a class="dropdown-item" href="<c:url value="/EditUser.htm"/>">Изменить данные</a>
              <div class="dropdown-divider"></div>
              <a class="dropdown-item" onclick="$('#logout').submit();return false;">Выйти</a>
            </div>
          </li>
        </ul>
      <form action="<c:url value="/logout"/>" method="post" id="logout">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
      </form>

    </sec:authorize>--%>

  </div>
</div>
