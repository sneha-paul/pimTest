<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertDefinition name="mainLayout">
    <tiles:putAttribute name="title" value="PIM - Sync Status"/>
    <tiles:putAttribute name="body" value="/syncStatus/syncStatuses_body.jsp"/>
</tiles:insertDefinition>