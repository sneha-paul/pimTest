var main_scrolltop = 0; var active_slideouts = []; var slideout_active = false; openSlideOut = function (target) { main_scrolltop = $('html').scrollTop(); $('html').removeClass('mode-slideout').addClass('mode-slideout'); target.find('.slideout_header').css({ 'top': target.scrollTop() + 'px' }); target.find('.slideout_body').removeClass('slideout_body_margin').addClass('slideout_body_margin'); target.animate({ right: "0%" }, function () { target.find('[class*="slideout_header"]').css({ 'top': '0px', 'position': 'fixed' }); active_slideouts.push(target.attr('id')); slideout_active = false }); }
closeSlideOut = function (target) {
    target.find('.slideout_header').css({ 'position': 'absolute', 'top': target.scrollTop() + 'px' }); target.animate({ right: "-100%" }, function () {
        if (active_slideouts.length <= 1) { $('html').removeClass('mode-slideout').scrollTop(main_scrolltop); target.find('.slideout_body').removeClass('slideout_body_margin'); }
        active_slideouts.pop(); slideout_active = false;
    });
}
initSlideout = function () { $('[data-slideout_name]').off('click.slideout').on('click.slideout', function () { if (!slideout_active) { slideout_active = true; var slideout_name = $(this).attr('data-slideout_name'); openSlideOut($('#' + slideout_name)); } }); $('.slideout_result [data-go_back]').off('click.slideout').on('click.slideout', function () { if (!slideout_active) { slideout_active = true; closeSlideOut($('#' + active_slideouts[active_slideouts.length - 1])); } }); }
$(document).ready(function () { initSlideout(); });