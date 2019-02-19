$(function(){
    $.addModal({
        selector: '.js-upload-files',
        url: $.getURL('/pim/assetCollections/{collectionId}/assets'),
        data: {assetGroupId: $.getPageAttribute('assetGroupId'), assetGroup: false},
        name:'uploadFiles',
        title:'Upload Files',
        buttons: [
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $.addModal({
        selector: '.js-create-folder',
        url: $.getURL('/pim/assetCollections/{collectionId}/assets'),
        data: {assetGroupId: $.getPageAttribute('assetGroupId'), assetGroup: true},
        name:'createFolder',
        title:'Create Folder',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.refreshPage();$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $('.js-folder').off().on('dblclick', function(){
        window.location.href = $.getURL('/pim/assetCollections/{collectionId}/assets/{assetId}', {assetId: $(this).attr('id')});
    });
    $('#aniimated-thumbnials').lightGallery({
        thumbnail: true,
        selector: 'a.js-asset'
    });
});