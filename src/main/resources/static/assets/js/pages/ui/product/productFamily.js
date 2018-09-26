$(function(){
    $.addModal({
        selector: '.js-add-productAttribute',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/attribute'),
        name:'product-attribute',
        title:'Product Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
    $.addModal({
        selector: '.js-add-variantAttribute',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/VARIANT/attribute'),
        name:'variant-attribute',
        title:'Variant Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $.addModal({
        selector: '.js-add-productFeature',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/feature'),
        name:'product-feature',
        title:'Product Feature',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productFeatures');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
    $.addModal({
        selector: '.js-add-variantFeature',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/VARIANT/feature'),
        name:'variant-feature',
        title:'Variant Feature',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantFeatures');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});