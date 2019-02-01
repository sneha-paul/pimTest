$( document ).ready(function() {
    $.initGrid({
        selector: '#paginatedAvailableAttributeOptionsTable',
        names: ['availableAttributeOptions', 'availableAttributeOption'],
        dataUrl: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options/available/list'),
        columns: [
            { data: 'value', name : 'value' , title : 'Value'},
            { data: 'id', name : 'id', title : 'ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ],
        buttons: [$.addItemButton({action: addOption})]
    });

    function addOption(row) {
        $.ajax({
            url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options/{optionId}', {'optionId': row.id}),
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
    }
});