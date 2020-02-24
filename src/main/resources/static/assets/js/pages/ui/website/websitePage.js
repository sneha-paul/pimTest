$(function(){
    var urlParams = {};
    if($.getPageAttribute('websiteId') !== '') {
        urlParams['websiteId'] = '{websiteId}';
    }
    if($.getPageAttribute('pageId') !== '') {
        urlParams['pageId'] = '{pageId}';
    }

    $('.js-pageAttributes-tab').on('shown.bs.tab.pageAttributes', function (e) {
        $.initGrid({
            selector: '#paginatedPageAttributesTable',
            names: ['pageAttributes', 'pageAttribute'],
            dataUrl: $.getURL('/pim/websites/{websiteId}/pages/{pageId}/attributes/data'),
            hideActiveFilter: true,
            hideInactiveFilter: true,
            urlParams: urlParams,
            columns: [
                {data: 'attributeName', name: 'attributeName', title: 'Attribute Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small>' + row.attributeId + '<small>';}},
                {data: 'attributeValue', name: 'attributeValue', title: 'Attribute Value'},
                {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
            ],
            buttons: [
                $.websitePageAttributeDetailButton({
                    pageUrl: $.getURL('/pim/websites/{websiteId}/pages/{pageId}/attributes/')
                }),
                $.websitePageAttributeDeleteButton({
                    names: ['pageAttributes', 'pageAttribute'],
                    pageUrl: $.getURL('/pim/websites/{websiteId}/pages/{pageId}/attributes/')
                })
            ]
        });
        $(this).removeClass('js-pageAttributes-tab').off('shown.bs.tab.pageAttributes');
    });

    $.addModal({
        selector: '.js-add-pageAttributes',
        url: $.getURL('/pim/websites/{websiteId}/pages/{pageId}/attributes/create'),
        name:'create-pageAttributes',
        title:'Create Page Attributes',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('pageAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});