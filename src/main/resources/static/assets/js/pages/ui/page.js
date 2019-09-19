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
                scrollY: 300,
                scrollCollapse: true,
                dom:
                "<'row dt-header'<'col-sm-12 col-md-2'l><'col-sm-12 col-md-10 dt-toolbar'<'dt-buttons'><'dt-filter'f>>>" +
                "<'row dt-body'<'col-sm-12'tr>>" +
                "<'row dt-footer'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
                // rowReorder: typeof options.reordering === 'undefined' || options.reordering ? {snapX: 10} : false,
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
                    { data: 'parentChain', visible: false},
                    { data: 'active', title: 'Status',
                        render: function(data, type, row, meta) {
                            return 'Y' === row.discontinued ? '<span class="badge badge-warning">Discontinued</span>' : 'Y' === data ? '<span class="badge badge-success">Active</span>' : '<span class="badge badge-danger">Inactive</span>';
                        }
                    },
                    {
                        data: 'actions', title: 'Actions',
                        render: function(data, type, row, meta) {
                            var actions;
                            var icon = 'icon-close', action = 'Disable', btnClass = 'btn-danger';
                            if('Y' !== row.active) {
                                icon = 'icon-check';
                                action = 'Enable';
                                btnClass = 'btn-success';
                            }
                            if(row.parentChain !== '') {
                                options.urlParams['parentId'] = '{parentId}';
                            }
                            actions = '<a href="' + $.getURLWithRequestParams((options.url2 ? options.url2 : options.url) + row.key, options.urlParams, '', {parentId: row.parentChain}) + '" class="btn btn-sm btn-info" title="Details"><i class="icon-eye"></i></a> ';
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
        //TODO - need to standardize initTreeDataTable. Currently initTreeDataTable is coupled to category hierarchy
        initTreeDataTable1: function(options) {
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
                // rowReorder: typeof options.reordering === 'undefined' || options.reordering ? {snapX: 10} : false,
                createdRow: function (row, data, index) {
                    $(row).addClass('disable-select parent-' + data.parent);
                    if(data.isParent) {
                        if(collapsible || collapsed) {
                            $(row).addClass('parent-node');
                        }
                        if(!collapsed) {
                            displayed.add(data.id);
                            $(row).addClass('details');
                        }
                    }
                },
                columns: [
                    { data: 'level', visible: false },
                    { data: 'parent', visible: false },
                    { data: 'name', title: 'Asset Name',
                        render: function ( data, type, row, meta ) {
                            var level = row.level;
                            return '<div style="padding-left:' + (level * 25) + 'px"><div class="float-left"><span class="collapsed-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-right p-r-10 js-ctrl "  style="cursor: pointer"></i><i class=" text-primary fa fa-folder"></i></span><span class="expanded-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-down p-r-5 js-ctrl"  style="cursor: pointer"></i><i class="text-primary fa fa-folder-open"></i></span></div><div class="float-left p-l-10"><h6>' + data + '</h6></div></div>';
                        }
                    },
                    // { data: 'key', title: 'Category ID'},
                    // { data: 'parentChain', visible: false},
                    { data: 'active', title: 'Status',
                        render: function(data, type, row, meta) {
                            return 'Y' === data ? '<span class="badge badge-success">Active</span>' : '<span class="badge badge-danger">Inactive</span>';
                        }
                    },
                    {
                        data: 'actions', title: 'Actions',
                        render: function(data, type, row, meta) {
                            var actions;
                            var icon = 'icon-close', action = 'Disable', btnClass = 'btn-danger';
                            if('Y' !== row.active) {
                                icon = 'icon-check';
                                action = 'Enable';
                                btnClass = 'btn-success';
                            }
                            if(row.parentId !== '0') {
                                options.urlParams['parentId'] = '{parentId}';
                            }
                            // let fullId = row.parentChain === '' ? row.key : row.parentChain + '|' + row.key;
                            actions = '<a href="' + $.getURL((options.url2 ? options.url2 : options.url) + row.key) + '" class="btn btn-sm btn-info" title="Details"><i class="icon-eye"></i></a> ';
                            actions += '<button type="button" class="btn btn-sm btn-success js-add-asset-group" data-external-id="' + row.id + '" title="Add Asset Group"><i class="fa fa-folder" style="position: relative;left: -2px;"></i><span style="font-size: 8px;position: absolute;"><i class="fa fa-plus"></i></span></button> ';
                            actions += '<button type="button" class="btn btn-sm btn-primary js-add-asset" data-external-id="' + row.id + '" title="Upload Asset"><i class="fa fa-upload"></i></button> ';
                            // actions += '<button type="button" class="btn btn-sm ' + btnClass + ' js-toggle-status" data-external-id="' + row.key + '" data-active="' + row.active + '" title="' + action + '"><i class="' + icon + '"></i></button>';
                            return actions;
                        }
                    }
                ]
            }));

            getData(dt, false);


            $(options.selector + ' tbody').off().on('click', 'tr td:first-child .js-ctrl', function () {
                var _tr = $(this).closest('tr');
                var _row = dt.row(_tr);
                var _id = _row.data().id;
                if (displayed.has(_id)) {
                    function collapseChildNodes(trs) {
                        for(var i = 0; i < trs.length; i ++) {
                            var tr = $(trs[i]);
                            var row = dt.row(tr);
                            var id = row.data().id;

                            var childTrs = tr.parent().find('.parent-' + id);
                            if(childTrs.length > 0) {
                                collapseChildNodes(childTrs);
                            }
                            displayed.delete(id);
                            tr.removeClass('details');
                        }
                    }
                    collapseChildNodes([_tr]);

                } else {
                    displayed.add(_id);
                    _tr.addClass('details');
                }
                draw(dt, true);
            });

            /*$(options.selector).off().on('click', '.js-add-asset,.js-add-asset-group,.js-toggle-status', function() {
                if($(this).hasClass('js-add-asset')) {
                    $.showModal({
                        url: $.getURLWithRequestParams('/pim/assetCollections/{collectionId}/assets', {assetGroupId: $(this).data('external-id')}),
                        name:'asset',
                        title:'Create Asset',
                        buttons: [
                            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable1('assetsHierarchy');$.closeModal();});}},
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                } else if($(this).hasClass('js-add-asset-group')) {
                    $.showModal({
                        url: $.getURLWithRequestParams('/pim/assetCollections/{collectionId}/assets', {assetGroupId: $(this).data('external-id'), assetGroup: true}),
                        name:'assetGroup',
                        title:'Create Asset Group',
                        buttons: [
                            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable1('assetsHierarchy');$.closeModal();});}},
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                } else if($(this).hasClass('js-toggle-status')) {
                    $.toggleStatus(
                        $.getURL(options.url2 + '{externalId}/active/{active}', {
                            externalId: $(this).data('external-id'),
                            active: $(this).data('active'),
                            discontinued: $(this).data('discontinued')
                        }),
                        typeof options.names !== 'undefined' ? options.names[1] : 'entity',
                        $.refreshDataTable.bind(this, typeof options.names === 'undefined' ? options.name : options.names[0]), $(this).data('active'));
                }

            });*/
        },

        validateDataTableOptions: function(options) {
            $.each(options.columns, function(i, column){
                if(column.hasOwnProperty('visible') && column.visible === false) {

                } else if(column.hasOwnProperty('width')){
                    let colWidth = $.findMatch(column.width, /[\s]*(\d*)%/);
                    if(isNaN(colWidth)) {
                        options.columns[i].width = i == 0 ? '35%' : '20%';
                    }
                } else {
                    options.columns[i].width = i == 0 ? '35%' : '20%';
                }
            });
        },

        initDataTable: function(options) {
            $.bindDataTable(options, $(options.selector).DataTable( {
                processing: true,
                serverSide: true,
                pageLength: options.pageLength ? options.pageLength : 25,
                conditionalPaging: true,
                searching: typeof options.searching === 'undefined' ?  true : options.searching,
                ordering: !(typeof options.reordering !== 'undefined' && options.reordering),
                rowReorder: typeof options.reordering !== 'undefined' && options.reordering ? {snapX: 10} : false,
                dom:
                "<'row dt-header'<'col-sm-12 col-md-2'l><'col-sm-12 col-md-10 dt-toolbar'<'dt-buttons'><'dt-filter'f>>>" +
                "<'row dt-body'<'col-sm-12'tr>>" +
                "<'row dt-footer'<'col-sm-12 col-md-5'i><'col-sm-12 col-md-7'p>>",
                language: {
                    info: "_START_ to _END_ of _TOTAL_",
                    lengthMenu: "_MENU_",
                    search: ''
                },
                scrollY: 300,
                scrollCollapse: true,
                headerCallback: function headerCallback(thead, data, start, end, display) {
                    /*$(thead)
                        .find('th')
                        .first()
                        .html('Displaying ' + (end - start) + ' records');*/
                },
                initComplete : function() {
                    var input = $(options.selector + '_filter.dataTables_filter input').unbind()
                            .on('keyup', function(e) {
                                if($(this).val() !== '') {
                                    $(options.selector + '_filter.dataTables_filter .dt-clear').removeClass('js-hidden');
                                } else {
                                    $(options.selector + '_filter.dataTables_filter .dt-clear').addClass('js-hidden');
                                }
                                if(e.keyCode === 13) {
                                    $searchButton.click();
                                }
                            }),
                        self = this.api(),
                        $searchButton = $('<button type="button" class="btn btn btn-primary search-btn"><i class="icon-magnifier"></i></button>')

                            .click(function() {
                                self.search(input.val()).draw();
                                if(input.val() !== '') {
                                    $(options.selector + '_filter.dataTables_filter .dt-clear').removeClass('js-hidden');
                                } else {
                                    $(options.selector + '_filter.dataTables_filter .dt-clear').addClass('js-hidden');
                                }
                            }),
                        $clearButton = $('<button class="btn btn-default dt-clear js-hidden"><i class="fa fa-times-circle  text-danger"></i></button>')
                            .click(function() {
                                input.val('');
                                $searchButton.click();
                            });
                    $(options.selector + '_filter.dataTables_filter .search-btn').remove();
                    $(options.selector + '_filter.dataTables_filter').append($clearButton, $searchButton);

                    let toolbar = [];
                    $.each(options.toolbar, function(i, button) {
                        switch(button.name) {

                            case 'IMPORT':
                            {
                                toolbar.push($('<button class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" data-container="body" data-placement="top" title="" data-original-title="Import Data"><i class="fa fa-download"></i></button>')
                                    .click(function () {

                                    }));
                            }
                            break;
                            case 'EXPORT':
                                toolbar.push($('<button class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" data-placement="top" title="" data-original-title="Export Data"><i class="fa fa-upload"></i></button>')
                                    .click(function() {
                                        var testAdvSearch = {
                                            "conditions":[
                                                {
                                                    "conditions":[
                                                        {
                                                            "field": "productName",
                                                            "operator": "EQUAL",
                                                            "value": "#0 Bubble Mailers w/ Tear Strip",
                                                            "dataType": "STRING",
                                                            "regex": "N",
                                                            "ignoreCase": "N",
                                                            "logical":"AND"
                                                        },
                                                        {
                                                            "field": "externalId",
                                                            "operator": "EQUAL",
                                                            "value": "6_X_10_BUBBLE_MAILER_TS",
                                                            "dataType": "STRING",
                                                            "regex": "N",
                                                            "ignoreCase": "N"
                                                        }
                                                    ],
                                                    "logical":"OR"
                                                },
                                                {
                                                    "conditions":[
                                                        {
                                                            "field": "productName",
                                                            "operator": "EQUAL",
                                                            "value": "Gift Boxes (6 x 6 x 6)",
                                                            "dataType": "STRING",
                                                            "regex": "N",
                                                            "ignoreCase": "N",
                                                            "logical":"AND"
                                                        },
                                                        {
                                                            "field": "externalId",
                                                            "operator": "EQUAL",
                                                            "value": "6_BY_6_BY_6_GIFT_BOX",
                                                            "dataType": "STRING",
                                                            "regex": "N",
                                                            "ignoreCase": "N"
                                                        }
                                                    ]
                                                }
                                            ]
                                        };
                                        //window.location.href = button.actionUrl + '?filterCriteria=' + JSON.stringify(testAdvSearch);
                                        $.ajax({
                                            url: button.actionUrl,
                                            data: {
                                                filterCriteria:JSON.stringify(testAdvSearch)
                                            },
                                            method: 'POST',
                                            success: function (data) {

                                                console.log(data);
                                            }
                                        });
                                    }));
                                break;
                        }
                    });


                    let activeButton = $('<button class="btn btn-sm btn-success js-active-on" data-toggle="tooltip" data-placement="top" title="" data-original-title="Active Filter"><i class="icon-check"></i></button>')
                        .click(function() {
                            if($.getDataTableStatusOptions(options.selector) === '1000') {
                                toastr.warning('Sorry, this filter cannot be turned off, at lease one of the three status filters must be on', "Warning", {timeOut: 4000});
                            } else {
                                $(this).toggleClass(['btn-outline-secondary', 'btn-success', 'js-active-on']);
                                $searchButton.click();
                            }
                        });
                    toolbar.push(activeButton);
                    let inactiveButton = $('<button class="btn btn-sm btn-danger js-inactive-on" data-toggle="tooltip" data-placement="top" title="" data-original-title="Inactive Filter"><i class="icon-close"></i></button>')
                        .click(function() {
                            if($.getDataTableStatusOptions(options.selector) === '0100') {
                                toastr.warning('Sorry, this filter cannot be turned off, at lease one of the three status filters must be on', "Warning", {timeOut: 4000});
                            } else {
                                $(this).toggleClass(['btn-outline-secondary', 'btn-danger', 'js-inactive-on']);
                                $searchButton.click();
                            }
                        });
                    toolbar.push(inactiveButton);
                    let discontinuedButton = $('<button class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" data-placement="top" title="" data-original-title="Discontinued Filter"><i class="icon-ban"></i></button>')
                        .click(function() {
                            if($.getDataTableStatusOptions(options.selector) === '0010') {
                                toastr.warning('Sorry, this filter cannot be turned off, at lease one of the three status filters must be on', "Warning", {timeOut: 4000});
                            } else {
                                $(this).toggleClass(['btn-outline-secondary', 'btn-warning', 'js-discontinued-on']);
                                $searchButton.click();
                            }
                        });
                    toolbar.push(discontinuedButton);

                    let archivedButton = $('<button class="btn btn-sm btn-outline-secondary" data-toggle="tooltip" data-placement="top" title="" data-original-title="Archived Filter"><i class="fa fa-file-archive-o"></i></button>')
                        .click(function() {
                            if($.getDataTableStatusOptions(options.selector) === '0001') {
                                toastr.warning('Sorry, this filter cannot be turned off, at lease one of the three status filters must be on', "Warning", {timeOut: 4000});
                            } else {
                                $(this).toggleClass(['btn-outline-secondary', 'btn-warning', 'js-archived-on']);
                                $searchButton.click();
                            }
                        });
                    toolbar.push(archivedButton);

                    $(options.selector + '_wrapper').find('.dt-buttons').append(toolbar);
                    $(options.selector + '_wrapper').find('[data-toggle="tooltip"]').tooltip({ container: 'body',placement: 'top' , trigger : 'hover'});
                },
                ajax: {
                    data: function ( data ) {

                        //pricing attribute json
                        /*var testAdvSearch = {
                            "conditions": [
                                {
                                    "conditions": [
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "PLAIN",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "pricingAttributeName",
                                            "operator": "EQUAL",
                                            "value": "Plain",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ],
                                    "logical": "OR"
                                },
                                {
                                    "conditions": [
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "1_COLOR",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "pricingAttributeName",
                                            "operator": "EQUAL",
                                            "value": "1 Color",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ]
                                }
                            ]
                        };*/


                        //category json
                        /*var testAdvSearch = {
                            "conditions": [
                                {
                                    "field": "categoryName",
                                    "operator": "EQUAL",
                                    "value": "Accessories",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N",
                                    "logical":"AND"
                                },
                                {
                                    "field": "externalId",
                                    "operator": "EQUAL",
                                    "value": "ACCESSORIES",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N",
                                    "logical":"AND"
                                },
                                {
                                    "field": "description",
                                    "operator": "EQUAL",
                                    "value": "Accessories",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N"
                                }
                            ]
                        };*/

                        //Catalog json
                        /*var testAdvSearch = {
                            "conditions": [
                                {
                                    "conditions": [
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "DEFAULT_ENVELOPES",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "catalogName",
                                            "operator": "EQUAL",
                                            "value": "Envelopes Catalog",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ],
                                    "logical": "OR"
                                },
                                {
                                    "conditions": [
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "DEFAULT_FOLDERS",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "catalogName",
                                            "operator": "EQUAL",
                                            "value": "Folders Catalog",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ]
                                }
                            ]
                        };*/

                        //product and product variant test not success
                        /*var testAdvSearch = {
                            "conditions":[
                                {
                                    "conditions":[
                                        {
                                            "field": "productName",
                                            "operator": "EQUAL",
                                            "value": "#0 Bubble Mailers w/ Tear Strip",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "6_X_10_BUBBLE_MAILER_TS",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ],
                                    "logical":"OR"
                                },
                                {
                                    "conditions": [
                                        {
                                            "field": "productVariantName",
                                            "operator": "EQUAL",
                                            "value": "#0 Bubble Mailers w/ Tear Strip - Brown Kraft",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "BM6X10TSBK",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ]
                                }
                            ]
                        };*/

                        //2 product json
                        var testAdvSearch = {
                            "conditions":[
                                {
                                    "conditions":[
                                        {
                                            "field": "productName",
                                            "operator": "EQUAL",
                                            "value": "#0 Bubble Mailers w/ Tear Strip",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "6_X_10_BUBBLE_MAILER_TS",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ],
                                    "logical":"OR"
                                },
                                {
                                    "conditions":[
                                        {
                                            "field": "productName",
                                            "operator": "EQUAL",
                                            "value": "Gift Boxes (6 x 6 x 6)",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "6_BY_6_BY_6_GIFT_BOX",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ]
                                }
                            ]
                        };

                        /*var testAdvSearch = {
                            "conditions": [
                                {
                                    "conditions": [
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "FOLDERS",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "websiteName",
                                            "operator": "EQUAL",
                                            "value": "Folders.com",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ],
                                    "logical": "OR"
                                },
                                {
                                    "conditions": [
                                        {
                                            "field": "externalId",
                                            "operator": "EQUAL",
                                            "value": "ENVELOPES",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N",
                                            "logical":"AND"
                                        },
                                        {
                                            "field": "websiteName",
                                            "operator": "EQUAL",
                                            "value": "Envelopes.com",
                                            "dataType": "STRING",
                                            "regex": "N",
                                            "ignoreCase": "N"
                                        }
                                    ]
                                }
                        ]
                    };*/
                        /*var testAdvSearch = {
                            "conditions": [
                                {
                                    "field": "externalId",
                                    "operator": "EQUAL",
                                    "value": "ENVELOPES",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N",
                                    "logical":"AND"
                                },
                                {
                                    "field": "websiteName",
                                    "operator": "EQUAL",
                                    "value": "Envelopes.com",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N"
                                }
                            ]
                        };*/
                        /*var testAdvSearch = {
                            "conditions": [
                                {
                                    "field": "externalId",
                                    "operator": "EQUAL",
                                    "value": "FOLDERS",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N",
                                    "logical":"AND"
                                },
                                {
                                    "field": "websiteName",
                                    "operator": "EQUAL",
                                    "value": "Folders.com",
                                    "dataType": "STRING",
                                    "regex": "N",
                                    "ignoreCase": "N"
                                }
                            ]
                        };*/
                        data.statusOptions = $.getDataTableStatusOptions(options.selector);
                        // data.advancedSearch = JSON.stringify($('#jqs-adv-search-query').val());
                        data.advancedSearch = JSON.stringify(testAdvSearch);
                    },
                    url: options.url,
                    method: 'post',
                    dataSrc: function(json) {
                        $.each(json.data, function(index, value) {

                            if(typeof value.group !== 'undefined') {
                                var groups = value.group.split('|');
                                if(groups.length > 0) {
                                    value.group = groups[0];
                                }
                                for(var i = 1; i < groups.length; i ++) {
                                    value.group += '<i class="text-primary p-l-5 p-r-5 fa fa-caret-right"></i>' + groups[i];
                                }
                            }

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
                                            button.icon = 'icon-close';
                                            button.title = 'Disable';
                                            button.style = 'danger';
                                        }
                                        break;
                                }
                                if(typeof button.check === 'function') {
                                    if(button.check(value)) {
                                        value.actions += '<button type="button" class="btn btn-sm btn-' + button.style + ' js-' + button.name + '" title="' + button.title + '"><i class="' + button.icon + '"></i></button> ';
                                    }
                                } else {
                                    value.actions += '<button type="button" class="btn btn-sm btn-' + button.style + ' js-' + button.name + '" title="' + button.title + '"><i class="' + button.icon + '"></i></button> ';
                                }
                            });
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

            /*$.each(options.columns, function(index, column){
                if(typeof column.click === 'function') {
                    $(options.selector).on('click', '.' + column.selector, function () {
                        let row = $.getDataTable(dataTableName).row($(this).closest('tr'));
                        column.click(row.data());
                    });
                }
            });*/

            $.getDataTable(dataTableName).on( 'row-reorder', function ( e, diff, edit ) {
                const oTable = $.getDataTable(dataTableName);
                let src = edit.triggerRow.data();
                let direction = diff.length > 1 && oTable.row( diff[diff.length - 1].node ).data().externalId === src.externalId ? 'DOWN' : 'UP';
                let dest = {};
                if(diff.length > 1) {
                    dest = oTable.row( diff[direction === 'UP' ? 1 : diff.length - 2].node ).data();
                    $.ajax({
                        url: options.url,
                        data: {sourceId : src.externalId, destinationId: dest.externalId},
                        method: 'PUT',
                        success: function (data) {
                            oTable.columns.adjust().draw();
                            if(options.reorderCallback) {
                                options.reorderCallback();
                            }
                            toastr.success('Sequencing updated successfully', "Sequencing", {timeOut: 3000});

                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            $.ajaxError(jqXHR, function(){
                                toastr.error('An error occurred while reordering, try refreshing the page', "Error", {timeOut: 3000});
                            });
                        }
                    });
                }
            });

            $.getDataTable(dataTableName).on( 'draw.dt', function () {

            });

            $(options.selector).on('click', '.js-scope-selector', function () {
                var url = $.getURL(options.actionUrl + '/{familyAttributeId}/scope/{scope}', {
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
                        $.refreshDataTable('familyAttributesScopes');
                    }
                });
            });

            $(options.selector).on('click', '.js-scopable', function () {
                var url = $.getURL(options.actionUrl + '/{familyAttributeId}/scopable/{scopable}', {
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
        },
        getDataTableStatusOptions: function(selector) {
            var statusOption = '';
            if($(selector + '_wrapper .dt-buttons').find('.btn').length > 0) {
                statusOption += $(selector + '_wrapper .dt-buttons').find('.js-active-on').length;
                statusOption += $(selector + '_wrapper .dt-buttons').find('.js-inactive-on').length;
                statusOption += $(selector + '_wrapper .dt-buttons').find('.js-discontinued-on').length;
                statusOption += $(selector + '_wrapper .dt-buttons').find('.js-archived-on').length;
            }
            return statusOption;

        },
        /**
         * Refreshes the dataTable data only. All current parameters will be preserved, including
         * current sorting, page size and page number,
         *
         * NOTE- This method can be used to reload both dataTable and treeDataTable
         *
         * @param names - Comma separated name of the dataTables
         */
        refreshDataTable: function(names) {
            let _names = names.split(',');
            $.each(_names, function(i, name){
                name = name.trim();
                //If this is a treeDataTable, reload the treeDataTable, otherwise refresh the data
                if(!$.reloadTreeDataTable(name)) {
                    let dt = $.getDataTable(name);
                    if(dt) {
                        dt.ajax.reload(null, false);
                    }
                }
            });

        },

        /**
         * Reloads the dataTable. Will reload the data, reset sorting, page size and page number
         * to the initial configuration default
         *
         * NOTE- This method can be used to reload both dataTable and treeDataTable
         *
         * @param names - Comma separated name of the dataTables
         */
        reloadDataTable: function(names) {
            let _names = names.split(',');
            $.each(_names, function(i, name){
                name = name.trim();
                //If this is a treeDataTable, reload the treeDataTable, otherwise reload the dataTable
                if(!$.reloadTreeDataTable(name)) {
                    $.destroyDataTable(name);
                    $.initDataTable($.getDataTableOptions(name));
                }
            });
        },

        /**
         * Reinitialize the dataTable with new configuration parameters.
         * @param options - The new configuration options
         */
        reInitDataTable: function(options) {
            $.destroyDataTable(name, true);
            $.initDataTable(options);
        },

        /**
         * Reloads the treeDataTable. Will reload the data, reset sorting, page size and page number
         * to the initial configuration default, if present
         *
         * NOTE - Currently refreshing only the data of a treeDataTable is not supported.
         *
         * @param name - Name of the treeDataTable
         * @returns {boolean}
         */
        reloadTreeDataTable: function(name) {
            var options = $.getDataTableOptions(name) || {};
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
            const name = typeof options.names === 'undefined' ? options.name : options.names[0];
            $.setPageAttribute(name + '_datatable', dataTable);
            $.setPageAttribute(name + '_datatable_options', options);
            return dataTable;
        },
        unbindDataTable: function(name, hard) {
            const dt = $.getDataTable(name);
            if(dt && $.removePageAttribute(name + '_datatable')) {
                dt.destroy();
                if(hard) {
                    $.removePageAttribute(name + '_datatable_options');
                }
            }
        },
        getDataTable: function(name) {
            return $.getPageAttribute(name + '_datatable');
        },
        destroyDataTable: function(name, hard) {
            $.unbindDataTable(name, hard);
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
        initDatePicker: function() {
            var today = new Date(new Date().getFullYear(), new Date().getMonth(), new Date().getDate());
            $('.dateUI').each(function(){
                const self = $(this);
                let endDateEl, startDateEl;
                if(self.hasClass('js-start')) {
                    endDateEl = self.closest('.js-dateRange').find('.js-end');
                } else if(self.hasClass('js-end')) {
                    startDateEl = self.closest('.js-dateRange').find('.js-start');
                }
                if(endDateEl) {
                    self.datepicker({
                        uiLibrary: 'bootstrap4',
                        iconsLibrary: 'fontawesome',
                        format: 'yyyy-mm-dd',
                        minDate: today,
                        maxDate: function () {
                            return endDateEl.val();
                        }
                    });
                } else if(startDateEl) {
                    self.datepicker({
                        uiLibrary: 'bootstrap4',
                        iconsLibrary: 'fontawesome',
                        format: 'yyyy-mm-dd',
                        minDate: function () {
                            return startDateEl.val();
                        }
                    });
                } else {
                    self.datepicker({
                        uiLibrary: 'bootstrap4',
                        iconsLibrary: 'fontawesome',
                        format: 'yyyy-mm-dd',
                    });
                }
            });
        },
        lockInput: function(parent) {
            $('input[disabled="disabled"],select[disabled="disabled"],textarea[disabled="disabled"]', parent || 'body').each(function () {
                let msg = '';
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
            $('.fancy-checkbox.js-disabled').each(function(){
                let msg = '';
                if($(this).hasClass('js-parent-level')) {
                    msg = 'This property can only be modified at the product level';
                    $(this).after($('<div class="js-level-locked js-checkbox" title="' + msg + '"><i class="icon-arrow-up"></div>').on('click', function() {
                        toastr.info(msg, 'Readonly Property');
                    }));
                } else if($(this).hasClass('js-variant-axis')) {
                    msg = 'This is the variant axis and is not editable';
                    $(this).after($('<div class="js-axis-locked js-checkbox" title="' + msg + '"><i class="icon-target"></div>').on('click', function(){
                        toastr.info(msg, 'Readonly Property');
                    }));
                } else {
                    msg = 'This property is not editable';
                    $(this).after($('<div class="js-locked js-checkbox" title="' + msg + '"><i class="icon-lock"></div>').on('click', function(){
                        toastr.info(msg, 'Readonly Property');
                    }));
                }
                $(this).on('click', function(e){
                    e.preventDefault();
                    e.stopPropagation();
                    toastr.info(msg, 'Readonly Property');
                    return false;
                });
            });
        },
        autoExternalId: function() {
            $('body').on('keyup', '.js-name', function() {
                let nameEl = $(this).find('input');
                let externalIdEl = $(this).parent().find('.js-external-id input');
                if(externalIdEl.val() === '') {
                    externalIdEl.attr('data-sync', 'true');
                }
                if(externalIdEl && externalIdEl.data('sync')) {
                    externalIdEl.val(nameEl.val().replace(/[^\d\w\s]/g, '').replace(/(\s|_)+/g, '_'));
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

        removePageAttribute: function(key) {
            return delete $.getPageAttributes()[key];
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
        findMatch: function(string, regex) {
            if(match = regex.exec(string)) {
                let result = match[1];
                return typeof match[1] !== undefined ? match[1].trim() : '';
            }
            return '';
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
                        $('[data-toggle="tooltip"]').tooltip({trigger : 'hover'});
                        $.loadEvent();
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
                            } else if(data.refreshUrl) {
                                const refreshUrl = $.getPageAttribute('urlRoot') + data.refreshUrl;
                                try {
                                    window.history.replaceState({}, $(document).find("title").text(), refreshUrl);
                                } catch (e) {
                                    window.location.href = refreshUrl;
                                }
                                $.setPageAttribute('pageUrl', refreshUrl);
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

        archiveStatus: function(url, entityName, callback, isArchive) {
            $.confirmedAJAXRequest({
                url: url,
                method: 'PUT',
                text: 'This will ' + (isArchive === 'Y' ? 'unarchive' : 'archive') + ' the ' + entityName + "!",
                confirmButtonText: 'Yes, ' + (isArchive === 'Y' ? 'unarchive' : 'archive') +' it!',
                confirmButtonColor: isArchive === 'Y' ? '#dc3545' : '#28a745',
                successTitle: isArchive === 'Y' ? 'Unarchived!' : 'Archived!',
                successText: 'The ' + entityName + ' has been ' + (isArchive === 'Y' ? 'unarchived.' : 'archived.')
            }, callback);
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

        showError: function(title, message) {
            swal.fire({
                title: title,
                html: message,
                animation:false,
                type: 'error'
            });
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
                            req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
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
                            req.send(options.data || '');
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
            $.loadEvent();
        },
        bindUIEvents: function() {
            $('input[type="checkbox"]#active').on('change', function() {
                if($(this).prop('checked')) {
                    $('input[type="checkbox"]#discontinued').prop('checked', false);
                    // $('#discontinuationRange').collapse('show');
                }
            });

            $('input[type="checkbox"]#discontinued').on('change', function() {
                if($(this).prop('checked')) {
                    $('input[type="checkbox"]#active').prop('checked', false);
                }
            });

            $('textarea.auto-resize').on('keydown', function(e){
                $.resizeTextArea($(this));
            });

            $('textarea.auto-resize').each(function() {
                $(this).height( $(this)[0].scrollHeight );
            });
        },
        loadEvent: function() {
            $.bindUIEvents();
            $.initDatePicker();
            $.lockInput();
            $.autoExternalId();
        }
    });
    var page = new Page();
    var loc = window.location;
    $.setPageAttribute("urlRoot", loc.protocol + "//" + loc.hostname + ":" + loc.port);
    $.setPageAttribute("pageUrl", loc.href.replace(/#$/, ''));
    toastr.options.preventDuplicates = true;
    toastr.options.positionClass = 'toast-bottom-right';
})();
