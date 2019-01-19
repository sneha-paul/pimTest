$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableRootCategoriesTable',
        name: 'availableRootCategories',
        type: 'TYPE_3',
        pageLength: 10,
        url: $.getURL('/pim/catalogs/{catalogId}/rootCategories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small style="color:#808080">' + row.externalId + '</code><small>'}},
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
                $.refreshDataTable('rootCategoriesSortable,rootCategoriesReorderable,categoriesHierarchy');
                $.refreshDataTable('availableRootCategories');
                toastr.success('Successfully added the root category', 'Root Category Added');
            } else {
                toastr.success('Error occurred while adding the root category', 'Error Adding Root Category');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the root category', 'Error Adding Root Category');
        });
    });
});