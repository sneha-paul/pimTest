<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-pricingAttribute" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Pricing Attribute</span></button>
<%--                            <button id="js-advanced-search" type="button" class="btn btn-primary"><i class="fa fa-gears"></i> <span class="p-l-5">Advanced Search</span></button>--%>
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
        $.initEntitiesGrid({
            selector: '#paginatedPricingAttributesTable',
            names: ['pricingAttributes','pricingAttribute'],
            pageUrl: '/pim/pricingAttributes/',
            dataUrl: '/pim/pricingAttributes/data',
            archiveUrl:'/pim/pricingAttributes/{externalId}/pricingAttributes/archive/{archived}',
            columns: [
                { data: 'pricingAttributeName', name : 'pricingAttributeName' , title : 'Pricing Attribute Name'},
                { data: 'externalId', name : 'externalId', title : 'Pricing Attribute ID' }
            ]
        });

        $.addModal({
            selector: '#js-advanced-search',
            url: $.getURL('/pim/pricingAttributes/search'),
            name:'advanced-search',
            title:'Advanced Search',
            buttons: [
                {text: 'SEARCH', style: 'primary', close: false, click: function(){$.getDataTable('pricingAttributes').search('').draw();$.closeModal();}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
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