$(function(){
    $.addModal({
        selector: '.js-add-asset',
        url: $.getURL('/pim/assetCollections/{collectionId}/asset'),
        name:'asset',
        title:'Asset',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable1('assetsHierarchy');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $.addModal({
        selector: '.js-add-asset-group',
        url: $.getURL('/pim/assetCollections/{collectionId}/assetGroup'),
        name:'assetGroup',
        title:'Asset Group',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable1('assetsHierarchy');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});