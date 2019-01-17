$( document ).ready(function() {

    $.extend({
        setAsDefaultAsset: function(assetId) {
            $.ajaxSubmit({
                url: '/pim/products/{productId}/channels/{channelId}/assets/setDefault',
                data: {assetFamily: 'ASSETS', assetId: assetId},
                method: 'PUT',
                successMessage: ['Asset Updated', 'Successfully set the default asset'],
                errorMessage: ['Error', 'Error occurred while setting the default asset'],
                successCallback: function(data) {
                    $.refreshPage({channelId: $.getPageAttribute('channelId')});
                }
            });
        },
        addAssets: function(assetIds) {
            $.ajaxSubmit({
                url: '/pim/products/{productId}/channels/{channelId}/assets',
                data: {assetFamily: 'ASSETS', assetIds: assetIds},
                successMessage: ['Assets Added', 'Successfully added the assets'],
                errorMessage: ['Error', 'Error occurred while adding assets'],
                successCallback: function(data) {
                    $.refreshPage({channelId: $.getPageAttribute('channelId')});
                    $.closeModal();
                }
            });
        }
    });
    var urlParams = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams['websiteId'] = '{websiteId}';
    }
    if($.getPageAttribute('catalogId') !== '') {
        urlParams['catalogId'] = '{catalogId}';
    }
    if($.getPageAttribute('categoryId') !== '') {
        urlParams['categoryId'] = '{categoryId}';
    }
    if($.getPageAttribute('parentId') !== '') {
        urlParams['parentId'] = '{parentId}';
    }
    if($.getPageAttribute('hash') !== '') {
        urlParams['hash'] = '{hash}';
    }
    urlParams['channelId'] = '{channelId}';

    $.initAssociationsGrid({
        selector: '#paginatedProductVariantsTable',
        names: ['productVariants', 'productVariant'],
        pageUrl: $.getURL('/pim/products/{productId}/variants/'),
        dataUrl: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/data'),
        urlParams: urlParams,
        reordering: false,
        columns: [
            { data: 'productVariantName', name : 'productVariantName' , title : 'Variant Name'},
            { data: 'externalId', name : 'externalId', title : 'Variant ID' }
        ]
    });

    var urlParams1 = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams1['websiteId'] = '{websiteId}';
    }
    if($.getPageAttribute('catalogId') !== '') {
        urlParams1['catalogId'] = '{catalogId}';
    }

    $.initAssociationsGrid({
        selector: '#paginatedCategoriesTable',
        names: ['categories', 'category'],
        pageUrl: $.getURL('/pim/categories/{categoryId}/'),
        dataUrl: $.getURL('/pim/products/{productId}/categories/data'),
        urlParams: urlParams1,
        reordering: false,
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' }
        ],
        buttons: ['TOGGLE_STATUS']
    });

    $.addModal({
        selector: '#js-create-variant',
        url: $.getURL('/pim/products/{productId}/variants/create?ts=' + new Date().getTime()),
        data: {channelId: $.getPageAttribute('channelId')},
        name:'create-variant',
        title:'Create Product Variant',
        buttons: [
           // {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $('.js-add-categories').on('click', function(){
        //var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/products/{productId}/categories/available'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-categories',
            title:'Available Categories',
            size: eModal.size.lg,
            successCallback: function() {

            },
            buttons: [
                //{text: 'OK', style: 'info',   close: true, click: eventA },
                {text: 'CLOSE', style: 'danger', close: true, click: eventB }
            ]
        };
        eModal.ajax(options);
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

    $('.js-channel-selector .js-channel').on('click', function(){
        var channelId = $(this).data('channel-id');
        $.refreshPage({channelId : channelId});
    });

    $('#aniimated-thumbnials').lightGallery({
        thumbnail: true,
        selector: 'a.js-asset'
    });

    $('.digital-asset-container').flip({
        trigger: 'manual'
    });

    $('.datepicker').datepicker();
    // $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
});
