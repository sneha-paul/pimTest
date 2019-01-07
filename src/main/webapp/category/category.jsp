<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:choose>
    <c:when test="${mode eq 'DETAILS'}">
        <tiles:insertDefinition name="mainLayout">
            <tiles:putAttribute name="title" value="PIM - Categories"/>
            <tiles:putAttribute name="body" value="/category/category_body.jsp"/>
        </tiles:insertDefinition>
    </c:when>
    <c:otherwise>

        <div class="row clearfix">
            <div class="col-lg-12 col-md-12">
                <div class="card">
                    <div class="body">
                        <form method="post" action="/pim/categories" data-method="POST"
                              data-success-message='["Successfully created the category", "Category Created"]'
                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                            <div class="row">
                                <div class="col-md-6 col-sm-12">
                                    <div class="form-group js-name">
                                        <label>Category Name</label><code class="highlighter-rouge m-l-10">*</code>
                                        <input type="text" name="categoryName" class="form-control">
                                    </div>

                                    <div class="form-group js-external-id">
                                        <label>Category ID</label><code class="highlighter-rouge m-l-10">*</code>
                                        <input type="text" name="categoryId" class="form-control">
                                    </div>

                                    <div class="form-group">
                                        <label>Category Description</label>
                                        <textarea class="form-control" name="description" rows="5" cols="30"></textarea>
                                    </div>
                                </div>
                            </div>
                            <br>
                            <input type="hidden" name="group" value="CREATE"/>
                            <img src="/assets/img/tiny.png" onload="$.initAHAH(this)"/>
                        </form>

                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>