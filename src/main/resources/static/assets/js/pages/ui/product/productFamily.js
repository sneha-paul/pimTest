$(function(){
    $.addModal({
        selector: '.js-add-familyAttribute',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/attribute'),
        name:'family-attribute',
        title:'Family Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('familyAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
    $.addModal({
        selector: '.js-add-variantGroup',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/variantGroup'),
        name:'variantGroup',
        title:'Variant Group',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantGroups');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});