$( document ).ready(function() {console.log('inside');
    $.initDataTable({
        selector: '#paginatedAvailableRootCategoriesTable',
        name: 'availableRootCategories',
        type: 'TYPE_3',
        url: $.getURL('/pim/catalogs/{catalogId}/rootCategories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableRootCategoriesTable').on('click', '.js-add', function(){
        var rootCategoryId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/catalogs/{catalogId}/rootCategories/{rootCategoryId}', {'rootCategoryId': rootCategoryId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('rootCategories');
                $.refreshDataTable('availableRootCategories');
            } else {
                alert('Failed');
            }

        }).fail(function(jqXHR, status) {
            alert("Failed:" + status);
        });
    });
});
var state420 = '';