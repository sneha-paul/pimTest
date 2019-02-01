(function(){
    $.extend({

        /** Buttons **/
        changePasswordButton: function(options){
            return {
                name: 'CHANGE_PASSWORD',
                style: 'primary',
                title: 'Change Password',
                icon: 'icon-key',
                click: function (row) {
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

        attributeOptionsButton: function(options) {
          return {
              name: 'ATTRIBUTE_OPTIONS',
              style: 'primary',
              title: 'Show Attribute Options',
              icon: 'fa fa-list',
              check: function(row) {
                return 'Y' === row.selectable;
              },
              click: function(row) {
                  $.showModal({
                      url: $.getURL(options.actionUrl, {'attributeId': row.fullId}),
                      name:'attribute-options',
                      title:row.name + ' Options',
                      buttons: [
                          {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                      ]
                  });
              }
          }
        },

        addItemButton: function(options) {
            return {
                name: 'ADD_ITEM',
                style: 'success',
                title: 'Add',
                icon: 'fa fa-save',
                click: function(row) {
                    options.action(row);
                }
            }
        },

        pricingAttributeDetailsButton: function() {
            return {
                name: 'PRICING_ATTRIBUTE_DETAILS',
                style: 'secondary',
                title: 'Edit',
                icon: 'fa fa-edit',
                click: function(row) {
                    $.showModal({
                        url: $.getURL('/pim/products/{productId}/variants/{productVariantId}/pricingDetails/{pricingAttributeId}?ts=' + new Date().getTime(), {'pricingAttributeId': row.externalId}),
                        data: {channelId: $.getPageAttribute('channelId')},
                        name:'edit-pricing-details',
                        title:'Edit Pricing Details',
                        buttons: [
                            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(data){data.refreshPage ? $.refreshPage() : $.reloadDataTable('variantPricing');$.closeModal();});}},
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                }
            }
        },

        /** Renderers **/
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

        renderChannelSelector: function(data, channelId) {
            if (data.externalId !== data.channelVariantGroup[channelId]) {
                return '<span class="js-channel-selector" data-channel="' + channelId + '" data-id="' + data.externalId + '"><i class="fa fa-square-o"></i></span>'
            } else if (data.externalId === data.channelVariantGroup[channelId]) {
                return '<span class="text-success" data-channel="' + channelId + '" data-id="' + data.externalId + '"><i class="fa fa-check-square-o"></i></span>'
            }
        },

        renderScopable: function(data) {
            let icon = 'fa-square-o', color = '', title = 'No';
            if('Y' === data.scopable) {
                title = 'Yes';
                icon = 'fa-check-square-o';
                color = ' text-success';
            }
            return '<span class="js-scopable' + color + '" title="' + title + '" data-scopable="' + data.scopable + '" data-id="' + data.id + '"><i class="fa ' + icon + '"></i></span>';
        },

        renderScopeSelector: function(data, channelId) {
            if (_.isEmpty(data.scope[channelId]) || data.scope[channelId] === 'OPTIONAL') {
                return '<span class="js-scope-selector" title="Optional" data-channel="' + channelId + '" data-scope="OPTIONAL" data-id="' + data.id + '"><i class="fa fa-square-o"></i></span>';
            } else if (data.scope[channelId] === 'REQUIRED') {
                return '<span class="js-scope-selector text-success" title="Required" data-channel="' + channelId + '" data-scope="REQUIRED" data-id="' + data.id + '"><i class="fa fa-check-square-o"></i></span>';
            } else if (data.scope[channelId] === 'LOCKED') {
                return '<span class="text-primary" title="Variant Axis"><i class="icon-target"></i></span>';
            } else {
                return '<span class="js-scope-selector text-danger" title="Not Applicable" data-channel="' + channelId + '" data-scope="NOT_APPLICABLE" data-id="' + data.id + '"><i class="icon-ban" style="font-weight: bold"></i></span>';
            }
        },

        /** Events **/
        scopableClickEvent: function(row, successCallback) {
            var url = $.getURL('/pim/families/{familyId}/attributes/{familyAttributeId}/scopable/{scopable}', {
                familyAttributeId: row.id,
                scopable: row.scopable
            });
            $.ajaxSubmit({
                url: url,
                data: {},
                method: 'PUT',
                successMessage: [],
                errorMessage: ['Error Setting the Scope', 'An error occurred while setting the attribute scope'],
                successCallback: function(data) {
                    if(typeof successCallback === 'function') {
                        successCallback(data);
                    }
                }
            });
        },

        /** Grids **/
        initGrid: function(options) {
            $.initDataTable({
                selector: options.selector,
                names: options.names,
                url: options.dataUrl,
                searching: typeof options.searching === 'undefined' ?  true : options.searching,
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
            var columns = options.columns;
            if(!options.hideStatus) {
                columns[columns.length] = {
                    data: 'active',
                    name: 'active',
                    title: 'Status',
                    orderable: false,
                    render: function (data, type, row, meta) {
                        return $.renderStatusColumn(row);
                    }
                };
            }
            if(!options.hideActions) {
                columns[columns.length] = {data: 'actions', name: 'actions', title: 'Actions', orderable: false};
            }
            $.initGrid(Object.assign(options, {
                columns: columns,
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