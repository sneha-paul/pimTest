$( document ).ready(function() {
    $.initDataTable({
        selector: '#paginatedAvailableAxisAttributesTable',
        name: 'availableAxisAttributes',
        type: 'TYPE_3A',
        url: $.getURL('/pim/families/{familyId}/variantGroups/{variantGroupId}/axisAttributes/available/list'),
        columns: [
            {data: 'name', name: 'name', title: 'Name'},
            {data: 'id', name: 'id', title: 'ID'},
            {data: 'actions', name: 'actions', title: 'Actions', orderable: false}
        ]
    });
});