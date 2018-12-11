(function(){
    $.extend({
        entitiesGridDetailsButton: function(options, customButton) {
            const button = {
                name: 'DETAILS',
                style: 'info',
                title: 'Details',
                icon: 'icon-eye',
                click: function (row) {
                    window.location.href = options.url + row.externalId;
                }
            };
            return Object.assign({}, button, customButton || {});
        },
        entitiesGridCloneButton: function(options, customButton) {
            const button = {
                name: 'CLONE',
                style: 'primary',
                title: 'Clone',
                icon: 'icon-docs',
                click: function(row){
                    $.cloneInstance(
                        $.getURL(options.url + '{externalId}/clone/{cloneType}', {
                            externalId: row.externalId,
                            cloneType: 'LIGHT'
                        }),
                        options.names[1],
                        $.refreshDataTable.bind(this, options.names[0]));
                }
            };
            return Object.assign({}, button, customButton || {});
        },
        renderStatusColumn: function(data) {
            if (data.discontinued === 'Y') {
                return '<span class="badge badge-warning">Discontinued</span>';
            } else {
                if (data.active === 'Y') {
                    return '<span class="badge badge-success">Active</span>';
                } else {
                    return '<span class="badge badge-danger">Inactive</span>';
                }
            }
        },
        entitiesGridToggleStatusButton: function(options, customButton) {
            const button = {
                name: 'TOGGLE_STATUS',
                style: 'danger',
                title: 'Disable',
                icon: 'icon-ban',
                click: function(row){
                    $.toggleStatus(
                        $.getURL(options.url + '{externalId}/active/{active}', {
                            externalId: row.externalId,
                            active: row.active,
                            discontinued: row.discontinued
                        }),
                        options.names[1],
                        $.refreshDataTable.bind(this, options.names[0]), row.active);
                }
            };
            return Object.assign({}, button, customButton || {});
        },

        initEntitiesGrid: function(options) {
            $.initDataTable({
                selector: options.selector,
                names: options.names,
                url: options.url,
                type: 'TYPE_1',
                columns: [
                    options.columns[0],
                    options.columns[1],
                    { data: 'active', name : 'active' , title : 'Status', orderable: false, render: function(data, type, row, meta){return $.renderStatusColumn(row);}},
                    { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
                ],
                buttons: [
                    $.entitiesGridDetailsButton(options),
                    $.entitiesGridCloneButton(options),
                    $.entitiesGridToggleStatusButton(options)
                ]
            });
        }

    });
})();