$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableRootCategoriesTable',
        name: 'availableRootCategories',
        type: 'TYPE_3',
        url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}/rootCategories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableRootCategoriesTable').on('click', '.js-add', function(){
        var rootCategoryId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}/rootCategories/{rootCategoryId}', {'rootCategoryId': rootCategoryId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('rootCategories');
                $.refreshDataTable('availableRootCategories');
                toastr.success('Successfully added the root category', 'Root Category Added');
            } else {
                toastr.error('Error occurred while adding the root category', 'Error Adding Root Category');
            }

        }).fail(function(jqXHR, status) {
            toastr.error('Error occurred while adding the root category', 'Error Adding Root Category');
        });
    });
});