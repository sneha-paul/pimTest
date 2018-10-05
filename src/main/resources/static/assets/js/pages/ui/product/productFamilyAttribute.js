$(function(){
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
});