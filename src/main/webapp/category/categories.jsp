<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertDefinition name="mainLayout">
    <tiles:putAttribute name="title" value="PIM - Categories"/>
    <tiles:putListAttribute name="pageStyles">
        <tiles:addAttribute>/assets/vendor/bootstrap-treeview/bootstrap-treeview.min.css</tiles:addAttribute>
    </tiles:putListAttribute>
    <tiles:putAttribute name="body" value="/category/categories_body.jsp"/>
    <tiles:putListAttribute name="pageBottomScripts">
        <%--<tiles:addAttribute>/assets/vendor/bootstrap-treeview/bootstrap-treeview.js</tiles:addAttribute>--%>
        <%--<tiles:addAttribute>/assets/js/f1.js</tiles:addAttribute>--%>
    </tiles:putListAttribute>
    <tiles:putAttribute name="script">
        <script>
            $(function() {

            });
        </script>
    </tiles:putAttribute>
</tiles:insertDefinition>