$(document).ready(function () {
    $('#my-dropzone').dropzone({
        params: {
            assetGroupId : $.getPageAttribute('assetGroupId')
        },
        init: function(){
            this.on('addedfile', function(file) {

            });
            this.on('success', function(file, resp) {
               $.refreshPage();
               var _this = this;
                setTimeout(function () {
                    _this.removeFile(file);
                }, 500);

            });
        }
    });
});