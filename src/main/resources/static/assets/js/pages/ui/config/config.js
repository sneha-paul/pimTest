$(function(){
    var urlParams = {};
    if($.getPageAttribute('configId') !== '') {
        urlParams['configId'] = '{configId}';
    }

    $('.js-config-params-tab').on('shown.bs.tab.params', function (e) {
        $.initEntitiesGrid({
            selector: '#paginatedConfigParametersTable',
            names: ['configParameters','configParameter'],
            pageUrl: '/pim/configs/' + $.getPageAttribute('configId') + '/params/',
            dataUrl: $.getURL('/pim/configs/{configId}/params/data'),
            hideStatus:'true',
            columns: [
                { data: 'paramName', name : 'paramName' , title : 'Parameter Name'},
                { data: 'paramValue', name : 'paramValue', title : 'Parameter Value' }
            ],
            buttons: ['DETAILS', 'DELETE']
        });
        $(this).removeClass('js-config-params-tab').off('shown.bs.tab.params');
    });

    $.addModal({
        selector: '.js-add-parameters',
        url: $.getURL('/pim/configs/{configId}/params/create'),
        name:'create-parameters',
        title:'Create Parameters',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('configParameters');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

});