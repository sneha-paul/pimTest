<%--
&lt;%&ndash;@elvariable id="productVariant" type="com.bigname.pim.api.domain.ProductVariant"&ndash;%&gt;
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-variant" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create ProductVariant</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedProductVariantsTable" class="table table-hover dataTable table-custom">
                        <thead class="thead-dark">

                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $.initPage({
        'productId' : '${productVariant.product.productId}'
    });
    $( document ).ready(function() {
        $.initDataTable({
            selector: '#paginatedProductVariantsTable',
            names: ['productVariants','productVariant'],
            type: 'TYPE_1',
            url: $.getURL('/pim/products/{productId}/variants/'),
            columns: [
                { data: 'productVariantName', name : 'productVariantName' , title : 'Variant Name' },
                { data: 'externalId', name : 'externalId', title : 'Variant ID' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}            ]
        });


    });
</script>
--%>
