$(function(){
    var urlParams = {};
    if($.getPageAttribute('configId') !== '') {
        urlParams['configId'] = '{configId}';
    }

    $('.js-config-params-tab').on('shown.bs.tab.params', function (e) {
        $.initEntitiesGrid({
            selector: '#paginatedConfigParametersTable',
            names: ['configParameters','configParameter'],
            pageUrl: '/pim/websites/',
            dataUrl: $.getURL('/pim/configs/{configId}/params/data'),
            columns: [
                { data: 'paramName', name : 'paramName' , title : 'Parameter Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small>' + row.url + '<small>';}},
                { data: 'paramValue', name : 'paramValue', title : 'Parameter Value' }
            ],
            buttons: ['DETAILS']
        });
        $(this).removeClass('js-config-params-tab').off('shown.bs.tab.params');
    });
});