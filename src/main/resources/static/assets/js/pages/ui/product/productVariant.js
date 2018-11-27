$( document ).ready(function() {
    $.addModal({
        selector: '#js-add-pricing-details',
        url: $.getURL('/pim/products/{productId}/variants/{productVariantId}/pricingDetails?ts=' + new Date().getTime()),
        data: {channelId: $.getPageAttribute('channelId')},
        name:'add-pricing-details',
        title:'Add Pricing Details',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantPricing');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});
//# sourceURL=/assets/js/pages/ui/product/productVariant.js