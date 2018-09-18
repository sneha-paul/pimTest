$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableCatalogsTable',
        name: 'availableCatalogs',
        type: 'TYPE_3',
        url: $.getURL('/pim/websites/{websiteId}/catalogs/available/list'),
        columns: [
            { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
            { data: 'externalId', name : 'externalId', title : 'Catalog ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });
    console.log('b4');
    $('#paginatedAvailableCatalogsTable').on('click', '.js-add', function(){console.log('inside');
        var catalogId = $(this).data('external-id');

        $.ajax({
            url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}', {'catalogId': catalogId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('catalogs');
                $.refreshDataTable('availableCatalogs');
            } else {
                alert('Failed');
            }

        }).fail(function(jqXHR, status) {
            alert("Failed:" + status);
        });
    });
});
var state420 = '';