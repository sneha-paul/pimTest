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
                                value.actions = '<button type="button" class="btn btn-success js-add" data-external-id="' + value.externalId + '" title="Add"><span class="sr-only">Add</span> <i class="fa fa-save"></i></button>';
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
        addModal: function(options) {
            var defaultOptions = {
                loadingHtml: '<span class="fa fa-circle-o-notch fa-spin fa-3x text-primary"></span><span class="h4">Loading</span>',
                size: eModal.size.lg,
                name:'Modal',
                title: 'Modal'
            };
            $(options.selector).off().on('click', function() {
                eModal.ajax($.extend(true, defaultOptions, options));
            });
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
        },
        bindFormSubmit: function(submitEl) {
            $(submitEl).on('click', function(e) {
                $.submitAction(this, e);
            });
        },
        submitAction: function(e, submitEl) {
            e.preventDefault();
            $.submitForm($(submitEl).closest('form'));
        },
        submitForm: function(formEl, successCallback) {
            $.clearFieldErrors(formEl);
            $.post({
                url: $(formEl).attr('action'),
                data: $(formEl).serialize(),
                success: function(data) {
                    if(data.success) {
                        toastr.success($(formEl).data('success-message')[0], $(formEl).data('success-message')[1]);
                        if(successCallback) {
                            successCallback();
                        }
                    } else {
                        $.renderFieldErrors(formEl, data.fieldErrors);
                        toastr.error($(formEl).data('error-message')[0], $(formEl).data('error-message')[1], {timeOut: 3000})
                    }
                },
                error: function (resp) {
                    toastr.error('An error occurred while saving the data', "Error", {timeOut: 3000});
                }
            });
        },
        renderFieldErrors: function(formEl, errors) {
            $.each(errors, function(name, error) {
                $(formEl.find('input[name="' + name + '"],textarea[name="' + name + '"]').addClass('parsley-error')).after($('<ul class="parsley-errors-list filled"><li>' + error['value0'] + '</li></ul>'));
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
    toastr.options.preventDuplicates = true;
    toastr.options.positionClass = 'toast-bottom-right';
})();