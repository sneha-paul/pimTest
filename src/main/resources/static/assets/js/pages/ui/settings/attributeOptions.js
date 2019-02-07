$(function(){

    $.initGrid({
        selector: '#paginatedAttributeOptionsTable',
        names: ['attributeOptions', 'attributeOption'],
        dataUrl: $.getURL('/pim/attributeCollections/{collectionId}/attributes/{attributeId}/options/list'),
        columns: [
            { data: 'value', name : 'value' , title : 'Value'},
            { data: 'id', name : 'id', title : 'ID' },
            {
                data: 'active',
                name : 'active' ,
                title : 'Status',
                render: function ( data, type, row, meta ) {
                    return $.renderStatusColumn(row);
                }
            }
        ]
    });


    var container5 = document.getElementById('attributeOptions');
    setTimeout(function(){
        var hot5 = new Handsontable(container5, {
            readOnly: true,
            rowHeaders: true,
            fixedColumnsLeft: 3,
            colHeaders: true,
            stretchH:'first',
            width: '95%',
            columns: [
                {data: 'value'},
                {data: 'id'},
                {data: 'active'}
            ],
            colWidths: [300, 250, 90],
            minSpareRows: 1,

        });

        $.ajax({
            type: "GET",
            url: $.getURL('/pim/attributeCollections/{collectionId}/attributes/{attributeId}/options/list'),
            contentType: "application/json; charset=utf-8",
            dataType: "json",
            success: function (res) {
                hot5.loadData(res.data);
                hot5.updateSettings({
                    colHeaders: ["Value", "ID", "Status"]
                });
            },
            error: function (xhr, status) {
                alert("An error occurred: " + status);
            }

        });

    }, 200);

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