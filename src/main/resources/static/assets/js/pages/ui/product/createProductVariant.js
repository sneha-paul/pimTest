$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableProductVariantsTable',
        name: 'availableProductVariants',
        type: 'TYPE_3A',
        url: $.getURL('/pim/products/{productId}/channel/{channelId}/variants/available/list'),
        columns: [
            { data: 'value', name : 'value' , title : 'Value'},
            { data: 'id', name : 'id', title : 'ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    /*$('#paginatedAvailableAttributeOptionsTable').on('click', '.js-add', function(){
        var optionId = $(this).data('id');

        $.ajax({
            url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options/{optionId}', {'optionId': optionId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('attributeOptions');
                $.refreshDataTable('availableAttributeOptions');
                toastr.success('Successfully added the option', 'Option Added');
            } else {
                toastr.success('Error occurred while adding the option', 'Error Adding Option');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the attribute option', 'Error Adding Option');
        });
    });*/
});