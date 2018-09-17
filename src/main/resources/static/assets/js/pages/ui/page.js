(function(){
    function Page() {
        var attributes = {};

        this.setAttributes = function(data) {
            $.extend(true, attributes, data);
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
        initDataTable: function(options) {
            $.bindDataTable(options, $(options.selector).DataTable( {
                processing: true,
                serverSide: true,
                pageLength: 5,
                conditionalPaging: true,
                searching: false,
                ajax: {
                    url: options.url + (options.type !== 'TYPE_1' ? '' : 'list'),
                    data: function ( data ) {
                        //process data before sent to server.
                    },
                    dataSrc: function(json) {
                        $.each(json.data, function(index, value) {

                            if(options.type === 'TYPE_1') {
                                value.actions = '<a href="' + options.url + value.externalId + '" class="btn btn-sm btn-outline-success" title="Details"><i class="icon-eye"></i></a> ' +
                                    '<a href="javascript:void(0);" class="btn btn-sm btn-outline-primary" title="clone"><i class="icon-docs"></i></a> ' +
                                    '<a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Disable/Enable" data-type="confirm"><i class="icon-ban"></i></a>';
                            } else if(options.type === 'TYPE_2') {
                                value.actions = '<a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Enable/Disable" data-type="confirm"><i class="icon-ban"></i></a> ' +
                                    '<a href="javascript:void(0);" class="btn btn-sm btn-outline-danger js-sweetalert" title="Disable" data-type="confirm"><i class="icon-trash"></i></a>';
                            } else if(options.type === 'TYPE_3') {
                                value.actions = '<button type="button" class="btn btn-success" title="Add"><span class="sr-only">Add</span> <i class="fa fa-save"></i></button>';
                            }

                            if(options.type === 'TYPE_1' || options.type === 'TYPE_2') {
                                if (value.active === 'Y') {
                                    value.active = '<span class="badge badge-success">Active</span>';
                                } else {
                                    value.active = '<span class="badge badge-danger">Inactive</span>';
                                }
                            }

                        });
                        return json.data;
                    }
                },
                columns: options.columns
            }));
        },
        refreshDataTable: function(name) {
            $.getDataTable(name).ajax.reload();
        },
        reloadDataTable: function(name) {
            $.getDataTable(name).destroy();
            $.initDataTable($.getDataTableOptions(name));
        },
        bindDataTable: function(options, dataTable) {
            $.setPageAttribute(options.name + '_datatable', dataTable);
            $.setPageAttribute(options.name + '_datatable_options', options);
        },
        getDataTable: function(name) {
            return $.getPageAttribute(name + '_datatable');
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
        setPageAttribute: function(key, value) {
            var attribute = {};
            attribute[key] = value;
            $.setPageAttributes(attribute);
        },
        getPageAttribute: function(key) {
            return $.getPageAttributes()[key];
        },
        getURL: function(uri, params) {
            // var attr = {'id' : 'manu', 'id2': 'prasad'};
            // var input = '/pim/websites/{id}/availableCatalogs/{id}';
            var url = uri;
            var match;
            var regex = /(\{(\S+?)\})/g;
            while(match = regex.exec(uri)) {
                if(params && match[2] in params) {
                    url = url.replace(match[1], params[match[2]]);
                } else if(match[2] in $.getPageAttributes()) {
                    url = url.replace(match[1], $.getPageAttribute(match[2]));
                }
            }
            return $.getPageAttribute("urlRoot") + url;
        }
    });
    var page = new Page();
    var loc = window.location;
    $.setPageAttribute("urlRoot", loc.protocol + "//" + loc.hostname + ":" + loc.port);
})();