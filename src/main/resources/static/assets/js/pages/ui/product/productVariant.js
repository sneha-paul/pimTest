$( document ).ready(function() {
    $.extend({
        setAsDefaultAsset: function(assetId) {
            $.ajaxSubmit({
                url: '/pim/products/{productId}/variants/{productVariantId}/assets/setDefault',
                data: {assetFamily: 'ASSETS', assetId: assetId, channelId: $.getPageAttribute('channelId')},
                method: 'PUT',
                successMessage: ['Asset Updated', 'Successfully set the default asset'],
                errorMessage: ['Error', 'Error occurred while setting the default asset'],
                successCallback: function(data) {
                    $.refreshPage();
                }
            });
        },
        addAssets: function(assetIds) {console.log(assetIds);
            $.ajaxSubmit({
                url: '/pim/products/{productId}/variants/{productVariantId}/assets',
                data: {assetFamily: 'ASSETS', assetIds: assetIds, channelId: $.getPageAttribute('channelId')},
                successMessage: ['Assets Added', 'Successfully added the assets'],
                errorMessage: ['Error', 'Error occurred while adding assets'],
                successCallback: function(data) {
                    $.refreshPage();
                    $.closeModal();
                }
            });
        },
        reorderAssets: function(assetIds) {
            $.ajaxSubmit({
                url: '/pim/products/{productId}/variants/{productVariantId}/assets/reorder',
                data: {assetFamily: 'ASSETS', assetIds: assetIds, channelId: $.getPageAttribute('channelId')},
                method: 'PUT',
                successMessage: ['Reordered Assets', 'Successfully updated the asset sequences'],
                errorMessage: ['Error', 'Error occurred while updating the asset sequences'],
                successCallback: function(data) {
                    $.refreshPage();
                }
            });
        },
        deleteAsset: function(assetId) {
            $.confirmedAJAXRequest({
                url: $.getURL('/pim/products/{productId}/variants/{productVariantId}/assets'),
                data: 'assetFamily=ASSETS&assetId=' + assetId + '&channelId=' + $.getPageAttribute('channelId'),
                method: 'DELETE',
                text: 'This will delete the asset',
                confirmButtonText: 'Yes, delete it!',
                confirmButtonColor: '#dc3545',
                successTitle: 'Deleted!',
                successText: 'The asset has been deleted.'
            }, function(){
                $.refreshPage();
            });

        }
    });

    $.addModal({
        selector: '#js-add-asset',
        url: $.getURL('/pim/assetCollections/browser'),
        name:'add-assets',
        title:'Select Asset',
        buttons: [
            {text: 'ADD', style: 'primary', close: false, click: function(){$.addAssets($.getSelectedItems())}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    var adjustment;
    var startSequence = "";
    $("div.js-draggable").sortable({
        group: 'js-draggable',
        itemSelector: 'div.js-drag-item',
        containerSelector: 'div.js-draggable',
        vertical: false,
        placeholder: '<div class="placeholder col-xl-4 col-lg-6 col-md-12 col-sm-12" />',
        pullPlaceholder: false,

        // set item relative to cursor position
        onDragStart: function ($item, container, _super) {
            startSequence = $("div.js-draggable").sortable("serialize").get();
            var offset = $item.offset(),
                pointer = container.rootGroup.pointer

            adjustment = {
                left: pointer.left - offset.left,
                top: pointer.top - offset.top
            };

            _super($item, container)
        },
        onDrop: function (item, container, _super) {
            var newSequence = $("div.js-draggable").sortable("serialize").get();
            console.log(startSequence[0]);
            console.log(newSequence[0]);
            if(startSequence[0] !== newSequence[0]) {
                $.reorderAssets(newSequence[0].split(','));
            }
            _super(item, container)
        },
        onDrag: function ($item, position) {
            $item.addClass('col-xl-4 col-lg-6 col-md-12 col-sm-12');
            $item.css({
                left: position.left - adjustment.left,
                top: position.top - adjustment.top
            })
        },
        serialize: function (parent, children, isContainer) {
            return isContainer ? children.join() : parent.attr('rel')
        }
    });

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