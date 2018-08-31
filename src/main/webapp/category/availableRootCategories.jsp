<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertDefinition name="mainPopupLayout">
    <tiles:putAttribute name="body">
        <div class="card popup-content">
            <div class="body">
                <div class="table-responsive">
                    <table class="table table-hover available-categories dataTable table-custom">
                        <thead class="thead-dark">
                        <tr>
                            <th>Category Name</th>
                            <th>Category ID</th>
                            <th>Select</th>
                        </tr>
                        </thead>
                        <c:forEach items="${categories}" var="category">
                            <tr>
                                <td>${category.categoryName}</td>
                                <td>${category.categoryId}</td>
                                <td>
                                    <label class="fancy-checkbox">
                                        <input class="js-add" data-category-id="${category.categoryId}" type="checkbox" name="active" value="Y">
                                        <span></span>
                                    </label>
                                </td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
            </div>
        </div>
    </tiles:putAttribute>
    <tiles:putAttribute name="script">
        <script>
            $(function() {
                $('.availabel-categories').DataTable({
                    conditionalPaging: true
                });
                $('.js-add').on('click', function(){
                    var childId = $(this).data('category-id');

                    $.ajax({
                        url: $.getURL('/pim/catalogs/{catalogId}/category/{categoryId}', {'categoryId': childId}),
                        data: {},
                        method: 'POST',
                        dataType: 'json'
                    }).done(function(data) {
                        if(data.success == true) {
                            window.location.reload();
                        } else {
                            alert('Failed');
                        }

                    }).fail(function(jqXHR, status) {
                        alert("Failed:" + status);
                    });
                });
            });
        </script>
    </tiles:putAttribute>
</tiles:insertDefinition>