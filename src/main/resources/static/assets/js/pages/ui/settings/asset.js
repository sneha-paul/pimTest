$(document).ready(function () {
    $('#my-dropzone').dropzone({
        params: {
            assetGroupId : $.getPageAttribute('assetGroupId')
        }
    });
});