$( document ).ready(function() {
    $.initGrid({
        selector: '#paginatedAvailableSubCategoriesTable',
        names: ['availableSubCategories', 'availableSubCategory'],
        pageLength: 10,
        dataUrl: $.getURL('/pim/categories/{categoryId}/subCategories/available/list'),
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small style="color:#808080">' + row.externalId + '</code><small>'}},
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ],
        buttons: [$.addItemButton({action: addSubCategory})]
    });

    function addSubCategory(row) {
        $.ajax({
            url: $.getURL('/pim/categories/{categoryId}/subCategories/{subCategoryId}', {'subCategoryId': row.externalId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('subCategoriesSortable,subCategoriesReorderable,availableSubCategories');
                toastr.success('Successfully added the sub category', 'Sub Category Added');
            } else {
                toastr.success('Error occurred while adding the sub category', 'Error Adding Sub Category');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the sub category', 'Error Adding Sub Category');
        });
    }
});