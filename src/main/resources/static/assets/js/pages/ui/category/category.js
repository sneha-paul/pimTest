$(function(){
    $('.js-add-subCategory').on('click', function(){
        var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: $.getURL('/pim/categories/{categoryId}/availableSubCategories'),
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            name:'available-subCategories',
            title:'Available SubCategories',
            size: eModal.size.lg,
            buttons: [
                {text: 'OK', style: 'info',   close: true, click: eventA },
                {text: 'CANCEL', style: 'danger', close: true, click: eventB }
            ]
        };
        eModal.iframe(options);
    });
});