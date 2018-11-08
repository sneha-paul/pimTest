$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableCategoriesTable',
        name: 'availableCategories',
        type: 'TYPE_3',
        url: $.getURL('/pim/products/{productId}/categories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableCategoriesTable').on('click', '.js-add', function(){
        var categoryId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/products/{productId}/categories/{categoryId}', {'categoryId': categoryId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('categories');
                $.refreshDataTable('availableCategories');
                toastr.success('Successfully added the product category', 'Product category Added');
            } else {
                toastr.success('Error occurred while adding the product category', 'Error Adding Product category');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the product category', 'Error Adding Product category');
        });
    });
});