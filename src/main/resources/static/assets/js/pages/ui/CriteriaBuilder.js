(function($){
    $.fn.CriteriaBuilder = function(options) {
        var settings = $.extend({

        }, options);
        this.empty();
        /*var shellEl = $('<div class="wrapper"/>')
                .append($('<div class="form-header"/>'))
                .append($('<div class="criteria-builder"/>'));*/
        var newCriteriaEl = $('<a href="javascript:void(0)" class="condition icon-code jqs-add-criteria">New Condition</a>');
        var newCriteriaGroupEl = $('<a href="javascript:void(0)" class="condition icon-code-group">New Condition Group</a>');
        var deleteCriteriaGroupEl = $('<a href="javascript:void(0)" class="condition icon-code-group">Delete Condition Group</a>');
        var deleteCriteriaEl = $('<div class="form-group col-md-1"/>').append($('<a href="javascript:void(0)" class="delete"><i class="fa fa-trash"></i></a>'));

        var previewEl = $('<code/>');

        var criteriaGroupEl = $('<div class="criteria-group"/>');
        var logicalDropdownEl = $('<div class="form-group logical col-md-2"/>')
            .append($('<select class="form-control jqs-logical-operator"/>')
                .append($('<option value="and">and</option>'))
                .append($('<option value="or">or</option>')));
        var operandEl = $('<div class="form-group col"/>').append($('<select class="form-control jqs-operand"/>'));
        var operatorEl = $('<div class="form-group condition col-md-3"/>').append($('<select class="form-control jqs-operator"/>'));

        var valueEl = $('<div class="form-group col"/>').append($('<input type="text" class="form-control js-value" />'));

        var criteriaEl = $('<div class="form-row"/>').append(logicalDropdownEl).append(operandEl).append(operatorEl).append(valueEl).append(deleteCriteriaEl);
        function createCriteriaGroupEl(depth) {
            var depthClass = getDepthClass(depth);
            var _criteriaGroupEl = criteriaGroupEl.clone().addClass(getDepthClass(depth));
            _criteriaGroupEl
                .append(createCriteriaEl())
                .append(newCriteriaEl.clone().on('click', function(){
                    createCriteriaEl().insertBefore($(this).closest('.criteria-group.' + depthClass).find('.jqs-add-criteria:last'))
                }))
                .append(depth < 4 ? newCriteriaGroupEl.clone().on('click', function(){
                    createCriteriaGroupEl(depth + 1).insertBefore($(this).closest('.criteria-group.' + depthClass).find('.jqs-add-criteria:last'))
                }) : '')
                .append(depth > 1 ? deleteCriteriaGroupEl.clone() : '');
            return _criteriaGroupEl;
        }

        function createCriteriaEl() {
            return criteriaEl.clone();
        }
        // Return the number of criteria inside the given criteriaGroup
        function getCriteriaCount(groupEl) {
            return groupEl.find('form-row').length;
        }

        function getDepthClass(depth) {
            switch(depth) {
                case 1:
                    return 'one';
                case 2:
                    return 'two';
                case 3:
                    return 'three';
                case 4:
                    return 'four';
                case 5:
                    return 'five';
                case 6:
                    return 'six';
                case 7:
                    return 'seven';
                case 8:
                    return 'eight';
                case 9:
                    return 'nine';
                case 10:
                    return 'ten';

            }
        }

        // shellEl.find('.form-header').append(previewEl);

        // shellEl.find('.criteria-builder').append(createCriteriaGroupEl(1));
        this.addClass('query-builder-container')
            .append($('<div class="form-header"/>')
                .append(previewEl))
            .append($('<div class="criteria-builder"/>')
                .append(createCriteriaGroupEl(1)));

        return this;
    };
})(jQuery);