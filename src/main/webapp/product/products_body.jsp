<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-product" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Product</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedProductsTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
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
            selector: '#paginatedProductsTable',
            names: ['products','product'],
            type: 'TYPE_1',
            url: '/pim/products/',
            columns: [
                { data: 'productName', name : 'productName' , title : 'Product Name' },
                { data: 'externalId', name : 'externalId', title : 'Product ID' },
                { data: 'productFamilyId', name : 'productFamilyId', title : 'Product Family' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();

        $.addModal({
            selector: '#js-create-product',
            url: $.getURL('/pim/products/create'),
            name:'create-product',
            title:'Create Product',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('products');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
    });
</script>
