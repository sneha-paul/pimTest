$(function(){

    $.initDataTable({
        selector: '#paginatedAttributeOptionsTable',
        name: 'attributeOptions',
        type: 'TYPE_2',
        url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options/list'),
        columns: [
            { data: 'value', name : 'value' , title : 'Value'},
            { data: 'id', name : 'id', title : 'ID' },
            { data: 'active', name : 'active' , title : 'Active'}
        ]
    });

    $.extend({
        bindShowInputUIEvent : function(buttonEl) {
            $(buttonEl).on('click', function(){
                $.showInputUIAction(this);
            });
        },
        bindHideInputUIEvent : function(buttonEl) {
            $(buttonEl).on('click', function(){
                $.hideInputUIAction(this);
            });
        },

        resetAddUI: function() {
            $.hideInputUIAction($('.js-add-input-ui-group .js-cancel-option'));
        },

        showInputUIAction: function(buttonEl) {
            $($('.js-add-input-ui-group').show().find('input')[0]).focus();
            $(buttonEl).hide();
        },

        hideInputUIAction: function (buttonEl) {
            $('.js-add-attribute-option').show();
            $(buttonEl).closest('.js-add-input-ui-group').hide().find('input').val('');
        }
    });

    $('.js-available-attribute-option').on('click', function(){
        $.showModal({
            url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options/available'),
            name:'available-attribute-options',
            title:'Available Attribute Options',
            buttons: [
                {text: 'CLOSE', style: 'danger', close: true, click: function(){
                    $.showModal({
                        url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options'),
                        name:'attribute-options',
                        title:'Attribute Options',
                        buttons: [
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                }}
            ]
        });
    });

    $.bindShowInputUIEvent($('.js-add-attribute-option'));
    $.bindHideInputUIEvent($('.js-add-input-ui-group .js-cancel-option'));
});