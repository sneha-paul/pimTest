$(function(){
    $.addModal({
        selector: '.js-add-attribute',
        url: $.getURL('/pim/attributeCollections/{collectionId}/attributes/create'),
        name:'attribute',
        title:'Create Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('attributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});