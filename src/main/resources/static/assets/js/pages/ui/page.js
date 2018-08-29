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
        }
    });
    var page = new Page();
})();