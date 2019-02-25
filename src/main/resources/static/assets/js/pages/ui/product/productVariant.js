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
        addAssets: function(assetIds) {
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

        },
        linkDependentAttributes: function() {
            let parentAttributeIds = new Set([]);
            $('[data-parent-attr-id]').each(function(){
                parentAttributeIds.add($(this).data('parent-attr-id'));
            });

            parentAttributeIds.forEach(function(value1){
                let parentEl = $('[data-attr-id="' + value1 + '"]')[0];
                $(parentEl).on('change', function(){
                    $.updateDependentAttributes($('[data-parent-attr-id="' + value1 + '"]'), $(this).find('option:selected'), $(this).val());
                });
            });
        },
        updateDependentAttributes: function(dependentAttributes, parentOptionEls, parentOptionValues) {
            $(dependentAttributes).each(function(i, dependentAttribute){
                const dependentAttributeContainer = $(dependentAttribute).closest('.ig-container');
                const uiType = $(dependentAttributeContainer).hasClass('dropdown') ? 'DROPDOWN' : $(dependentAttributeContainer).hasClass('accordion') ? 'ACCORDION' : 'UNKNOWN';
                let dependentChildAttributes = $(dependentAttributeContainer).find('.js-dependent-child');
                if(dependentChildAttributes.length < parentOptionValues.length) {
                    for(let i = 0; i < parentOptionValues.length; i ++) {
                        if($(dependentAttributeContainer).find('[data-parent-option="' + parentOptionValues[i] +'"]').length === 0) {
                            let newDependentChildAttribute = $(dependentAttributeContainer).find('.js-template').clone();
                            let attrId = $(newDependentChildAttribute).data('attr-id');
                            $(newDependentChildAttribute).removeClass('js-template').addClass('js-dependent-child').find('.js-parentOptionName').text($(parentOptionEls[i]).text());

                            $(newDependentChildAttribute).attr('data-parent-option', parentOptionValues[i]).removeAttr('data-parent-attr-id')
                                .find('.js-root-element').attr('name', $(newDependentChildAttribute).attr('data-attr-id') + '.' + parentOptionValues[i]);
                            if(uiType === 'ACCORDION') {
                                $(newDependentChildAttribute).find('#heading-').attr('id', 'heading-' + attrId)
                                    .find('.btn-link').attr('data-target', '#collapse-' + attrId).attr('aria-controls', 'collapse-' + attrId);
                                $(newDependentChildAttribute).find('#collapse-').attr('id', 'collapse-' + attrId).attr('aria-labelledby', 'heading-' + attrId);
                            }
                            $(dependentAttributeContainer).append(newDependentChildAttribute);
                            break;
                        }
                    }
                } else {

                    $(dependentChildAttributes).each(function(i, dependentChildAttribute){
                        var currentParentOption = $(dependentChildAttribute).data('parent-option');
                        if(currentParentOption && !parentOptionValues.includes(currentParentOption)){
                            $(dependentChildAttribute).remove();
                            return false;
                        }
                    });
                }
            });
        }
    });

    $.linkDependentAttributes();

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

    $('.js-variantPricing-tab').on('shown.bs.tab.variantPricing', function (e) {
        columns[columns.length] = {  data : 'actions' , title : 'Actions', orderable: false };
        $.initGrid({
            selector: '#paginatedPricingTable',
            names: ['variantPricing', 'variantPricing'],
            dataUrl: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/{productVariantId}/pricing'),
            columns: columns,
            buttons: [$.pricingAttributeDetailsButton()]
        });
        $(this).removeClass('js-variantPricing-tab').off('shown.bs.tab.variantPricing');
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
                pointer = container.rootGroup.pointer;

            adjustment = {
                left: pointer.left - offset.left,
                top: pointer.top - offset.top
            };

            _super($item, container)
        },
        onDrop: function (item, container, _super) {
            var newSequence = $("div.js-draggable").sortable("serialize").get();
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