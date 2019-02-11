$(function(){

    let parentAttributeName = $.getPageAttribute('parentAttributeName');
    $('.js-attributeOptions-tab').on('shown.bs.tab.attributeOptions', function (e) {
        $.initGrid({
            selector: '#paginatedAttributeOptionsTable',
            names: ['attributeOptions', 'attributeOption'],
            dataUrl: $.getURL('/pim/families/{familyId}/attributes/{attributeFullId}/options/data'),
            columns: [
                // { data: 'parent', name : 'parent' , title : parentAttributeName, visible: '' !== parentAttributeName},
                { data: 'value', name : 'value' , title : 'Value'},
                { data: 'id', name : 'id', title : 'ID' },
                { data: 'actions', name : 'actions', title : 'Actions', orderable: false }
            ],
            buttons: [$.attributeOptionDetailButton({actionUrl: '/pim/families/{familyId}/attributes/{attributeId}/options/{attributeOptionId}'})]
        });
        $(this).removeClass('js-attributeOptions-tab').off('shown.bs.tab.attributeOptions');
    });

    $('.js-add-attributeOption').on('click', function(){
        $.showModal({
            url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options/available'),
            name:'available-attribute-options',
            title:'Available ' + $.getPageAttribute('attributeName') + ' Options',
            buttons: [
                {text: 'CLOSE', style: 'danger', close: true }
            ]
        });
    });

    $.extend({
        bindAddNewOption : function(selectEl) {
            $(selectEl).on('change', function(){
                if($(selectEl).val() === '') {
                    $('.js-new-attribute-group').show();
                } else {
                    $('.js-new-attribute-group input[type="text"]').val('');
                    $(".js-new-attribute-group select").val($(".js-new-attribute-group select option:first").val());
                    $('.js-new-attribute-group input[type="checkbox"]').prop( "checked", false );
                    $('.js-new-attribute-group').hide();
                }
            });
        },
        bindAttributeChangeEvent : function(selectEl) {
            $(selectEl).on('change', function(){
                $('input[name="name"]').val($(this).find('option:selected').data('name'));
                $('select[name="uiType"]').val($(this).find('option:selected').data('ui'))

            });
        },
        bindMasterGroupChangeEvent: function(checkboxEl) {
            $(checkboxEl).on('change', function() {
                var parentGroupEl = $('#js-parent-group-id');
                if($(this).prop("checked")) {
                    $(parentGroupEl).val($(parentGroupEl).find('option:first').val());
                    $(parentGroupEl).parent().hide();
                } else {
                    $(parentGroupEl).parent().show();
                }
            });
        }
    });
$.bindAddNewOption($('#js-attribute-group-id'));
$.bindMasterGroupChangeEvent($('.js-master-group'));
$.bindAttributeChangeEvent($('#js-attribute'));
});