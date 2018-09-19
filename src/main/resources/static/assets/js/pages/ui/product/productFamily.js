$(function(){
    $.addModal({
        selector: '.js-add-productAttribute',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/attribute'),
        name:'product-attribute',
        title:'Product Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productAttributes');});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
    $.addModal({
        selector: '.js-add-variantAttribute',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/VARIANT/attribute'),
        name:'variant-attribute',
        title:'Variant Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantAttributes');});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});