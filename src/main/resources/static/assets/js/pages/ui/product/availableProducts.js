$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableProductsTable',
        name: 'availableProducts',
        type: 'TYPE_3',
        pageLength: 10,
        url: $.getURL('/pim/categories/{categoryId}/products/available/list'),
        columns: [
            { data: 'productName', name : 'productName' , title : 'Product Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small style="color:#808080">' + row.externalId + '</code><small>'}},
            { data: 'productFamilyId', name : 'productFamilyId', title : 'Product Family', visible: false },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableProductsTable').on('click', '.js-add', function(){
        var productId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/categories/{categoryId}/products/{productId}', {'productId': productId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('productsSortable,productsReorderable,availableProducts');
                toastr.success('Successfully added the category product', 'Category product Added');
            } else {
                toastr.success('Error occurred while adding the category product', 'Error Adding Category Product');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the category product', 'Error Adding Category Product');
        });
    });
});