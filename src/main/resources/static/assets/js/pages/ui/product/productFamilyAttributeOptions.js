$(function(){

    $.initDataTable({
        selector: '#paginatedAttributeOptionsTable',
        name: 'productAttributeOptions',
        type: 'TYPE_2',
        url: $.getURL('/pim/productFamilies/{productFamilyId}/{entityType}/attributes/{attributeId}/options/list'),
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

    $.bindShowInputUIEvent($('.js-add-attribute-option'));
    $.bindHideInputUIEvent($('.js-add-input-ui-group .js-cancel-option'));
});