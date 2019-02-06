$( document ).ready(function() {
    var columns = [];
    var i = 0;
    var axisAttributes = $.getPageAttribute('axisAttributes');
    for(var prop in axisAttributes) {
        if(axisAttributes.hasOwnProperty(prop)) {
            columns[i++] = {data: prop, name: prop, title: axisAttributes[prop]};
        }
    }
    columns[i] = { data: 'actions', name : 'actions' , title : 'Actions', orderable: false};

    setTimeout(function(){
        $.initGrid({
            selector: '#paginatedAvailableProductVariantsTable',
            names: ['availableProductVariants', 'availableProductVariant'],
            dataUrl: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/available/list'),
            columns: columns,
            buttons: [$.addItemButton({action: createVariant})]
        });
    }, 200);

    function createVariant(row) {
        $.ajax({
            url: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/{variantIdentifier}', {'variantIdentifier': row.id}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('productVariants');
                $.refreshDataTable('availableProductVariants');
                toastr.success('Successfully created the variant', 'Variant Created');
            } else {
                toastr.success('Error occurred while creating the variant', 'Error Creating Variant');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while creating the variant', 'Error Creating Variant');
        });
    }
});