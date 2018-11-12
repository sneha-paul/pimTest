$( document ).ready(function() {
    $.addModal({
        selector: '#js-add-pricing-details',
        url: $.getURL('/pim/products/{productId}/variants/{productVariantId}/pricingDetails?ts=' + new Date().getTime()),
        data: {channelId: $.getPageAttribute('channelId')},
        name:'add-pricing-details',
        title:'Add Pricing Details',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
    $.addModal({
        selector: '#js-edit-quantity-breaks',
        url: $.getURL('/pim/products/{productId}/variants/{productVariantId}/quantityBreaks?ts=' + new Date().getTime()),
        data: {channelId: $.getPageAttribute('channelId')},
        name:'edit-quantity-breaks',
        title:'Quantity Breaks',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});
//# sourceURL=/assets/js/pages/ui/product/productVariant.js