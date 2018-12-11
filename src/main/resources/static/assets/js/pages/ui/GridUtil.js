(function(){
    $.extend({

        detailsButton: function(options, customButton) {
            const button = {
                name: 'DETAILS',
                style: 'info',
                title: 'Details',
                icon: 'icon-eye',
                click: function (row) {
                    window.location.href = $.getURLWithRequestParams(options.pageUrl + row.externalId, options.urlParams || {});
                }
            };
            return Object.assign({}, button, customButton || {});
        },

        cloneButton: function(options, customButton) {
            const button = {
                name: 'CLONE',
                style: 'primary',
                title: 'Clone',
                icon: 'icon-docs',
                click: function(row){
                    $.cloneInstance(
                        $.getURL(options.pageUrl + '{externalId}/clone/{cloneType}', {
                            externalId: row.externalId,
                            cloneType: 'LIGHT'
                        }),
                        options.names[1],
                        $.refreshDataTable.bind(this, options.names[0]));
                }
            };
            return Object.assign({}, button, customButton || {});
        },

        toggleStatusButton: function(options, customButton) {
            const button = {
                name: 'TOGGLE_STATUS',
                style: 'danger',
                title: 'Disable',
                icon: 'icon-ban',
                click: function(row){
                    $.toggleStatus(
                        $.getURL(options.pageUrl + '{externalId}/active/{active}', {
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

        initGrid: function(options) {
            $.initDataTable({
                selector: options.selector,
                names: options.names,
                url: options.dataUrl,
                columns: options.columns,
                buttons: options.buttons
            });
        },

        initEntitiesGrid: function(options) {
            let buttons = [];
            if(options.buttons) {
                $.each(options.buttons, function(i, button) {
                    if(typeof button === 'string') {
                        switch (button) {
                            case 'DETAILS':
                                buttons[i] = $.detailsButton(options);
                                break;
                            case 'CLONE':
                                buttons[i] = $.cloneButton(options);
                                break;
                            case 'TOGGLE_STATUS':
                                buttons[i] = $.toggleStatusButton(options);
                                break;
                        }
                    }
                });
            }
            $.initGrid(Object.assign(options, {
                columns: [
                    options.columns[0],
                    options.columns[1],
                    { data: 'active', name : 'active' , title : 'Status', orderable: false, render: function(data, type, row, meta){return $.renderStatusColumn(row);}},
                    { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
                ],
                buttons: buttons.length > 0 ? buttons : [
                    $.detailsButton(options),
                    $.cloneButton(options),
                    $.toggleStatusButton(options)
                ]
            }));
        },
        initAssociationsGrid: function(options) {
            let buttons = [];
            if(options.buttons) {
                $.each(options.buttons, function(i, button) {
                    if(typeof button === 'string') {
                        switch (button) {
                            case 'DETAILS':
                                buttons[i] = $.detailsButton(options);
                                break;
                            case 'TOGGLE_STATUS':
                                buttons[i] = $.toggleStatusButton(options);
                                break;
                        }
                    }
                });
            }
            $.initGrid(Object.assign(options, {
                columns: [
                    options.columns[0],
                    options.columns[1],
                    { data: 'active', name : 'active' , title : 'Status', orderable: false, render: function(data, type, row, meta){return $.renderStatusColumn(row);}},
                    { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
                ],
                buttons: buttons.length > 0 ? buttons : [
                    $.detailsButton(options),
                    $.toggleStatusButton(options)
                ]
            }));
        }

    });
})();