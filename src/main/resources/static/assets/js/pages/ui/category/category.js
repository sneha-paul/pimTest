$(function(){
    var urlParams = {};

    if($.getPageAttribute('websiteId') !== '') {
        urlParams['websiteId'] = $.getPageAttribute('websiteId');
    }
    if($.getPageAttribute('catalogId') !== '') {
        urlParams['catalogId'] = $.getPageAttribute('catalogId');
    }
    if($.getPageAttribute("parentId") !== '') {
        urlParams['parentId'] = '{parentId}|{categoryId}';
    } else {
        urlParams['parentId'] = '{categoryId}';
    }

    if($.getPageAttribute('hash') !== '') {
        urlParams['hash'] = $.getPageAttribute('hash');
    }

    $('.js-subCategories-tab').on('shown.bs.tab.subCategories', function (e) {
        $.initAssociationsGrid({
            selector: '#paginatedSubCategoriesSortableTable',
            names: ['subCategoriesSortable', 'subCategory'],
            pageUrl: $.getURL('/pim/categories/'),
            dataUrl: $.getURL('/pim/categories/{categoryId}/subCategories/data'),
            toggleUrl: '/pim/categories/{categoryId}/subCategories/{externalId}/active/{active}',
            pageLength: 10,
            urlParams: urlParams,
            reordering: false,
            columns: [
                { data: 'sequenceNum', name : 'sequenceNum', visible: false },
                { data: 'subCategoryName', name : 'categoryName' , title : 'Category Name'},
                { data: 'externalId', name : 'externalId', title : 'Category ID' }
            ]
        });
        $(this).removeClass('js-subCategories-tab').off('shown.bs.tab.subCategories');
    });

    $.initAssociationsGrid({
        selector: '#paginatedSubCategoriesReorderableTable',
        names: ['subCategoriesReorderable', 'subCategory'],
        pageUrl: $.getURL('/pim/categories/'),
        dataUrl: $.getURL('/pim/categories/{categoryId}/subCategories/data'),
        toggleUrl: '/pim/categories/{categoryId}/subCategories/{externalId}/active/{active}',
        urlParams: urlParams,
        reordering: true,
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum' , title : 'Seq #', className: 'js-handle' },
            { data: 'subCategoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' }
        ]
    });

    $('.js-sorting-mode.subCategories').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('subCategoriesSortable');
            $('a.nav-link[href*="subCategoriesSortable"]').trigger('click');
            $(this).parent().find('.js-reordering-mode.subCategories').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }

    });

    $('.js-reordering-mode.subCategories').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('subCategoriesReorderable');
            $('a.nav-link[href*="subCategoriesReorderable"]').trigger('click');
            $(this).parent().find('.js-sorting-mode.subCategories').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }
    });

    var urlParams1 = {};

    if($.getPageAttribute('websiteId') !== '') {
        urlParams1['websiteId'] = $.getPageAttribute('websiteId');
    }
    if($.getPageAttribute('catalogId') !== '') {
        urlParams1['catalogId'] = $.getPageAttribute('catalogId');
    }
    urlParams1['categoryId'] = '{categoryId}';
    if($.getPageAttribute("parentId") !== '') {
        urlParams1['parentId'] = '{parentId}';
    }
    if($.getPageAttribute('hash') !== '') {
        urlParams1['hash'] = $.getPageAttribute('hash');
    }

    $('.js-products-tab').on('shown.bs.tab.products', function (e){
        $.initAssociationsGrid({
            selector: '#paginatedProductsSortableTable',
            names: ['productsSortable', 'product'],
            pageUrl: $.getURL('/pim/products/'),
            dataUrl: $.getURL('/pim/categories/{categoryId}/products/data'),
            toggleUrl: '/pim/categories/{categoryId}/products/{externalId}/active/{active}',
            urlParams: urlParams1,
            reordering: false,
            columns: [
                { data: 'sequenceNum', name : 'sequenceNum', visible: false },
                { data: 'productName', name : 'productName' , title : 'Parent Product Name', width: '35%', render: function ( data, type, row, meta ) {
                    let imgUrl = row.imageName === 'noimage.png' ? '/assets/img/' + row.imageName : '/uploads/' + row.imageName;
                    return '<div class="grid-image-holder pull-left rounded"><img  src="' + imgUrl + '" data-toggle="' + data + '" data-placement="top" title="" alt="" class="grid-main-img rounded"></div><div class="pull-left"><h6>' + data + '</h6><small>' + row.externalId + '<small></div>'}},
                { data: 'externalId', name : 'externalId', title : 'Parent Product ID' }
            ]
        });
        $(this).removeClass('js-products-tab').off('shown.bs.tab.products');
    });


    $.initAssociationsGrid({
        selector: '#paginatedProductsReorderableTable',
        names: ['productsReorderable', 'product'],
        pageUrl: $.getURL('/pim/products/'),
        dataUrl: $.getURL('/pim/categories/{categoryId}/products/data'),
        toggleUrl: '/pim/categories/{categoryId}/products/{externalId}/active/{active}',
        urlParams: urlParams1,
        reordering: true,
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum' , title : 'Seq #', className: 'js-handle' },
            { data: 'productName', name : 'productName' , title : 'Parent Product Name'},
            { data: 'externalId', name : 'externalId', title : 'Parent Product ID' }
        ]
    });

    $('.js-sorting-mode.products').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('productsSortable');
            $('a.nav-link[href*="productsSortable"]').trigger('click');
            $(this).parent().find('.js-reordering-mode.products').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }

    });

    $('.js-reordering-mode.products').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('productsReorderable');
            $('a.nav-link[href*="productsReorderable"]').trigger('click');
            $(this).parent().find('.js-sorting-mode.products').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }
    });

    $('.js-add-subCategory').on('click', function(){
        //var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/categories/{categoryId}/subCategories/available'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-subCategories',
            title:'Available SubCategories',
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

    $('.js-add-products').on('click', function(){
        //var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/categories/{categoryId}/products/available'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-products',
            title:'Available Parent Products',
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
});