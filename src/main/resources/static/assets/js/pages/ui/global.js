$(function(){
    $('input[disabled="disabled"],select[disabled="disabled"],textarea[disabled="disabled"]').each(function(){
        var msg = '';
        if($(this).hasClass('js-parent-level')) {
            msg = 'This property can only be modified at the product level';
            $(this).after($('<span class="js-level-locked icon-arrow-up" title="' + msg + '"></span>').on('click', function() {
                toastr.info(msg, 'Readonly Property');
            }));
        } else if($(this).hasClass('js-variant-axis')) {
            msg = 'This is the variant axis and is not editable';
            $(this).after($('<span class="js-axis-locked icon-target" title="' + msg + '"></span>').on('click', function() {
                toastr.info(msg, 'Readonly Property');
            }));
        } else {
            msg = 'This property is not editable';
            $(this).after($('<span class="js-locked icon-lock" title="' + msg + '"></span>').on('click', function() {
                toastr.info(msg, 'Readonly Property');
            }));
        }
    });

    $('.fancy-checkbox.js-disabled').each(function(){
        var msg = '';
        if($(this).hasClass('js-parent-level')) {
            msg = 'This property can only be modified at the product level';
            $(this).after($('<div class="js-level-locked js-checkbox" title="' + msg + '"><i class="icon-arrow-up"></div>').on('click', function() {
                toastr.info(msg, 'Readonly Property');
            }));
        } else if($(this).hasClass('js-variant-axis')) {
            msg = 'This is the variant axis and is not editable';
            $(this).after($('<div class="js-axis-locked js-checkbox" title="' + msg + '"><i class="icon-target"></div>').on('click', function(){
                toastr.info(msg, 'Readonly Property');
            }));
        } else {
            msg = 'This property is not editable';
            $(this).after($('<div class="js-locked icon-lock js-checkbox" title="' + msg + '"><i class="icon-lock"></div>').on('click', function(){
                toastr.info(msg, 'Readonly Property');
            }));
        }
        $(this).on('click', function(e){
            e.preventDefault();
            e.stopPropagation();
            toastr.info(msg, 'Readonly Property');
            return false;
        });
    });

    $('textarea.auto-resize').on('keydown', function(e){
        $.resizeTextArea($(this));
    });

    $('textarea.auto-resize').each(function() {
        $(this).height( $(this)[0].scrollHeight );
    })

});