$(function(){
    $.addModal({
        selector: '.js-add-familyAttribute',
        url: $.getURL('/pim/families/{familyId}/attribute'),
        name:'family-attribute',
        title:'Family Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('familyAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
    /*$('.js-add-variantGroup').on('click', function() {
        window.location.href = $.getURL('/pim/families/{familyId}/variantGroups');
    });*/
    $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
    $.addModal({
        selector: '.js-add-variantGroup',
        url: $.getURL('/pim/families/{familyId}/variantGroups/create'),
        name:'variant-group',
        title:'Create Variant Group',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('variantGroups');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
});