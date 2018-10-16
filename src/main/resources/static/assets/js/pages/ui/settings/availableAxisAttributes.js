$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableAxisAttributesTable',
        name: 'availableAxisAttributes',
        type: 'TYPE_3A',
        url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available/list'),
        columns: [
            {data: 'name', name: 'name', title: 'Name'},
            {data: 'id', name: 'id', title: 'ID'},
            {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableAxisAttributesTable').on('click', '.js-add', function(){
        var attributeId = $(this).data('id');

        $.ajax({
            url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes/{attributeId}', {'attributeId': attributeId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('axisAttributes');
                $.refreshDataTable('availableAxisAttributes');
                toastr.success('Successfully set the axis attribute', 'Axis Attribute Set');
            } else {
                toastr.success('Error occurred while setting the axis attribute', 'Error Setting Attribute');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while setting the axis attribute', 'Error Setting Attribute');
        });
    });
});