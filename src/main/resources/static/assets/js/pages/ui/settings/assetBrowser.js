$(function(){
    const browserEl = $('#js-file-browser');
    const folderElTemplate = browserEl.find('.js-folders .js-template .js-folder');
    const foldersContainer = browserEl.find('.js-folders .js-container');
    const foldersTitle = browserEl.find('.js-folders .js-title');

    const fileElTemplate = browserEl.find('.js-files .js-template .js-file');
    const filesContainer = browserEl.find('.js-files .js-container');
    const filesTitle = browserEl.find('.js-files .js-title');

    const crumbElTemplate = $('<li class="breadcrumb-item"><span class="js-crumb"><i class="text-primary p-r-10 fa fa-folder"></i></span></li>');
    $.extend({
        bindFolderClickEvent : function() {
            browserEl.on('click', '.js-folder,.js-crumb',  function(){
                $.loadItems($(this).attr('id'));
            });
        },
        clearItems: function() {
            foldersContainer.text('');
            foldersTitle.addClass('js-hidden');

            filesContainer.text('');
            filesTitle.addClass('js-hidden');

            $('.js-parent-chain').text('');
        },
        renderFolders: function(folders) {
            $.each(folders, function(i, folder) {
                var folderEl = $(folderElTemplate).clone();
                $(folderEl).attr('id', folder.id);
                $(folderEl).find('.name').html(folder.name);
                foldersContainer.append(folderEl);
            });
            if(folders.length > 0) {
                foldersTitle.removeClass('js-hidden');
            }
        },
        renderFiles: function(files) {
            $.each(files, function(i, file) {
                var fileEl = $(fileElTemplate).clone();
                // $(fileEl).attr('id', file.id);
                $(fileEl).find('.image img').attr('src', '/uploads/' + file.internalFileName).attr('alt', file.name).attr('title', file.name);
                $(fileEl).find('.name').html(file.name);
                filesContainer.append(fileEl);
            });
            if(files.length > 0) {
                filesTitle.removeClass('js-hidden');
            }
        },
        renderCrumbs: function(crumbs) {
            $.each(crumbs, function(i, crumb) {
                var crumbEl = $(crumbElTemplate).clone();
                $(crumbEl).find('.js-crumb').attr('id', crumb.parentId).append($('<span>' + crumb.name + '</span>'));

                $('.js-parent-chain').append(crumbEl);
            });
        },
        loadItems: function(directoryId) {
            $.clearItems();
            $.ajax({
                url: '/pim/assetCollections/browser/data',
                data: {directoryId: directoryId},
                method: 'GET',
                async: true,
                success: function (data) {
                    if(data.success === true) {
                        $.renderFolders(data.folders);
                        $.renderFiles(data.files);
                        $.renderCrumbs(data.parentChain);
                    } else {
                        toastr.error('An error occurred while getting items, try refreshing the page', "Error", {timeOut: 3000});
                    }
                },
                error: function (jqXHR) {
                    $.ajaxError(jqXHR, function(){
                        toastr.error('An error occurred while getting items, try refreshing the page', "Error", {timeOut: 3000});
                    });
                }
            });
        }
    });
    $.bindFolderClickEvent();
});