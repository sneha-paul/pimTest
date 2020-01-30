$(function(){
    var urlParams = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams['websiteId'] = '{websiteId}';
    }

    $('.js-catalogs-tab').on('shown.bs.tab.catalogs', function (e) {
        $.initAssociationsGrid({
            selector: '#paginatedCatalogsTable',
            names: ['websiteCatalogs', 'websiteCatalog'],
            pageUrl: $.getURL('/pim/catalogs/'),
            dataUrl: $.getURL('/pim/websites/{websiteId}/catalogs/data'),
            urlParams: urlParams,
            columns: [
                { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
                { data: 'externalId', name : 'catalogId', title : 'Catalog ID' }
            ],
            buttons: ['DETAILS']
        });
        $(this).removeClass('js-catalogs-tab').off('shown.bs.tab.catalogs');
    });


    $('.js-add-catalog').off().on('click', function(){
        var close = function(){};
        var options = {
            url: $.getURL('/pim/websites/{websiteId}/catalogs/available'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-catalogs',
            title:'Available Catalogs',
            size: eModal.size.lg,
            successCallback: function() {

            },
            buttons: [
                {text: 'CLOSE', style: 'danger', close: true, click: close }
            ]
        };
        eModal.ajax(options);
    });

    $('.js-websiteHistory-tab').on('shown.bs.tab.websiteHistory', function (e) {
        $.initGrid({
            selector: '#paginatedWebsiteHistoryTable',
            names: ['versions','version'],
            dataUrl: $.getURL('/pim/websites/{websiteId}/history'),
            hideStatus:'true',
            columns: [
                { data: 'timeStamp', name : 'timeStamp', title : 'Time'},
                { data: 'userName', name : 'userName' , title : 'User', orderable: false},
                { data: 'actions', name: 'actions', title: 'Actions', orderable: false}
            ],
            buttons: [$.detailsButton({pageUrl: $.getURL('/pim/websites/{websiteId}/history/')})]
        });
        $(this).removeClass('js-websiteHistory-tab').off('shown.bs.tab.websiteHistory');
    });

    $('.js-websitePages-tab').on('shown.bs.tab.websitePages', function (e) {
        $.initAssociationsGrid({
            selector: '#paginatedWebsitePageTable',
            names: ['websitePages', 'websitePage'],
            pageUrl: $.getURL('/pim/websites/{websiteId}/pages/'),
            dataUrl: $.getURL('/pim/websites/{websiteId}/pages/data'),
            urlParams: urlParams,
            hideStatus:'true',
            columns: [
                {data: 'pageFriendlyUrl', name: 'pageFriendlyUrl', title: 'Page Url', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small>' + $.getPageAttribute('websiteUrl') + '/' + row.pageUrl + '<small>';}}
            ],
            buttons: ['DETAILS']
        });
        $(this).removeClass('js-websitePages-tab').off('shown.bs.tab.websitePages');
    });

    $.addModal({
        selector: '.js-add-pages',
        url: $.getURL('/pim/websites/{websiteId}/pages/create'),
        name:'create-websitePages',
        title:'Create Website Pages',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('websitePages');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});
