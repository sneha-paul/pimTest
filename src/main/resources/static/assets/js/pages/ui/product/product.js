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

    $('.js-productVariants-tab').on('shown.bs.tab.productVariants', function (e) {
        $.initAssociationsGrid({
            selector: '#paginatedProductVariantsSortableTable',
            names: ['productVariantsSortable', 'productVariant'],
            pageUrl: $.getURL('/pim/products/{productId}/variants/'),
            showDiscontinueFilter: true,
            dataUrl: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/data'),
            toggleUrl: '/pim/products/{productId}/channels/{channelId}/variants/{externalId}/active/{active}',
            archiveUrl:'/pim/products/{productId}/channels/{channelId}/variants/{externalId}/archive/{archived}',
            urlParams: urlParams,
            reordering: false,
            columns: [
                { data: 'sequenceNum', name : 'sequenceNum', visible: false },
                { data: 'productVariantName', name : 'productVariantName' , title : 'Child Product Name', render: function ( data, type, row, meta ) {
                    let imgUrl = row.imageName === 'noimage.png' ? '/assets/img/' + row.imageName : '/uploads/' + row.imageName;
                    return '<div class="grid-image-holder pull-left rounded"><img  src="' + imgUrl + '" data-toggle="' + data + '" data-placement="top" title="" alt="" class="grid-main-img rounded"></div><div class="pull-left"><h6>' + data + '</h6><small>' + row.externalId + '<small></div>'}},
                { data: 'externalId', name : 'externalId', title : 'Child Product ID' }
            ]
        });
        $(this).removeClass('js-productVariants-tab').off('shown.bs.tab.productVariants');
    });

    $.initAssociationsGrid({
        selector: '#paginatedProductVariantsReorderableTable',
        names: ['productVariantsReorderable', 'productVariant'],
        pageUrl: $.getURL('/pim/products/{productId}/variants/'),
        dataUrl: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/data'),
        toggleUrl: '/pim/products/{productId}/channels/{channelId}/variants/{externalId}/active/{active}',
        urlParams: urlParams,
        reordering: true,
        // reorderCallback: function() {$.refreshDataTable('categoriesHierarchy')},
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum' , title : 'Seq #', className: 'js-handle' },
            { data: 'productVariantName', name : 'productVariantName' , title : 'Child Product Name', render: function ( data, type, row, meta ) {
                let imgUrl = row.imageName === 'noimage.png' ? '/assets/img/' + row.imageName : '/uploads/' + row.imageName;
                return '<div class="grid-image-holder pull-left rounded"><img  src="' + imgUrl + '" data-toggle="' + data + '" data-placement="top" title="" alt="" class="grid-main-img rounded"></div><div class="pull-left"><h6>' + data + '</h6><small>' + row.externalId + '<small></div>'}},
            { data: 'externalId', name : 'externalId', title : 'Child Product ID' }
        ]
    });

    $('.js-sorting-mode.variants').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('productVariantsSortable');
            $('a.nav-link[href*="sortableProductVariant"]').trigger('click');
            $(this).parent().find('.js-reordering-mode.variants').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }

    });

    $('.js-reordering-mode.variants').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('productVariantsReorderable');
            $('a.nav-link[href*="reorderableProductVariant"]').trigger('click');
            $(this).parent().find('.js-sorting-mode.variants').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }
    });

    var urlParams1 = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams1['websiteId'] = '{websiteId}';
    }
    if($.getPageAttribute('catalogId') !== '') {
        urlParams1['catalogId'] = '{catalogId}';
    }

    $('.js-productCategories-tab').on('shown.bs.tab.productCategories', function (e) {
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
        $(this).removeClass('js-productCategories-tab').off('shown.bs.tab.productCategories');
    });

    $.addModal({
        selector: '#js-create-variant',
        url: $.getURL('/pim/products/{productId}/variants/create?ts=' + new Date().getTime()),
        data: {channelId: $.getPageAttribute('channelId')},
        name:'create-variant',
        title:'Create Child Product',
        buttons: [
           // {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $('#js-sync-updatedProductVariants').on("click", function () {
        $.syncUpdatedInstance(
            $.getURL("/pim/products/syncUpdatedProductVariants"), "productVariants");
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

    $('#js-sync-productCategories').on("click", function () {
        $.syncUpdatedInstance(
            $.getURL("/pim/products/{productId}/syncProductCategories"), "categories");
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

   // $('.datepicker').datepicker();
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

