(function(){
    function Page() {
        var attributes = {};

        this.setAttributes = function(data) {
            attributes = Object.assign({}, attributes, data);
        };

        this.getAttributes = function() {
            return attributes;
        };
    }

    $.extend({
        page: function() {
            return page;
        },
        initPage: function(attributes) {
            page.setAttributes(attributes);
        },
        initTreeDataTable: function(options) {
            options.treeDataTable = options.treeDataTable || true;
            var displayed = new Set([]);
            var collapsed = options.collapsed || false;
            var collapsible = true === options.collapsible;
            function getData(dt, refresh) {
                $.ajax({
                    url: options.url,
                    data: {},
                    method: 'GET',
                    success: function (data) {
                        $.each(data, function (i, value) {
                            value.actions = '';
                        });
                        if(refresh) {
                            dt.clear().draw();
                        }
                        dt.rows.add(data);
                        draw(dt, refresh);
                    },
                    error: function(jqXHR, textStatus, errorThrown) {
                        $.ajaxError(jqXHR, function(){
                            toastr.error('An error occurred while loading the data, try refreshing the page', "Error", {timeOut: 3000});
                        });
                    }

                });
            }
            function draw(dt, refresh) {
                if(refresh) {
                    var regex = "^(0";
                    displayed.forEach(function (value) {
                        regex = regex + "|" + value;
                    });
                    regex = regex + ")$";
                    dt.columns([1]).search(regex, true, false).draw();
                } else {
                    if(collapsed) {
                        dt.columns([0]).search('^(0)$', true, false).draw();
                    } else {
                        dt.columns.adjust().draw();
                    }
                }
            }
            var dt = $.bindDataTable(options, $(options.selector).DataTable( {
                data: [],
                ordering: false,
                info: false,
                searching: true,
                paging:   false,
                rowReorder: typeof options.reordering === 'undefined' || options.reordering ? {snapX: 10} : false,
                createdRow: function (row, data, index) {
                    $(row).addClass('disable-select parent-' + data.parent);
                    if(data.isParent) {
                        if(collapsible || collapsed) {
                            $(row).addClass('parent-node');
                        }
                        if(!collapsed) {
                            displayed.add(data.key);
                            $(row).addClass('details');
                        }
                    }
                },
                columns: [
                    { data: 'level', visible: false },
                    { data: 'parent', visible: false },
                    { data: 'name', title: 'Category Name',
                        render: function ( data, type, row, meta ) {
                            var level = row.level;
                            return '<div style="padding-left:' + (level * 25) + 'px"><div class="float-left"><span class="collapsed-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-right p-r-10 js-ctrl "  style="cursor: pointer"></i><i class=" text-primary fa fa-folder"></i></span><span class="expanded-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-down p-r-5 js-ctrl"  style="cursor: pointer"></i><i class="text-primary fa fa-folder-open"></i></span></div><div class="float-left p-l-10"><h6>' + data + '</h6></div></div>';
                        }
                    },
                    { data: 'key', title: 'Category ID'},
                    { data: 'active', title: 'Status',
                        render: function(data, type, row, meta) {
                            return 'Y' === data ? '<span class="badge badge-success">Active</span>' : '<span class="badge badge-danger">Inactive</span>';
                        }
                    },
                    {
                        data: 'actions', title: 'Actions',
                        render: function(data, type, row, meta) {
                            var actions;
                            var icon = 'icon-ban', action = 'Disable', btnClass = 'btn-danger';
                            if('Y' !== row.active) {
                                icon = 'icon-check';
                                action = 'Enable';
                                btnClass = 'btn-success';
                            }
                            actions = '<a href="' + $.getURLWithRequestParams((options.url2 ? options.url2 : options.url) + row.key, options.urlParams) + '" class="btn btn-sm btn-info" title="Details"><i class="icon-eye"></i></a> ';
                            actions += '<button type="button" class="btn btn-sm ' + btnClass + ' js-toggle-status" data-external-id="' + row.key + '" data-active="' + row.active + '" title="' + action + '"><i class="' + icon + '"></i></button>';
                            return actions;
                        }
                    }
                ]
            }));

            getData(dt, false);


            $(options.selector + ' tbody').off().on('click', 'tr td:first-child .js-ctrl', function () {
                var _tr = $(this).closest('tr');
                var _row = dt.row(_tr);
                var _key = _row.data().key;
                if (displayed.has(_key)) {
                    function collapseChildNodes(trs) {
                        for(var i = 0; i < trs.length; i ++) {
                            var tr = $(trs[i]);
                            var row = dt.row(tr);
                            var key = row.data().key;

                            var childTrs = tr.parent().find('.parent-' + key);
                            if(childTrs.length > 0) {
                                collapseChildNodes(childTrs);
                            }
                            displayed.delete(key);
                            tr.removeClass('details');
                        }
                    }
                    collapseChildNodes([_tr]);

                } else {
                    displayed.add(_key);
                    _tr.addClass('details');
                }
                draw(dt, true);
            });

            $(options.selector).off().on('click', '.js-toggle-status', function() {
                $.toggleStatus(
                    $.getURL(options.url2 + '{externalId}/active/{active}', {
                        externalId: $(this).data('external-id'),
                        active: $(this).data('active'),
                        discontinued: $(this).data('discontinued')
                    }),
                    typeof options.names !== 'undefined' ? options.names[1] : 'entity',
                    $.refreshDataTable.bind(this, typeof options.names === 'undefined' ? options.name : options.names[0]), $(this).data('active'));
            });
        },
        initDataTable: function(options) {
            $.bindDataTable(options, $(options.selector).DataTable( {
                processing: true,
                serverSide: true,
                pageLength: 25,
                conditionalPaging: true,
                searching: false,
                rowReorder: typeof options.reordering !== 'undefined' && options.reordering ? {snapX: 10} : false,
                language: {
                    info: "_START_ to _END_ of _TOTAL_"
                },
                ajax: {
                    url: options.url + (options.type !== 'TYPE_1' && options.type !== 'TYPE_1A' ? '' : 'list'),
                    data: function ( data ) {
                        //process data before sent to server.
                    },
                    dataSrc: function(json) {
                        $.each(json.data, function(index, value) {
                            value.seq = 1;
                            value.actions = '';
                            $.each(options.buttons, function(index, button){
                                switch(button.name) {
                                    case 'TOGGLE_STATUS':
                                        if('Y' !== value.active) {
                                            button.icon = 'icon-check';
                                            button.title = 'Enable';
                                            button.style = 'success';
                                        } else {
                                            button.icon = 'icon-ban';
                                            button.title = 'Disable';
                                            button.style = 'danger';
                                        }
                                        break;
                                }
                                value.actions += '<button type="button" class="btn btn-sm btn-' + button.style + ' js-' + button.name + '" title="' + button.title + '"><i class="' + button.icon + '"></i></button> ';
                            });
                            if('GROUP_4' === options.buttonGroup || 'GROUP_4A' === options.buttonGroup) {
                                value.actions = '';
                                if('Y' === value.selectable) {
                                    value.actions = '<button type="button" class="btn btn-outline-primary js-attribute-options" data-name="' + value.name + '" data-external-id="' + value.fullId + '" title="Show Attribute Options"><i class="fa fa-list"></i></button>';
                                }
                                if('GROUP_4A' === options.buttonGroup) {
                                    if (value.scopable === 'Y') {
                                        value.scopable = '<span class="badge badge-success">Yes</span>';
                                    } else {
                                        value.scopable = '<span class="badge badge-danger">No</span>';
                                    }
                                }
                            } else if('TYPE_4' === options.type) {
                                value.actions = '<button type="button" class="btn btn-sm btn-secondary js-edit-pricing-attribute-data" data-external-id="' + value.externalId + '" title="Edit"><i class="fa fa-edit"></i></button> '/* +
                                                '<button type="button" class="btn btn-sm btn-danger js-sweetalert" title="Delete" data-type="confirm"><i class="fa fa-trash-o"></i></button> '*/;
                            } else if(options.type === 'TYPE_1' || options.type === 'TYPE_1A') {
                                /*var icon = 'icon-ban', action = 'Disable', btnClass = 'btn-danger';
                                if('Y' !== value.active) {
                                    icon = 'icon-check';
                                    action = 'Enable';
                                    btnClass = 'btn-success';
                                }
                                value.actions = '<a href="' + (options.url2 ? options.url2 : options.url) + value.externalId + '" class="btn btn-sm btn-info" title="Details"><i class="icon-eye"></i></a> ';
                                if(options.type === 'TYPE_1') {
                                    value.actions += '<button type="button" class="btn btn-sm btn-primary js-clone" data-external-id="' + value.externalId + '" title="Clone"><i class="icon-docs"></i></button> ';
                                }
                                value.actions += '<button type="button" class="btn btn-sm ' + btnClass + ' js-toggle-status" data-external-id="' + value.externalId + '" data-active="' + value.active + '" title="' + action + '"><i class="' + icon + '"></i></button>';*/
                            } else if(options.type === 'TYPE_2') {

                                if('GROUP_4B' === options.buttonGroup) {
                                    var icon = 'fa-square-o', color = '', title = 'No';
                                    if('Y' === value.scopable) {
                                        title = 'Yes';
                                        icon = 'fa-check-square-o';
                                        color = ' text-success';
                                    }
                                    value.scopable = '<span class="js-scopable' + color + '" title="' + title + '" data-scopable="' + value.scopable + '" data-id="' + value.id + '"><i class="fa ' + icon + '"></i></span>';

                                    for(var channelId in $.getPageAttribute('channels')) {
                                        if($.getPageAttribute('channels').hasOwnProperty(channelId)) {
                                            if (_.isEmpty(value.scope[channelId]) || value.scope[channelId] === 'OPTIONAL') {
                                                value['channel_' + channelId] = '<span class="js-scope-selector" title="Optional" data-channel="' + channelId + '" data-scope="OPTIONAL" data-id="' + value.id + '"><i class="fa fa-square-o"></i></span>';
                                            } else if (value.scope[channelId] === 'REQUIRED') {
                                                value['channel_' + channelId] = '<span class="js-scope-selector text-success" title="Required" data-channel="' + channelId + '" data-scope="REQUIRED" data-id="' + value.id + '"><i class="fa fa-check-square-o"></i></span>';
                                            } else if (value.scope[channelId] === 'LOCKED') {
                                                value['channel_' + channelId] = '<span class="text-primary" title="Variant Axis"><i class="icon-target"></i></span>';
                                            } else {
                                                value['channel_' + channelId] = '<span class="js-scope-selector text-danger" title="Not Applicable" data-channel="' + channelId + '" data-scope="NOT_APPLICABLE" data-id="' + value.id + '"><i class="icon-ban" style="font-weight: bold"></i></span>';
                                            }
                                        }
                                    }
                                } else if('GROUP_4C' === options.buttonGroup) {
                                    /*var icon = 'fa-square-o', color = '', title = 'No';
                                    if('Y' === value.scopable) {
                                        title = 'Yes';
                                        icon = 'fa-check-square-o';
                                        color = ' text-success';
                                    }
                                    value.scopable = '<span class="js-scopable' + color + '" title="' + title + '" data-scopable="' + value.scopable + '" data-id="' + value.id + '"><i class="fa ' + icon + '"></i></span>';
*/
                                    for(var channelId in $.getPageAttribute('channels')) {
                                        if($.getPageAttribute('channels').hasOwnProperty(channelId)) {
                                            if (value.externalId !== value.channelVariantGroup[channelId]) {
                                                value['channel_' + channelId] = '<span class="js-channel-selector" data-channel="' + channelId + '" data-id="' + value.externalId + '"><i class="fa fa-square-o"></i></span>';
                                            } else if (value.externalId === value.channelVariantGroup[channelId]) {
                                                value['channel_' + channelId] = '<span class="text-success" data-channel="' + channelId + '" data-id="' + value.externalId + '"><i class="fa fa-check-square-o"></i></span>';
                                            } /*else {
                                                value['channel_' + channelId] = '<span class="js-scope-selector text-danger" title="Not Applicable" data-channel="' + channelId + '" data-scope="NOT_APPLICABLE" data-id="' + value.id + '"><i class="icon-ban" style="font-weight: bold"></i></span>';
                                            }*/
                                        }
                                    }
                                } else {
                                    value.actions = '<a href="' + $.getURLWithRequestParams((options.url2 ? options.url2 : options.url) + value.externalId, options.urlParams) + '" class="btn btn-sm btn-info" title="Details"><i class="icon-eye"></i></a> ' +
                                    '<a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Enable/Disable" data-type="confirm"><i class="icon-ban"></i></a> ';
                                        // '<a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Disable" data-type="confirm"><i class="icon-trash"></i></a>';
                                }
                            } else if(options.type === 'TYPE_3') {
                                value.actions = '<button type="button" class="btn btn-success js-add" data-external-id="' + value.externalId + '" title="Add"><span class="sr-only">Add</span> <i class="fa fa-save"></i></button>';
                            } else if(options.type === 'TYPE_3A') {
                                value.actions = '<button type="button" class="btn btn-success js-add" data-id="' + value.id + '" title="Add"><span class="sr-only">Add</span> <i class="fa fa-save"></i></button>';
                            }

                            if(/*options.type === 'TYPE_1' || */options.type === 'TYPE_2') {
                                //alert(value.discontinued)
                                if(value.discontinued === 'Y'){
                                    value.active = '<span class="badge badge-warning">Discontinued</span>';
                                }else{
                                    if (value.active === 'Y') {
                                        value.active = '<span class="badge badge-success">Active</span>';
                                    } else {
                                        value.active = '<span class="badge badge-danger">Inactive</span>';
                                    }
                                }

                            }

                            if(typeof value.group !== 'undefined') {
                                var groups = value.group.split('|');
                                if(groups.length > 0) {
                                    value.group = groups[0];
                                }
                                for(var i = 1; i < groups.length; i ++) {
                                    value.group += '<i class="text-primary p-l-5 p-r-5 fa fa-caret-right"></i>' + groups[i];
                                }
                            }

                        });
                        return json.data;
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                        $.ajaxError(jqXHR, function(){
                            toastr.error('An error occurred while loading the data, try refreshing the page', "Error", {timeOut: 3000});
                        });
                    }
                },
                columns: options.columns
            }));
            const dataTableName = typeof options.names === 'undefined' ? options.name : options.names[0];
            $.each(options.buttons, function(index, button){
                $(options.selector).on('click', '.js-' + button.name, function () {
                    let row = $.getDataTable(dataTableName).row($(this).closest('tr'));
                    if(button.click) {
                        button.click(row.data());
                    }
                });
            });

            $.getDataTable(dataTableName).on( 'row-reorder', function ( e, diff, edit ) {
                const oTable = $.getDataTable(dataTableName);
                let src = edit.triggerRow.data();
                let direction = diff.length > 1 && oTable.row( diff[diff.length - 1].node ).data().externalId === src.externalId ? 'DOWN' : 'UP';
                console.log(direction);
                let dest = {};
                if(diff.length > 1) {
                    dest = oTable.row( diff[direction === 'UP' ? 1 : diff.length - 2].node ).data();
                    console.log(src.externalId);
                    console.log(dest.externalId);
                }
            });

            /*$(options.selector).on('click', '.js-toggle-status', function () {
                if('TYPE_1' === options.type) {
                    $.toggleStatus(
                        $.getURL(options.url + '{externalId}/active/{active}', {
                            externalId: $(this).data('external-id'),
                            active: $(this).data('active'),
                            discontinued: $(this).data('discontinued')
                        }),
                        typeof options.names !== 'undefined' ? options.names[1] : 'entity',
                        $.refreshDataTable.bind(this, typeof options.names === 'undefined' ? options.name : options.names[0]), $(this).data('active'));
                }
            });*/

            $(options.selector).on('click', '.js-scope-selector', function () {
                var url = $.getURL(options.url + '/{familyAttributeId}/scope/{scope}', {
                    familyAttributeId: $(this).data('id'),
                    scope: $(this).data('scope'),

                });
                var data = {channelId: $(this).data('channel')};
                $.ajaxSubmit({
                    url: url,
                    data: data,
                    method: 'PUT',
                    successMessage: [],
                    errorMessage: ['Error Setting the Scope', 'An error occurred while setting the attribute scope'],
                    successCallback: function(data) {
                        $.refreshDataTable(typeof options.names === 'undefined' ? options.name : options.names[0]);
                    }
                });
            });

            $(options.selector).on('click', '.js-channel-selector', function () {
                var url = $.getURL(options.url + '/{variantGroupId}/channels/{channelId}', {
                    variantGroupId: $(this).data('id'),
                    channelId: $(this).data('channel'),

                });
                var data = {};
                $.ajaxSubmit({
                    url: url,
                    data: data,
                    method: 'PUT',
                    successMessage: [],
                    errorMessage: ['Error Setting the Channel Variant Group', 'An error occurred while setting the channel variant group'],
                    successCallback: function(data) {
                        $.refreshDataTable(typeof options.names === 'undefined' ? options.name : options.names[0]);
                        $.refreshDataTable('familyAttributesScope');
                    }
                });
            });

            $(options.selector).on('click', '.js-scopable', function () {
                var url = $.getURL(options.url + '/{familyAttributeId}/scopable/{scopable}', {
                    familyAttributeId: $(this).data('id'),
                    scopable: $(this).data('scopable'),

                });
                var data = {};
                $.ajaxSubmit({
                    url: url,
                    data: data,
                    method: 'PUT',
                    successMessage: [],
                    errorMessage: ['Error Setting the Scope', 'An error occurred while setting the attribute scope'],
                    successCallback: function(data) {
                        $.refreshDataTable(typeof options.names === 'undefined' ? options.name : options.names[0]);
                    }
                });
            });

            /*$(options.selector).on('click', '.js-clone', function () {
                if('TYPE_1' === options.type) {
                    $.cloneInstance(
                        $.getURL(options.url + '{externalId}/clone/{cloneType}', {
                            externalId: $(this).data('external-id'),
                            cloneType: 'LIGHT'
                        }),
                        typeof options.names !== 'undefined' ? options.names[1] : 'entity',
                        $.refreshDataTable.bind(this, typeof options.names === 'undefined' ? options.name : options.names[0]));
                }
            });*/

            $(options.selector).on('click', '.js-attribute-options', function(){
                if('GROUP_4' === options.buttonGroup) {
                    $.showModal({
                        url: $.getURL('/pim/attributeCollections/{collectionId}/attributes/{attributeId}/options', {'attributeId': $(this).data('external-id')}),
                        name:'attribute-options',
                        title: $(this).data('name') + ' Options',
                        buttons: [
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                } else if('GROUP_4A' === options.buttonGroup) {
                    $.showModal({
                        url: $.getURL('/pim/families/{familyId}/attributes/{attributeId}/options', {'attributeId': $(this).data('external-id')}),
                        name:'attribute-options',
                        title:$(this).data('name') + ' Options',
                        buttons: [
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                }
            });
            $(options.selector).on('click', '.js-edit-pricing-attribute-data', function(){
                $.showModal({
                    url: $.getURL('/pim/products/{productId}/variants/{productVariantId}/pricingDetails/{pricingAttributeId}?ts=' + new Date().getTime(), {'pricingAttributeId': $(this).data('external-id')}),
                    data: {channelId: $.getPageAttribute('channelId')},
                    name:'edit-pricing-details',
                    title:'Edit Pricing Details',
                    buttons: [
                        {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(data){data.refreshPage ? $.refreshPage() : $.reloadDataTable('variantPricing');$.closeModal();});}},
                        {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                    ]
                });
            });
            /*$(options.selector).on('click', '.js-attribute-options', function(){
                if('GROUP_4' === options.buttonGroup) {
                    $.showModal({
                        url: $.getURL('/pim/productFamilies/{productFamilyId}/attributes/{attributeId}/options', {'attributeId': $(this).data('external-id')}),
                        name:'attribute-options',
                        title:'Attribute Options',
                        buttons: [
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                }
            });*/
        },
        refreshDataTable: function(name) {
            if(!$.reloadTreeDataTable(name)) {
                $.getDataTable(name).ajax.reload(null, false);
            }
        },
        reloadDataTable: function(name) {
            if(!$.reloadTreeDataTable(name)) {
                $.getDataTable(name).destroy();
                $.initDataTable($.getDataTableOptions(name));
            }
        },

        reloadTreeDataTable: function(name) {
            var options = $.getDataTableOptions(name);
            var treeDataTable = typeof options.treeDataTable !== 'undefined' || false;
            if(treeDataTable) {
                $.getDataTable(name).destroy();
                $.initTreeDataTable(options);
                return true;
            }
            return false;
        },

        initMultiList: function(options) {
            $(options.ids).nestable({maxDepth: 1, group: 1}).on('change', function(){options.changeCallback()});
        },
        addModal: function(options) {
            $(options.selector).off().on('click', function() {
                $.showModal(options);
            });
        },
        showModal: function(options) {
            var defaultOptions = {
                loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
                size: eModal.size.lg,
                name:'Modal',
                title: 'Modal',
                error: function(jqXHR) {
                    $.ajaxError(jqXHR, function(){

                    });
                }
            };
            eModal.ajax(Object.assign({},defaultOptions, options));
        },
        closeModal: function() {
            $('.js-eModal-close').trigger('click');
        },
        bindDataTable: function(options, dataTable) {
            var name = typeof options.names === 'undefined' ? options.name : options.names[0];
            $.setPageAttribute(name + '_datatable', dataTable);
            $.setPageAttribute(name + '_datatable_options', options);
            return dataTable;
        },
        unbindDataTable: function(options) {
            var name = typeof options.names === 'undefined' ? options.name : options.names[0];
            if($.getDataTable(name)) {
                $.destroyDataTable(name);
            }
        },
        getDataTable: function(name) {
            return $.getPageAttribute(name + '_datatable');
        },
        destroyDataTable: function(name) {

        },
        getDataTableOptions: function(name) {
            return $.getPageAttribute(name + '_datatable_options');
        },
        setPageAttributes: function(attributes) {
            page.setAttributes(attributes);
        },
        getPageAttributes: function() {
            return page.getAttributes();
        },
        lockInput: function(parent) {
            $('input[disabled="disabled"],select[disabled="disabled"],textarea[disabled="disabled"]', parent || 'body').each(function () {
                var msg = '';
                if ($(this).hasClass('js-parent-level')) {
                    msg = 'This property can only be modified at the product level';
                    $(this).after($('<span class="js-level-locked icon-arrow-up" title="' + msg + '"></span>').on('click', function () {
                        toastr.info(msg, 'Readonly Property');
                    }));
                } else if ($(this).hasClass('js-variant-axis')) {
                    msg = 'This is the variant axis and is not editable';
                    $(this).after($('<span class="js-axis-locked icon-target" title="' + msg + '"></span>').on('click', function () {
                        toastr.info(msg, 'Readonly Property');
                    }));
                } else {
                    msg = 'This property is not editable';
                    $(this).after($('<span class="js-locked icon-lock" title="' + msg + '"></span>').on('click', function () {
                        toastr.info(msg, 'Readonly Property');
                    }));
                }
            });
        },
        formatNumber: function(nStr) {
            nStr = nStr.replace(/\D/g,'');
            nStr += '';
            var x = nStr.split('.');
            var x1 = x[0];
            var x2 = x.length > 1 ? '.' + x[1] : '';
            var rgx = /(\d+)(\d{3})/;
            while (rgx.test(x1)) {
                x1 = x1.replace(rgx, '$1' + ',' + '$2');
            }
            return x1 + x2;
        },
        setPageAttribute: function(key, value) {
            var attribute = {};
            attribute[key] = value;
            $.setPageAttributes(attribute);
        },
        getPageAttribute: function(key) {
            return $.getPageAttributes()[key];
        },
        getURL: function(uri, context) {
            var url = uri;
            var match;
            var regex = /(\{(\S+?)\})/g;
            while(match = regex.exec(uri)) {
                if(context && match[2] in context) {
                    url = url.replace(match[1], context[match[2]]);
                } else if(match[2] in $.getPageAttributes()) {
                    url = url.replace(match[1], $.getPageAttribute(match[2]));
                }
            }
            return encodeURI((url.startsWith('http') || url.startsWith('www') ? '' : $.getPageAttribute("urlRoot") ) + url);
        },
        getURLWithRequestParams: function(uri, requestParams, fullURL, context) {
            if(_.isEmpty(requestParams)) {
                return uri;
            }
            if(uri.indexOf('?') === -1) {
                uri += '?'
            } else {
                uri += '&';
            }
            for(var param in requestParams) {
                if(requestParams.hasOwnProperty(param)) {
                    uri += (uri.endsWith('?') || uri.endsWith('&') ? '' : '&');
                    uri += param + '=' + requestParams[param];
                }
            }
            return $.getURL(uri, context);
        },
        pageURL: function() {
            return $.getPageAttribute("pageUrl");
        },
        resizeTextArea: function(el) {
            if ($(el).scrollTop()) {
                $(el).height(function(i,h){
                    return h + 20;
                });
            }
        },
        refreshPage: function(params, hash) {
            if(!params) {
                params = {};
            }
            if(typeof hash === 'undefined') {
                hash = $('.nav-link.active').attr('href');
            }
            params.reload = true;
            $.ajax({
                url: $.pageURL(),
                data: params,
                method: 'GET',
                async: true,
                success: function (data) {
                    /*$('#js-body-container').html(data);
                    if(hash) {
                        $('a.nav-link[href*="' + hash + '"]').trigger('click');
                    } else {
                        $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
                    }*/

                    $('#js-body-container').fadeOut('fast', function(){
                        $('#js-body-container').html(data);
                        if(hash) {
                            $('a.nav-link[href*="' + hash + '"]').trigger('click');
                        } else {
                            $('a.nav-link[href*="' + window.location.hash + '"]').trigger('click');
                        }
                        $('#js-body-container').fadeIn('fast', function(){

                        });
                    });



                },
                error: function (jqXHR) {
                    $.ajaxError(jqXHR, function(){
                        window.location.href = $.pageURL();
                    });
                }
            });
        },
        bindFormSubmit: function(submitEl) {
            $(submitEl).on('click', function(e) {
                $.submitAction(this, e);
            });
        },
        submitAction: function(e, submitEl, successCallback) {
            e.preventDefault();
            $.submitForm($(submitEl).closest('form'), successCallback);
        },
        submitForm: function(formEl, successCallback) {
            $.clearFieldErrors(formEl);

            var method = $(formEl).data('method');
            if(typeof method === 'undefined' || method === '') {
                method = 'POST';
            }
            $.ajax({
                url: $.getURL($(formEl).attr('action')),
                data: $(formEl).serialize(),
                method: method,
                success: function(data) {
                    if(data.success) {
                        toastr.success($(formEl).data('success-message')[0], $(formEl).data('success-message')[1]);
                        if(successCallback) {
                            successCallback(data);
                        } else {
                            if(data.refresh) {
                                $.refreshPage();
                            } else if(typeof data.path !== 'undefined' && data.path !== '') {
                                window.location.href = data.path;
                            }
                        }
                    } else {
                        $.renderFieldErrors(formEl, data.fieldErrors);
                        toastr.error($(formEl).data('error-message')[0], $(formEl).data('error-message')[1], {timeOut: 3000})
                    }
                },
                error: function (jqXHR) {
                    $.ajaxError(jqXHR, function(){
                        toastr.error('An error occurred while saving the data', "Error", {timeOut: 3000});
                    });
                }
            });
        },

        sessionExpired: function() {
            swal({
                title: 'Session Expired',
                text: "Due to inactivity, your session has been expired. Do you want to login?",
                type: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#28a745',
                cancelButtonColor: '#dc3545',
                confirmButtonText: 'Yes, login',
                cancelButtonText: 'No, logout',
                allowOutsideClick: false,
                animation: false
            }).then((result) => {
                if (result.value) {
                    window.location.reload();
                } else if (result.dismiss === swal.DismissReason.cancel) {
                    window.location.href = "/logout"
                }
            });
            setTimeout(function(){$.closeModal();}, 1000);
        },

        ajaxError: function(jqXHR, errorCallback) {
            var responseJSON = jqXHR.responseJSON ? jqXHR.responseJSON : JSON.parse(jqXHR.responseText);
            if(403 === responseJSON.status && 'Session Expired' === responseJSON.message) {
                $.sessionExpired();
            } else {
                errorCallback();
            }
        },

        ajaxSubmit: function(options) {
            var method = options.method;
            if(typeof method === 'undefined' || method === '') {
                method = 'POST';
            }

            $.ajax({
                url: $.getURL(options.url),
                data: options.data,
                method: method,
                success: function(data) {
                    if(data.success) {
                        if(options.successMessage && options.successMessage.length === 2) {
                            toastr.success(options.successMessage[1], options.successMessage[0]);
                        }
                        if(options.successCallback) {
                            options.successCallback(data);
                        } else {
                            if(data.refresh) {
                                $.refreshPage();
                            }else if(typeof data.path !== 'undefined' && data.path !== '') {
                                window.location.href = data.path;
                            }
                        }
                    } else {
                        toastr.error(options.errorMessage[1], options.errorMessage[0], {timeOut: 3000})
                    }
                },
                error: function (jqXHR) {
                    $.ajaxError(jqXHR, function(){
                        toastr.error(options.errorMessage[1], options.errorMessage[0], {timeOut: 3000});
                    });
                }
            });

        },

        toggleStatus: function(url, entityName, callback, isActive) {
            $.confirmedAJAXRequest({
                url: url,
                method: 'PUT',
                text: 'This will ' + (isActive === 'Y' ? 'disable' : 'enable') + ' the ' + entityName + "!",
                confirmButtonText: 'Yes, ' + (isActive === 'Y' ? 'disable' : 'enable') +' it!',
                confirmButtonColor: isActive === 'Y' ? '#dc3545' : '#28a745',
                successTitle: isActive === 'Y' ? 'Disabled!' : 'Enabled!',
                successText: 'The ' + entityName + ' has been ' + (isActive === 'Y' ? 'disabled.' : 'enabled.')
            }, callback);
        },

        cloneInstance: function(url, entityName, callback) {
            $.confirmedAJAXRequest({
                url: url,
                method: 'PUT',
                text: 'This will clone the ' + entityName + "!",
                confirmButtonText: 'Yes, clone it!',
                confirmButtonColor: '#28a745',
                successTitle: 'Cloned!',
                successText: 'The ' + entityName + ' has been cloned.'
            }, callback);
        },

        confirmedAJAXRequest: function(options, callback) {
            const defaultOptions = {
                method: 'PUT',
                title: 'Are you sure?',
                text: 'Do you want to continue?',
                type: 'question',
                showCancelButton: true,
                confirmButtonColor: '#28a745',
                confirmButtonText: 'Yes!',
                errorTitle: 'Error!',
                errorText: 'The AJAX request failed!',
                errorType: 'error',
                successTitle: 'Done!',
                successText: 'The AJAX request completed successfully!',
                successType: 'success',
                callback: callback
            };
            options = Object.assign({},defaultOptions, options);
            swal({
                title: options.title,
                text: options.text,
                type: options.type,
                showCancelButton: options.showCancelButton,
                confirmButtonColor: options.confirmButtonColor,
                confirmButtonText: options.confirmButtonText,
                animation: false,


                preConfirm: (yes) => {
                    if(yes) {
                        // Return a new promise.
                        return new Promise(function (resolve, reject) {
                            // Do the usual XHR stuff
                            var req = new XMLHttpRequest();
                            req.open(options.method, options.url);

                            req.onload = function () {
                                // This is called even on 404 etc
                                // so check the status
                                if (req.status == 200) {
                                    // Resolve the promise with the response text
                                    resolve(req.response);
                                }
                                else {
                                    // Otherwise reject with the status text
                                    // which will hopefully be a meaningful error
                                    reject(Error(req.statusText));
                                }
                            };

                            // Handle network errors
                            req.onerror = function () {
                                reject(Error("Network Error"));
                            };

                            // Make the request
                            req.send();
                        });
                    }
                }
            }).then(function(response) {
                var data = typeof response.value !== 'undefined' ? JSON.parse(response.value) : {};
                if (data.success) {
                    swal({title: options.successTitle, text: options.successText, type: options.successType, timer: 3000, animation: false});
                    callback();
                }
            }, function(error) {
                swal(options.errorTitle, options.errorText, options.errorType);
            });
        },
        confirmedAJAXRequest1: function(options, callback) {
            var defaultOptions = {
                title: 'Are you sure?',
                text: 'Do you want to continue?',
                type: 'question',
                showCancelButton: true,
                confirmButtonColor: '#dc3545',
                confirmButtonText: 'Yes!',
                errorTitle: 'Error!',
                errorText: 'The AJAX request failed!',
                errorType: 'error',
                successTitle: 'Done!',
                successText: 'The AJAX request completed successfully!',
                successType: 'success',
            };
            $.extend(true, options, defaultOptions);
            swal({
                title: options.title,
                text: options.text,
                type: options.type,
                showCancelButton: options.showCancelButton,
                confirmButtonColor: options.confirmButtonColor,
                confirmButtonText: options.confirmButtonText,

                preConfirm: (yes) => {
                    return Promise.resolve($.ajax({
                            url: options.url,
                            data: {},
                            method:'PUT'
                        }));
                        // resolve({result: {value: 'Manu'}});

                    /*return new Promise(function(resolve, reject) {
                            $.ajax({
                                url: options.url,
                                data: {},
                                method:'PUT',
                                success: function(data) {
                                    /!*if(data.success) {
                                        swal(options.successTitle, options.successText, options.successType);
                                        resolve(true);
                                    }*!/
                                    return data;

                                },
                                error: function(err) {
                                    swal(options.errorTitle, options.errorText, options.errorType);
                                    reject(false);
                                }
                            });
                            /!*axios.post(options.url, {})
                                .then(response => {
                                    if (!response.ok) {
                                        throw new Error(response.statusText);
                                    }
                                    return response.json()
                                }).catch(error => {
                                    swal(options.errorTitle, options.errorText, options.errorType);

                            })*!/
                    });*/
                    /*return post(options.url).then(response => {
                        if (!response.ok) {
                            throw new Error(response.statusText);
                        }
                        return response.json()
                    }).catch(error => {
                        swal(options.errorTitle, options.errorText, options.errorType);
                    })*/
                }
            }).then((result) => { console.log(result.value.success);
                if (result.value.success) {
                    swal(
                        options.successTitle,
                        options.successText,
                        options.successType
                    )
                }
            });
        },

        renderFieldErrors: function(formEl, errors) {
            $.each(errors, function(name, error) {
                $(formEl.find('input[name="' + name + '"],select[name="' + name + '"],textarea[name="' + name + '"]').addClass('parsley-error')).closest('.form-group').after($('<ul class="parsley-errors-list filled"><li>' + error['value0'] + '</li></ul>'));
            });
        },
        clearFieldErrors: function(formEl) {
            $(formEl).find('ul.parsley-errors-list').remove();
            $(formEl).find('.parsley-error').each(function() {
                $(this).removeClass('parsley-error');
            })
        },
        loadJavaScript: function loadScript(url, el, callback){

            var script = document.createElement("script")
            script.type = "text/javascript";

            if (script.readyState){  //IE
                script.onreadystatechange = function(){
                    if (script.readyState === "loaded" ||
                        script.readyState === "complete"){
                        script.onreadystatechange = null;
                        if(callback) callback();
                    }
                };
            } else {  //Others
                script.onload = function(){
                    if(callback) callback();
                };
            }

            script.src = url;
            el.parentNode.appendChild(script);
            el.parentNode.removeChild(el);
        },
        linkDropdowns: function(selector) { console.log('in');
            var dropdown = $(selector);
            if(dropdown) {
                var linkedDropdown = $('#' + dropdown.data('link'));
                dropdown.on('change', function(){
                    var values = $(this).find('option:selected').data('values');
                    $.populateDropdown(linkedDropdown, values ? values.split('|') : []);
                });
            }
        },
        populateDropdown: function(dropdownEl, values) {
            $(dropdownEl).find('option:gt(0)').remove();
            for(var i =0; i < values.length; i = i + 2) {
                $(dropdownEl).append($('<option value="' + values[i] + '">' + values[i + 1] + '</option>'));
            }
        },
        initAHAH: function(el) {
            $.each($(el).siblings('script1'), function(i, v) {
                if($(v).attr('src')) {
                    $.loadJavaScript($(v).attr('src'), v);
                } else {
                    eval($(v).innerHTML);
                }
            });
        }
    });
    var page = new Page();
    var loc = window.location;
    $.setPageAttribute("urlRoot", loc.protocol + "//" + loc.hostname + ":" + loc.port);
    $.setPageAttribute("pageUrl", loc.href.replace(/#$/, ''));
    toastr.options.preventDuplicates = true;
    toastr.options.positionClass = 'toast-bottom-right';
})();
