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

    $.initAssociationsGrid({
        selector: '#paginatedSubCategoriesSortableTable',
        names: ['subCategoriesSortable', 'subCategory'],
        pageUrl: $.getURL('/pim/categories/'),
        dataUrl: $.getURL('/pim/categories/{categoryId}/subCategories/data'),
        toggleUrl: '{categoryId}/subCategories/{externalId}/active/{active}',
        urlParams: urlParams,
        reordering: false,
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum', visible: false },
            { data: 'subCategoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' }
        ]
    });

    $.initAssociationsGrid({
        selector: '#paginatedSubCategoriesReorderableTable',
        names: ['subCategoriesReorderable', 'subCategory'],
        pageUrl: $.getURL('/pim/categories/'),
        dataUrl: $.getURL('/pim/categories/{categoryId}/subCategories/data'),
        toggleUrl: '/{categoryId}/subCategories/{externalId}/active/{active}',
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

    $.initAssociationsGrid({
        selector: '#paginatedProductsSortableTable',
        names: ['productsSortable', 'product'],
        pageUrl: $.getURL('/pim/products/'),
        dataUrl: $.getURL('/pim/categories/{categoryId}/products/data'),
        urlParams: urlParams1,
        reordering: false,
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum', visible: false },
            { data: 'productName', name : 'productName' , title : 'Product Name'},
            { data: 'externalId', name : 'externalId', title : 'Product ID' }
        ]
    });

    $.initAssociationsGrid({
        selector: '#paginatedProductsReorderableTable',
        names: ['productsReorderable', 'product'],
        pageUrl: $.getURL('/pim/products/'),
        dataUrl: $.getURL('/pim/categories/{categoryId}/products/data'),
        urlParams: urlParams1,
        reordering: true,
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum' , title : 'Seq #', className: 'js-handle' },
            { data: 'productName', name : 'productName' , title : 'Product Name'},
            { data: 'externalId', name : 'externalId', title : 'Product ID' }
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

    /*$.initDataTable({
        selector: '#paginatedProductsTable',
        name: 'products',
        type: 'TYPE_2',
        url: $.getURL('/pim/categories/{categoryId}/products'),
        columns: [
            { data: 'productName', name : 'productName' , title : 'Product Name'},
            { data: 'productId', name : 'productId', title : 'Product ID' },
            { data: 'active', name : 'active' , title : 'Status', orderable: false},
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });*/

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
            title:'Available Products',
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