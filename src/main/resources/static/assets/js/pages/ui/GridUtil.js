(function(){
    $.extend({
        changePasswordButton: function(options){
            return {
                name: 'CHANGE_PASSWORD',
                style: 'primary',
                title: 'Change Password',
                icon: 'icon-key',
                click: function (row) {
                   /* window.location.href = $.getURLWithRequestParams(options.pageUrl+'changePassword/' + row.externalId, options.urlParams || {});*/
                    $.showModal({
                        url: $.getURL(options.pageUrl+'changePasswordView'),
                        data:{'id': row.externalId},
                        name:'reset-password',
                        title:'Reset Password',
                        buttons: [
                            {text: 'SAVE', style: 'primary', close: true, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){toastr.success('Password updated successfully', 'Password Changed');});}},
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ],
                    });
                }
            };
        },

        detailsButton: function(options) {
            return {
                name: 'DETAILS',
                style: 'info',
                title: 'Details',
                icon: 'icon-eye',
                click: function (row) {
                    window.location.href = $.getURLWithRequestParams(options.pageUrl + row.externalId, options.urlParams || {});
                }
            };
        },

        cloneButton: function(options) {
            return {
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
        },

        toggleStatusButton: function(options) {
            return {
                name: 'TOGGLE_STATUS',
                style: 'danger',
                title: 'Disable',
                icon: 'icon-ban',
                click: function(row){
                    $.toggleStatus(
                        $.getURL((typeof options.toggleUrl === 'undefined' || options.toggleUrl === '' ? options.pageUrl + '{externalId}/active/{active}' : options.toggleUrl), {
                            externalId: row.externalId,
                            active: row.active,
                            discontinued: row.discontinued
                        }),
                        options.names[1],
                        $.refreshDataTable.bind(this, options.names[0]), row.active);
                }
            };
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
                pageLength: options.pageLength,
                reorderCallback: options.reorderCallback || function(){},
                reordering: options.reordering,
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
                            case 'CHANGE_PASSWORD':
                                buttons[i] = $.changePasswordButton(options);
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
            var columns = options.columns;
            columns[columns.length] = { data: 'active', name : 'active' , title : 'Status', orderable: false, render: function(data, type, row, meta){return $.renderStatusColumn(row);}};
            columns[columns.length] = { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            $.initGrid(Object.assign(options, {
                columns: columns,
                buttons: buttons.length > 0 ? buttons : [
                    $.detailsButton(options),
                    $.toggleStatusButton(options)
                ]
            }));
        }

    });
})();