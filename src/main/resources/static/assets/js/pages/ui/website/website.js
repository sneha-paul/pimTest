$(function(){
    $('.js-add-catalog').on('click', function(){
        var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/websites/{websiteId}/availableCatalogs'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-catalogs',
            title:'Available Catalogs',
            size: eModal.size.lg,
            buttons: [
                // {text: 'OK', style: 'info',   close: true, click: eventA },
                {text: 'CLOSE', style: 'danger', close: true, click: eventB }
            ]
        };
        eModal.ajax(options);
    });
});