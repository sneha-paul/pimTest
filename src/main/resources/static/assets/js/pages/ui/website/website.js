$(function(){
    $('.js-add-catalog').off().on('click', function(){
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/websites/{websiteId}/catalogs/available'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-catalogs',
            title:'Available Catalogs',
            size: eModal.size.lg,
            successCallback: function() {

            },
            buttons: [
                {text: 'CLOSE', style: 'danger', close: true, click: eventB }
            ]
        };
        eModal.ajax(options);
    });
});
