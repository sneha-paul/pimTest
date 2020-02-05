<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Param"/>
            <tiles:putAttribute name="body" value="/config/param_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>
        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <form method="post" action="/pim/configs/{configId}/params" data-method="PUT" data-success-message='["Successfully created the parameter", "Parameter Created"]' data-error-message='["Correct the validation error and try again", "Invalid Data"]' >
                    <div class="row">
                        <div class="col-md-6 col-sm-12">
                            <div class="form-group js-name">
                                <label>Parameter Name</label>
                                <input type="text" name="paramName" class="form-control" />
                            </div>
                            <div class="form-group">
                                <label>Parameter Value</label>
                                <input type="text" name="paramValue" class="form-control" />
                            </div>
                        </div>
                    </div>
                    <br>
                    <input type="hidden" name="group" value="PARAMS"/>
                    <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
                </form>
            </div>
        </div>
    </c:otherwise>
</c:choose>