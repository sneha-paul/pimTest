$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableProductsTable',
        name: 'availableProducts',
        type: 'TYPE_3',
        url: $.getURL('/pim/websites/{websiteId}/products/available/list'),
        columns: [
            { data: 'productName', name : 'productName' , title : 'Product Name'},
            { data: 'externalId', name : 'externalId', title : 'Product ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableProductsTable').on('click', '.js-add', function(){
        var productId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/websites/{websiteId}/products/{productId}', {'productId': productId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('products');
                $.refreshDataTable('availableProducts');
                toastr.success('Successfully added the category product', 'Category product Added');
            } else {
                toastr.success('Error occurred while adding the category product', 'Error Adding Category Product');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the category product', 'Error Adding Category Product');
        });
    });
});