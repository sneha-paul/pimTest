$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableCatalogsTable',
        name: 'availableCatalogs',
        type: 'TYPE_3',
        url: $.getURL('/pim/websites/{websiteId}/catalogs/available/data'),
        columns: [
            { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
            { data: 'externalId', name : 'externalId', title : 'Catalog ID' },
            { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
        ]
    });

    $('#paginatedAvailableCatalogsTable').on('click', '.js-add', function(){
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
                toastr.success('Successfully added the website catalog', 'Website Catalog Added');
            } else {
                toastr.success('Error occurred while adding the website catalog', 'Error Adding Website Catalog');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the website catalog', 'Error Adding Website Catalog');
        });
    });
});