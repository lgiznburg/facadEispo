<%--
  Created by IntelliJ IDEA.
  User: leonid
  Date: 17.12.17
  Time: 21:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html>
<head>
  <title>${title}</title>
  <meta name="viewport" content="width=device-width, initial-scale=1.0">

  <link rel="icon" href="<c:url value="/resources/img/favicon.ico"/>">
  <link rel="stylesheet" href="<c:url value="/resources/css/jquery-ui.min.css"/>"/>
  <!-- Bootstrap -->
  <link href="<c:url value="/resources/css/bootstrap.css"/>" rel="stylesheet" media="screen">

  <%--<link href="<c:url value="/resources/css/reque.css"/>" rel="stylesheet">--%>

  <script src="<c:url value="/resources/js/jquery.js"/>"></script>
  <script src="<c:url value="/resources/js/jquery-ui.min.js"/> "></script>
  <script src="<c:url value="/resources/js/datepicker-ru.js"/> "></script>

</head>
<body >

<c:import url="/WEB-INF/pages/static/Header.jsp"/>

<div class="container" role="main">

  <%--<sec:authorize access="hasAnyRole('ROLE_ADMIN','ROLE_SERVICEMAN')">--%>
    <div class="container-fluid">
      <div class="row">
        <div class="col-sm-2 sidebar">
          <c:import url="/WEB-INF/pages/static/AdminMenu.jsp"/>
        </div>
        <div class="col-sm-10">
          <c:import url="${content}"/>
        </div>
      </div>
    </div>

    <%--<div class="row">
      <c:import url="/WEB-INF/pages/static/AdminMenu.jsp"/>
    </div>
    <div class="row">
      <c:import url="${content}"/>
    </div>--%>
  <%--</sec:authorize>--%>

<%--
  <sec:authorize access="not hasAnyRole('ROLE_ADMIN','ROLE_SERVICEMAN')">
    <c:import url="${content}"/>
  </sec:authorize>
--%>

</div>


<!-- Bootstrap core JavaScript
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->
<script src="<c:url value="/resources/js/popper.min.js"/> "></script>
<script src="<c:url value="/resources/js/bootstrap.min.js"/> "></script>
</body>
</html>
