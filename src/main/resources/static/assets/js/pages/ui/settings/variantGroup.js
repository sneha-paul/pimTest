$(function(){
    $.addModal({
        selector: '.js-add-axisAttribute',
        url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available'),
        name:'axis-attribute',
        title:'Available Variant Axis Attributes',
        buttons: [
            // {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('axisAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    /*$.addModal({
        selector: '.js-add-variantGroup',
        url: $.getURL('/pim/families/{familyId}/variantGroups/create'),
        name:'variant-group',
        title:'Create Variant Group',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantGroups');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });*/
});