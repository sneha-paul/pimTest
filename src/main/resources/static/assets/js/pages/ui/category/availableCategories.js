$( document ).ready(function() {
    $.initGrid({
        selector: '#paginatedAvailableCategoriesTable',
        names: ['availableCategories', 'availableCategory'],
        dataUrl: $.getURL('/pim/products/{productId}/categories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ],
        buttons: [$.addItemButton({action: addCategory})]
    });

    function addCategory(row) {
        $.ajax({
            url: $.getURL('/pim/products/{productId}/categories/{categoryId}', {'categoryId': row.externalId}),
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
    }
});