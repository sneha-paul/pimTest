$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableSubCategoriesTable',
        name: 'availableSubCategories',
        type: 'TYPE_3',
        url: $.getURL('/pim/categories/{categoryId}/subCategories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableSubCategoriesTable').on('click', '.js-add', function(){
        var subCategoryId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/categories/{categoryId}/subCategories/{subCategoryId}', {'subCategoryId': subCategoryId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('subCategories');
                $.refreshDataTable('availableSubCategories');
                toastr.success('Successfully added the sub category', 'Sub Category Added');
            } else {
                toastr.success('Error occurred while adding the sub category', 'Error Adding Sub Category');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the sub category', 'Error Adding Sub Category');
        });
    });
});