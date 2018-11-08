<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-pricingAttribute" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Pricing Attribute</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedPricingAttributesTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
                        <thead class="thead-dark">

                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $( document ).ready(function() {
        $.initDataTable({
            selector: '#paginatedPricingAttributesTable',
            names: ['pricingAttributes','pricingAttribute'],
            type: 'TYPE_1',
            url: '/pim/pricingAttributes/',
            columns: [
                { data: 'pricingName', name : 'pricingName' , title : 'Pricing Name'},
                { data: 'externalId', name : 'externalId', title : 'Pricing ID' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
    $.addModal({
        selector: '#js-create-pricingAttribute',
        url: $.getURL('/pim/pricingAttributes/create'),
        name:'create-pricingAttribute',
        title:'Create Pricing Attribute',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('pricingAttributes');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
</script>