$(function(){
    var urlParams = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams['websiteId'] = '{websiteId}';
    }
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
});
