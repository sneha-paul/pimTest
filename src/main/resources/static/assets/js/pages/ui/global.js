$(function(){
    $('input[disabled="disabled"],select[disabled="disabled"],textarea[disabled="disabled"]').each(function(){

        $(this).after('<span class="js-locked icon-lock"></span>');
    });
});