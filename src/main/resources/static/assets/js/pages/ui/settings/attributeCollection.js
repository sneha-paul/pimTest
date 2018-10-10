$(function(){
    $.addModal({
        selector: '.js-add-attribute',
        url: $.getURL('/pim/attributeCollections/{collectionId}/attribute'),
        name:'attribute',
        title:'Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('attributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});