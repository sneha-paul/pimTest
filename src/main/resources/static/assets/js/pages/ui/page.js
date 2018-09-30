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

                            if('GROUP_4' === options.buttonGroup) {
                                value.actions = '';
                                if('Y' === value.selectable) {
                                    value.actions = '<button type="button" class="btn btn-outline-primary js-attribute-options" data-external-id="' + value.fullId + '" title="Show Attribute Options"><i class="fa fa-list"></i></button>';
                                }
                            } else if(options.type === 'TYPE_1') {
                                var icon = 'icon-ban', action = 'Disable', btnClass = 'btn-danger';
                                if('Y' !== value.active) {
                                    icon = 'icon-check';
                                    action = 'Enable';
                                    btnClass = 'btn-success';
                                }
                                value.actions = '<a href="' + options.url + value.externalId + '" class="btn btn-sm btn-info" title="Details"><i class="icon-eye"></i></a> ' +
                                    '<a href="javascript:void(0);" class="btn btn-sm btn-primary" title="Clone"><i class="icon-docs"></i></a> ' +
                                    '<button type="button" class="btn btn-sm ' + btnClass + ' js-toggle-status" data-external-id="' + value.externalId + '" data-active="' + value.active + '" title="' + action + '"><i class="' + icon + '"></i></button>';
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
            $(options.selector).on('click', '.js-toggle-status', function () {
                if('TYPE_1' === options.type) {
                    $.toggleStatus(
                        $.getURL(options.url + '{externalId}/active/{active}', {
                            externalId: $(this).data('external-id'),
                            active: $(this).data('active')
                        }),
                        typeof options.names !== 'undefined' ? options.names[1] : 'entity',
                        $.refreshDataTable.bind(this, typeof options.names === 'undefined' ? options.name : options.names[0]), $(this).data('active'));
                }
            });

            $(options.selector).on('click', '.js-attribute-options', function(){
                if('GROUP_4' === options.buttonGroup) {
                    $.showModal({
                        url: $.getURL('/pim/productFamilies/{productFamilyId}/PRODUCT/attributes/{attributeId}/options', {'attributeId': $(this).data('external-id')}),
                        name:'product-attribute-options',
                        title:'Product Attribute Options',
                        buttons: [
                            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
                        ]
                    });
                }
            });
        },
        refreshDataTable: function(name) {
            $.getDataTable(name).ajax.reload();
        },
        reloadDataTable: function(name) {
            $.getDataTable(name).destroy();
            $.initDataTable($.getDataTableOptions(name));
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
                title: 'Modal'
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
                            successCallback();
                        } else {
                            if(typeof data.path !== 'undefined' && data.path !== '') {
                                window.location.href = data.path;
                            }
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
                    swal({title: options.successTitle, text: options.successText, type: options.successType, timer: 3000/*, animation: false*/});
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
                $(formEl.find('input[name="' + name + '"],select[name="' + name + '"],textarea[name="' + name + '"]').addClass('parsley-error')).after($('<ul class="parsley-errors-list filled"><li>' + error['value0'] + '</li></ul>'));
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