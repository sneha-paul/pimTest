<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertDefinition name="mainLayout">
    <tiles:putAttribute name="title" value="PIM - Products"/>
    <tiles:putAttribute name="body" value="/product/product_body.jsp"/>
    <%--<tiles:putListAttribute name="pageBottomScripts">
        <tiles:addAttribute>/assets/js/pages/ui/website/website.js</tiles:addAttribute>
    </tiles:putListAttribute>--%>
    <%--<tiles:putAttribute name="script">
        <script>
            $(function() {
                // validation needs name of the element
                $('#food').multiselect();

                // initialize after multiselect
                $('#basic-form').parsley();

                tableDragger(document.querySelector("#only-bodytable"), { mode: "row", onlyBody: true });
            });
        </script>
    </tiles:putAttribute>--%>
</tiles:insertDefinition>