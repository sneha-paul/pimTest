<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>

<tiles:insertDefinition name="mainLayout">
    <tiles:putAttribute name="title" value="BLACWOOD"/>
    <tiles:putListAttribute name="pageStyles">
        <tiles:addAttribute>/assets/css/c1.css</tiles:addAttribute>
        <tiles:addAttribute>/assets/css/c2.css</tiles:addAttribute>
    </tiles:putListAttribute>
    <tiles:putListAttribute name="pageTopScripts">
        <tiles:addAttribute>/assets/js/t1.js</tiles:addAttribute>
        <tiles:addAttribute>/assets/js/t2.js</tiles:addAttribute>
    </tiles:putListAttribute>
    <tiles:putAttribute name="body" value="/product/products_body.jsp"/>
    <%--<tiles:putListAttribute name="pageBottomScripts">
        <tiles:addAttribute>/assets/js/f1.js</tiles:addAttribute>
        <tiles:addAttribute>/assets/js/f2.js</tiles:addAttribute>
    </tiles:putListAttribute>--%>
    <tiles:putAttribute name="pageBottomScripts">
        <script type="javascript" src="/assets/js/f1/js"></script>
        <script type="javascript" src="/assets/js/f2/js"></script>
    </tiles:putAttribute>
    <tiles:putAttribute name="script">
        <script>console.log('hi');</script>
    </tiles:putAttribute>
</tiles:insertDefinition>