$( document ).ready(function() {
    $('.js-add-qty-break').on('click', function(e){
        var qtyBreakUI = $(this).siblings('.js-pricing').clone();
        qtyBreakUI.removeClass('displaynone').find('.js-qty').attr('contentEditable', true);
        $(qtyBreakUI).insertBefore($(this).parent());
        return false;


    });

    $('.js-qty').each(function(){
        $(this).text($.formatNumber($(this).text()) + ' Qty');
    });

    $('.js-qty-breaks')
        .on('focus', '.js-qty', function(){
            $(this).text($(this).text().replace(/\D/g,''));
            setEndOfContenteditable($(this)[0]);
            document.execCommand('selectAll',false,null);

        })
        .on('blur','.js-qty', function(){
            var qtyBreak = $(this).text().replace(/\D/g,'');
            if(qtyBreak === '') {
                qtyBreak = '0';
            }
            $(this).text($.formatNumber(qtyBreak) + ' Qty');
            $(this).closest('.js-pricing').find('input.js-name').attr('name', 'q' + qtyBreak);
        });

    function setEndOfContenteditable(contentEditableElement)
    {
        var range,selection;
        if(document.createRange)//Firefox, Chrome, Opera, Safari, IE 9+
        {
            range = document.createRange();//Create a range (a range is a like the selection but invisible)
            range.selectNodeContents(contentEditableElement);//Select the entire contents of the element with the range
            range.collapse(false);//collapse the range to the end point. false means collapse to end rather than the start
            selection = window.getSelection();//get the selection object (allows you to change selection)
            selection.removeAllRanges();//remove any selections already made
            selection.addRange(range);//make the range you have just created the visible selection
        }
        else if(document.selection)//IE 8 and lower
        {
            range = document.body.createTextRange();//Create a range (a range is a like the selection but invisible)
            range.moveToElementText(contentEditableElement);//Select the entire contents of the element with the range
            range.collapse(false);//collapse the range to the end point. false means collapse to end rather than the start
            range.select();//Select the range (make it the visible selection
        }
    }
});
//# sourceURL=/assets/js/pages/ui/product/pricingDetails.js