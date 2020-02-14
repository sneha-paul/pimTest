$(function(){
    $('.js-pageAttributes-tab').on('shown.bs.tab.pageAttributes', function (e) {
        $.initGrid({
            selector: '#paginatedPageAttributesTable',
            names: ['pageAttributes', 'pageAttribute'],
            dataUrl: $.getURL('/pim/websites/{websiteId}/pages/{pageId}/attributes/data'),
            columns: [
                {data: 'attributeName', name: 'attributeName', title: 'Attribute Name'},
                {data: 'attributeId', name: 'attributeId', title: 'Attribute Id'},
                {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
            ],
            buttons: [$.websitePageAttributeDetailButton({pageUrl: $.getURL('/pim/websites/{websiteId}/pages/{pageId}/attributes/')}), $.attributeOptionsTabButton({actionUrl: '/pim/attributeCollections/{collectionId}/attributes/{attributeId}#attributeOptions'})]
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