<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<tiles:insertDefinition name="mainPopupLayout">
    <tiles:putAttribute name="body">
        <div class="popup-content" style="padding:20px">
            <div class="body">
                <div class="table-responsive">
                <table class="table table-hover available-catalogs js-datatable dataTable table-custom">
                    <thead class="thead-dark">
                    <tr>
                        <th>Catalog Name</th>
                        <th>Catalog ID</th>
                        <th>Select</th>
                    </tr>
                    </thead>
                    <c:forEach items="${catalogs}" var="catalog">
                        <tr>
                            <td>${catalog.catalogName}</td>
                            <td>${catalog.catalogId}</td>
                            <td>
                                <label class="fancy-checkbox">
                                    <input class="js-add" data-catalog-id="${catalog.catalogId}" type="checkbox" name="active" value="Y">
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
                $('.availabel-catalogs').DataTable({
                    conditionalPaging: true
                });
                $('.js-add').on('click', function(){
                    var catalogId = $(this).data('catalog-id');

                    $.ajax({
                        url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}', {'catalogId': catalogId}),
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