$(function(){
    let parentAttributeName = $.getPageAttribute('parentAttributeName');
    $('.js-attributeOptions-tab').on('shown.bs.tab.attributeOptions', function (e) {
        $.initGrid({
            selector: '#paginatedAttributeOptionsTable',
            names: ['attributeOptions', 'attributeOption'],
            dataUrl: $.getURL('/pim/attributeCollections/{collectionId}/attributes/{attributeFullId}/options/data'),
            columns: [
                // { data: 'parent', name : 'parent' , title : parentAttributeName, visible: '' !== parentAttributeName},
                { data: 'value', name : 'value' , title : 'Value'},
                { data: 'id', name : 'id', title : 'ID' },
                { data: 'actions', name : 'actions', title : 'Actions', orderable: false }
            ],
            buttons: [$.attributeOptionDetailButton({actionUrl: '/pim/attributeCollections/{collectionId}/attributes/{attributeId}/options/{attributeOptionId}'})]
        });
        $(this).removeClass('js-attributeOptions-tab').off('shown.bs.tab.attributeOptions');
    });

    $.addModal({
        selector: '.js-add-attributeOption',
        url: $.getURL('/pim/attributeCollections/{collectionId}/attributes/{attributeId}/options/create'),
        name:'attribute-option',
        title:'Create ' + $.getPageAttribute('attributeName') + ' Attribute Option',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('attributeOptions');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });



});