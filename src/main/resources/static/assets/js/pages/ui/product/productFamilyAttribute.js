$(function(){
    $.extend({
        bindAddNewOption : function(selectEl) {
            $(selectEl).on('change', function(){
                if($(selectEl).val() === '') {
                    $('.js-attribute-group-name').show();
                } else {
                    $('.js-attribute-group-name input').val('');
                    $('.js-attribute-group-name').hide();
                }
            });
        }
    });
$.bindAddNewOption($('#js-attribute-group-id'));
});