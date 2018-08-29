$(function(){
    $('.js-add-catalog').on('click', function(){
        var eventA = function(){};
        var eventB = function(){};
        var options = {
            url: "http://localhost:8081/pim/websites/ENVELOPES/availableCatalogs",
            loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
            title:'Available Catalogs',
            size: eModal.size.lg,
            buttons: [
                {text: 'OK', style: 'info',   close: true, click: eventA },
                {text: 'CANCEL', style: 'danger', close: true, click: eventB }
            ]
        };
        eModal.iframe(options);
    });
});