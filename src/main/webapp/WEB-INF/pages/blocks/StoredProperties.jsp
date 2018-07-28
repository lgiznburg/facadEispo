<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

    <h2>List of System Properties</h2>

    <table class="table">
      <tr>
        <th>Property Group</th>
        <th>Property Name</th>
        <th>Property Value</th>
      </tr>

      <c:set value="" var="group"/>
      <c:forEach items="${storedProperties}" var="storedProperty">
        <tr <c:if test="${group ne storedProperty.propertyName.groupName}">style="border-top: solid black 1px"</c:if> >
          <td>
            <c:if test="${group ne storedProperty.propertyName.groupName}">
              ${storedProperty.propertyName.groupName}
              <c:set var="group" value="${storedProperty.propertyName.groupName}"/>
            </c:if>&nbsp;
          </td>
          <td>
            <c:url value="/storedPropertyEdit.htm" var="editUrl"><c:param name="propertyName" value="${storedProperty.propertyName}"/></c:url>
            <c:choose>
              <c:when test="${storedProperty.propertyName.editable}"><a href="${editUrl}">${storedProperty.propertyName.name}</a></c:when>
              <c:otherwise>${storedProperty.propertyName.name}</c:otherwise>
            </c:choose>

          </td>
          <td>
            <c:choose>
              <c:when test="${storedProperty.propertyName.editable}"><a href="${editUrl}">${storedProperty.value}</a></c:when>
              <c:otherwise>${storedProperty.value}</c:otherwise>
            </c:choose>

          </td>
        </tr>
      </c:forEach>
    </table>

