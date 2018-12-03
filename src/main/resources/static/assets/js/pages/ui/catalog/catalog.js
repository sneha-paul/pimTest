$(function(){
    $('.js-add-category').on('click', function(){
      //  var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}/rootCategories/available'),
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
});