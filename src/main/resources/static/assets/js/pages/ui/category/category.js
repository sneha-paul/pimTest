$(function(){
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
});

$(function(){
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