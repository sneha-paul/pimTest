$( document ).ready(function() {
    setTimeout(function(){
        $.initGrid({
            selector: '#paginatedAvailableCatalogsTable',
            names: ['availableCatalogs', 'availableCatalog'],
            dataUrl: $.getURL('/pim/websites/{websiteId}/catalogs/available/list'),
            columns: [
                { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
                { data: 'externalId', name : 'externalId', title : 'Catalog ID' },
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ],
            buttons: [$.addItemButton({action: addCatalog})]
        });
    }, 200);


    function addCatalog(row) {
        $.ajax({
            url: $.getURL('/pim/websites/{websiteId}/catalogs/{catalogId}', {'catalogId': row.externalId}),
            data: {},
            method: 'POST',
            dataType: 'json'
        }).done(function(data) {
            if(data.success === true) {
                $.refreshDataTable('websiteCatalogs');
                $.refreshDataTable('availableCatalogs');
                toastr.success('Successfully added the website catalog', 'Website Catalog Added');
            } else {
                toastr.success('Error occurred while adding the website catalog', 'Error Adding Website Catalog');
            }

        }).fail(function(jqXHR, status) {
            toastr.success('Error occurred while adding the website catalog', 'Error Adding Website Catalog');
        });
    }
});