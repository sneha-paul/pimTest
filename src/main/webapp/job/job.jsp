<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Jobs"/>
            <tiles:putAttribute name="body" value="/job/job_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
</c:choose>