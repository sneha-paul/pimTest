$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedProductVariantsTable',
        name: 'productVariants',
        type: 'TYPE_1',
        url: $.getURL('/pim/products/{productId}/channels/{channelId}/variants/'),
        url2: $.getURL('/pim/products/{productId}/variants/'),
        columns: [
            { data: 'productVariantName', name : 'productVariantName' , title : 'Variant Name'},
            { data: 'externalId', name : 'externalId', title : 'Variant ID' },
            { data: 'active', name : 'active' , title : 'Status', orderable: false},
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    $.addModal({
        selector: '#js-create-variant',
        url: $.getURL('/pim/products/{productId}/variants/create?ts=' + new Date().getTime()),
        data: {channelId: $.getPageAttribute('channelId')},
        name:'create-variant',
        title:'Create Product Variant',
        buttons: [
           // {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $('.js-channel-selector .js-channel').on('click', function(){
        var channelId = $(this).data('channel-id');
        $.refreshPage({channelId : channelId});
    });

    $('#aniimated-thumbnials').lightGallery({
        thumbnail: true,
        selector: 'a.js-asset'
    });

   /* $('.digital-asset-container').flip({
        trigger: 'manual'
    });*/

    $('.datepicker').datepicker();
    // $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
});