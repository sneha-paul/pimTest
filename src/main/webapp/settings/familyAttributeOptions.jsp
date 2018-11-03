<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="popup-content" style="padding:20px">
    <div class="body">
        <div class="row p-b-25">
            <div class="col-lg-12 col-md-12">
                <div class="pull-right">
                    <button type="button" class="btn btn-success js-available-attribute-option"><i class="fa fa-plus"></i> <span class="p-l-5">Add Option</span></button>
                    <div class="input-group js-add-input-ui-group" style="display: none">
                        <form method="post" action="/pim/families/{familyId}/attributes/${attributeId}/options" data-method="PUT"
                              data-success-message='["Successfully added the attribute option", "Option Added"]'
                              data-error-message='["Correct the validation error and try again", "Invalid Data"]'>
                            <input type="text" name="value" class="form-control" style="width: 300px">
                            <div class="input-group-append">
                                <span class="input-group-text">
                                    <button type="button" class="btn btn-success" onclick="$.submitAction(event, this, function(){$.reloadDataTable('attributeOptions');$.resetAddUI();})"><i class="fa fa-plus"></i></button>
                                    <button type="button" class="btn btn-danger js-cancel-option" style="margin-left: 5px;"><i class="fa fa-times"></i></button>
                                </span>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div class="table-responsive">
            <table id="paginatedAttributeOptionsTable" class="table table-hover dataTable table-custom" style="width: 100%">
                <thead class="thead-dark">

                </thead>

            </table>
        </div>
    </div>
</div>
<img src="/assets/img/tiny.png" onload="$.setPageAttributes({'attributeId': '${attributeId}', 'attributeName': '${attributeName}'});$.initAHAH(this)"/>
<script src="/assets/js/pages/ui/settings/familyAttributeOptions.js"></script>