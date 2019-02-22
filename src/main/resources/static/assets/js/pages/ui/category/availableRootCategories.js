$( document ).ready(function() {
    setTimeout(function(){
        $.initGrid({
            selector: '#paginatedAvailableRootCategoriesTable',
            names: ['availableRootCategories', 'availableRootCategory'],
            pageLength: 10,
            dataUrl: $.getURL('/pim/catalogs/{catalogId}/rootCategories/available/list'),
            columns: [
                { data: 'categoryName', name : 'categoryName' , title : 'Category Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small style="color:#808080">' + row.externalId + '</code><small>'}},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ],
            buttons: [$.addItemButton({action: addRootCategory})]
        });
    }, 200);


    function addRootCategory(row) {
        $.ajax({
            url: $.getURL('/pim/catalogs/{catalogId}/rootCategories/{rootCategoryId}', {'rootCategoryId': row.externalId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('rootCategoriesSortable,rootCategoriesReorderable,categoriesHierarchy');
                $.refreshDataTable('availableRootCategories');
                toastr.success('Successfully added the root category', 'Root Category Added');
            } else {
                toastr.error('Error occurred while adding the root category', 'Error Adding Root Category');
            }

        }).fail(function(jqXHR, status) {
            toastr.error('Error occurred while adding the root category', 'Error Adding Root Category');
        });
    }
});