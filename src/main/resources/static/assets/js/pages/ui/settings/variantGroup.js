$(function(){
    /*$.addModal({
        selector: '.js-add-axisAttribute',
        url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available'),
        name:'axis-attribute',
        title:'Available Variant Axis Attributes',
        buttons: [
            // {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('axisAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });*/

    /*$.initDataTable({
        selector: '#paginatedAxisAttributesTable',
        name: 'axisAttributes',
        type: 'TYPE_2',
        buttonGroup: 'GROUP_4A',
        url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes'),
        columns: [
            {data: 'name', name: 'name', title: 'Attribute Name'},
            {data: 'id', name: 'id', title: 'Attribute ID'},
            {data: 'actions', name: 'actions', title: 'Actions'}
        ]
    });*/

    function saveVariantGroupAxisAttributes() {
        var url = '/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes';
        var data = {

            'axisLevel1AttributeIds' : $('#axisAttributesL1').nestable('serialize'),
            'axisLevel2AttributeIds' : $('#axisAttributesL2').length > 0 ? $('#axisAttributesL2').nestable('serialize') : []
        };
        $.ajaxSubmit({
            url: url,
            data: data,
            successMessage: ['Updated Variant Group Axis Attributes', 'Successfully updated variant group axis attributes'],
            errorMessage: ['Error Updating Variant Group Axis Attributes', 'An error occurred while updating variant group axis attributes']
            /*successCallback: function(data) {
                window.location.href = $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}#axisAttributes');
                window.location.reload();
            }*/
        });
    }

    function saveVariantGroupAttributes() {
        var url = '/pim/families/{familyId}/variantGroups/{variantGroupId}/variantAttributes';
        var data = {

            'variantLevel1AttributeIds' : $('#variantL1').nestable('serialize'),
            'variantLevel2AttributeIds' : $('#variantL2').length > 0 ? $('#variantL2').nestable('serialize') : []
        };
        $.ajaxSubmit({
            url: url,
            data: data,
            successMessage: ['Updated Variant Group Attributes', 'Successfully updated variant group attributes'],
            errorMessage: ['Error Updating Variant Group Attributes', 'An error occurred while updating variant group attributes']
        });
    }

    $.initMultiList({ids : '#availableAxisAttributes,#axisAttributesL1,#axisAttributesL2', changeCallback : saveVariantGroupAxisAttributes});
    $.initMultiList({ids : '#product,#variantL1,#variantL2', changeCallback : saveVariantGroupAttributes});

});