$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedProductVariantsTable',
        name: 'productVariants',
        type: 'TYPE_1',
        url: $.getURL('/pim/products/{productId}/variants/'),
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
        url: $.getURL('/pim/products/{productId}/variants/create'),
        name:'create-variant',
        title:'Create Product Variant',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('productVariants');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

    $('.js-channel-selector .js-channel').on('click', function(){
        var channelId = $(this).data('channel-id');
        $.refreshPage({channelId : channelId}, $('.nav-link.active').attr('href'));
    });


    $('.datepicker').datepicker();
    // $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
});