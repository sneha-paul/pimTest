$(function(){

    var urlParams = {};
    if($.getPageAttribute('jobInstanceId') !== '') {
        urlParams['jobInstanceId'] = '{jobInstanceId}';
    }

    $('.js-logs-tab').on('shown.bs.tab.logs', function (e) {
        $.initEntitiesGrid({
            selector: '#paginatedLogsTable',
            names: ['logs', 'log'],
            pageUrl: $.getURL('/pim/jobs/'),
            dataUrl: $.getURL('/pim/jobs/{jobInstanceId}/logs/data'),
            urlParams: urlParams,
            hideStatus:'true',
            hideActions:'true',
            columns: [
                { data: 'logs', name : 'logs', title : 'Logs', orderable: false},
                { data: 'level', name : 'level', title : 'Level', orderable: false},
            ],
        });
        $(this).removeClass('js-logs-tab').off('shown.bs.tab.logs');
    });


});