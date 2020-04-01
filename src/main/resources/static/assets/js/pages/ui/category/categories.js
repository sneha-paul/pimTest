$(function(){
    $.initEntitiesGrid({
        selector: '#paginatedCategoriesTable',
        names: ['categories','category'],
        pageUrl: '/pim/categories/',
        toolbar: [{name: 'EXPORT', actionUrl: '/pim/categories/export'}, {name: 'IMPORT'}],
        showDiscontinueFilter: true,
        dataUrl: '/pim/categories/data',
        toggleUrl:'/pim/categories/{externalId}/categories/active/{active}',
        archiveUrl:'/pim/categories/{externalId}/categories/archive/{archived}',
        columns: [
            { data: 'categoryName', name : 'categoryName' , title : 'Category Name', width:'45%', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6>';}},
            { data: 'externalId', name : 'externalId', title : 'Category ID', width:'30%' }
        ],
    });

    var urlParams = {};
    urlParams['parentId'] = '{parentId}';
    urlParams['hash'] = '1';

    $.initTreeDataTable({
        selector: '#categoriesHierarchy',
        names: ['categoriesHierarchy', 'category'],
        url: '/pim/categories/hierarchy/',
        url2: '/pim/categories/',
        collapsed: false,
        collapsible: true,
        urlParams: urlParams
    });

    $('.js-category-grid-view').on('click', function(){
        $.refreshDataTable('categories');
        $('a.nav-link[href*="categoryGrid"]').trigger('click');
        $(this).removeClass('btn-outline-secondary').addClass('btn-secondary');
        $(this).parent().find('.js-category-tree-view').removeClass('btn-secondary').addClass('btn-outline-secondary');
    });

    $('.js-category-tree-view').on('click', function(e, view){
        if(1 !== view) {
            $.refreshDataTable('categoriesHierarchy');
        }
        $('a.nav-link[href*="categoryTree"]').trigger('click');
        $(this).removeClass('btn-outline-secondary').addClass('btn-secondary');
        $(this).parent().find('.js-category-grid-view').removeClass('btn-secondary').addClass('btn-outline-secondary');
    });

    if(window.location.hash === '#1') {
        $('.js-category-tree-view').trigger('click', 1);
    }

    $.addModal({
        selector: '#js-create-category',
        url: $.getURL('/pim/categories/create'),
        name:'create-category',
        title:'Create Category',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('categories');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $('#js-sync-updatedCategories').on("click", function () {
        $.syncUpdatedInstance(
            $.getURL("/pim/categories/syncUpdatedCategories"), "categories");
    });

    $.addModal({
        selector: '#js-advanced-search',
        url: $.getURL('/pim/categories/search'),
        name:'advanced-search',
        title:'Advanced Search',
        buttons: [
            {text: 'SEARCH', style: 'primary', close: false, click: function(){$.getDataTable('categories').search('').draw();$.closeModal();}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});
