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
        },
        reorderAssets: function(assetIds) {
            $.ajaxSubmit({
                url: '/pim/products/{productId}/channels/{channelId}/assets/reorder',
                data: {assetFamily: 'ASSETS', assetIds: assetIds},
                method: 'PUT',
                successMessage: ['Reordered Assets', 'Successfully updated the asset sequences'],
                errorMessage: ['Error', 'Error occurred while updating the asset sequences'],
                successCallback: function(data) {
                    $.refreshPage({channelId: $.getPageAttribute('channelId')});
                }
            });
        },
        deleteAsset: function(assetId) {
            $.confirmedAJAXRequest({
                url: $.getURL('/pim/products/{productId}/channels/{channelId}/assets'),
                data: 'assetFamily=ASSETS&assetId=' + assetId,
                method: 'DELETE',
                text: 'This will delete the asset',
                confirmButtonText: 'Yes, delete it!',
                confirmButtonColor: '#dc3545',
                successTitle: 'Deleted!',
                successText: 'The asset has been deleted.'
            }, function(){
                $.refreshPage({channelId: $.getPageAttribute('channelId')});
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
        toggleUrl: '/pim/products/{productId}/channels/{channelId}/variants/{externalId}/active/{active}',
        urlParams: urlParams,
        reordering: false,
        columns: [
            { data: 'productVariantName', name : 'productVariantName' , title : 'Variant Name', render: function ( data, type, row, meta ) {return '<img src="/uploads/31268C8F-4C4A-4974-A26F-E6CA8CCDEE82.jpg" class="rounded-circle avatar" alt=""><p class="c_name">' + data + '</p>';}},
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
        toggleUrl: '/pim/products/{productId}/categories/{externalId}/active/{active}',
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
});

