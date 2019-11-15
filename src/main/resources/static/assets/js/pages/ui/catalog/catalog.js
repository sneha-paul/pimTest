$(function(){
    var urlParams = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams['websiteId'] = '{websiteId}';
    }
    urlParams['catalogId'] = '{catalogId}';
    urlParams['hash'] = 'rootCategories';

    $('.js-rootCategories-tab').on('shown.bs.tab.rootCategories', function (e) {
        $.initAssociationsGrid({
            selector: '#paginatedRootCategoriesSortableTable',
            names: ['rootCategoriesSortable', 'rootCategory'],
            pageUrl: $.getURL('/pim/categories/'),
            dataUrl: $.getURL('/pim/catalogs/{catalogId}/rootCategories/data'),
            toggleUrl: '/pim/catalogs/{catalogId}/rootCategories/{externalId}/active/{active}',
            urlParams: urlParams,
            reordering: false,
            columns: [
                { data: 'sequenceNum', name : 'sequenceNum', visible: false },
                { data: 'rootCategoryName', name : 'categoryName' , title : 'Category Name'},
                { data: 'externalId', name : 'externalId', title : 'Category ID' }
            ]
        });
        $(this).removeClass('js-rootCategories-tab').off('shown.bs.tab.rootCategories');
    });


    $.initAssociationsGrid({
        selector: '#paginatedRootCategoriesReorderableTable',
        names: ['rootCategoriesReorderable', 'rootCategory'],
        pageUrl: $.getURL('/pim/categories/'),
        dataUrl: $.getURL('/pim/catalogs/{catalogId}/rootCategories/data'),
        toggleUrl: '/pim/catalogs/{catalogId}/rootCategories/{externalId}/active/{active}',
        urlParams: urlParams,
        reordering: true,
        reorderCallback: function() {$.refreshDataTable('categoriesHierarchy')},
        columns: [
            { data: 'sequenceNum', name : 'sequenceNum' , title : 'Seq #', className: 'js-handle' },
            { data: 'rootCategoryName', name : 'categoryName' , title : 'Category Name'},
            { data: 'externalId', name : 'externalId', title : 'Category ID' }
        ]
    });

    var urlParams1 = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams1['websiteId'] = '{websiteId}';
    }
    urlParams1['catalogId'] = '{catalogId}';
    urlParams1['hash'] = 'hierarchy';

    $('.js-hierarchy-tab').on('shown.bs.tab.hierarchy', function (e) {
        $.initTreeDataTable({
            selector: '#categoriesHierarchy',
            names: ['categoriesHierarchy', 'category'],
            url: $.getURL('/pim/catalogs/{catalogId}/hierarchy/'),
            url2: '/pim/categories/',
            collapsed: false,
            collapsible: false,
            urlParams: urlParams1
        });
        $(this).removeClass('js-hierarchy-tab').off('shown.bs.tab.hierarchy');
    });


    $('.js-sorting-mode').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('rootCategoriesSortable');
            $('a.nav-link[href*="sortable"]').trigger('click');
            $(this).parent().find('.js-reordering-mode').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }

    });

    $('.js-reordering-mode').on('click', function() {
        if(!$(this).hasClass('selected')) {
            $.refreshDataTable('rootCategoriesReorderable');
            $('a.nav-link[href*="reorderable"]').trigger('click');
            $(this).parent().find('.js-sorting-mode').removeClass('selected btn-secondary').addClass('btn-outline-secondary');
            $(this).removeClass('btn-outline-secondary').addClass('selected btn-secondary');
        }
    });

    $('.js-add-category').on('click', function(){
      //  var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/catalogs/{catalogId}/rootCategories/available'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-categories',
            title:'Available Categories',
            size: eModal.size.lg,
            successCallback: function() {

            },
            buttons: [
               // {text: 'OK', style: 'info',   close: true, click: eventA },
                {text: 'CLOSE', style: 'danger', close: true, click: eventB }
            ]
        };
        eModal.ajax(options);
    });

    $('.js-catalogHistory-tab').on('shown.bs.tab.catalogHistory', function (e) {
        $.initGrid({
            selector: '#paginatedCatalogHistoryTable',
            names: ['versions','version'],
            dataUrl: $.getURL('/pim/catalogs/{catalogId}/history'),
            hideStatus:'true',
            columns: [
                { data: 'timeStamp', name : 'timeStamp', title : 'Time'},
                { data: 'userName', name : 'userName' , title : 'User', orderable: false},
                { data: 'actions', name: 'actions', title: 'Actions', orderable: false}
            ],
            buttons: [$.detailsButton({pageUrl: $.getURL('/pim/catalogs/{catalogId}/history/')})]
        });
        $(this).removeClass('js-catalogHistory-tab').off('shown.bs.tab.catalogHistory');
    });
});