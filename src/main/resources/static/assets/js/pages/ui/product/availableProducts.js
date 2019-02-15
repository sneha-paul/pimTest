$( document ).ready(function() {
    $.initGrid({
        selector: '#paginatedAvailableProductsTable',
        names: ['availableProducts', 'availableProduct'],
        pageLength: 10,
        dataUrl: $.getURL('/pim/categories/{categoryId}/products/available/list'),
        columns: [
            { data: 'productName', name : 'productName' , title : 'Parent Product Name', width:'80%', render: function ( data, type, row, meta ) {let imgUrl = row.imageName === 'noimage.png' ? '/assets/img/' + row.imageName : '/uploads/' + row.imageName;
                return '<div class="grid-image-holder pull-left rounded"><img  src="' + imgUrl + '" data-toggle="' + data + '" data-placement="top" title="" alt="" class="grid-main-img rounded"></div><div class="pull-left"><h6>' + data + '</h6><small style="color:#808080">' + row.externalId + '</code><small>'}},
            { data: 'productFamilyId', name : 'productFamilyId', title : 'Product Family', visible: false },
            { data: 'actions', name : 'actions' , title : 'Actions', width:'10%', orderable: false}
        ],
        buttons: [$.addItemButton({action: addProduct})]
    });

    function addProduct(row) {
        $.ajax({
            url: $.getURL('/pim/categories/{categoryId}/products/{productId}', {'productId': row.externalId}),
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
    }
});