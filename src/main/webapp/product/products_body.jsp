<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-product" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Parent Product</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive scrollable-dt">
                    <table id="paginatedProductsTable" class="table table-hover dataTable table-custom c_list" style="width: 100% !important;">
                        <thead class="thead-dark">

                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    var dt;
    $( document ).ready(function() {
        dt = $.initEntitiesGrid({
            selector: '#paginatedProductsTable',
            names: ['products','product'],
            pageUrl: '/pim/products/',
            toolbar: [{name: 'EXPORT', actionUrl: '/pim/products/export'}, {name: 'IMPORT'}],
            dataUrl: '/pim/products/data',
            toggleUrl:'/pim/products/{externalId}/products/active/{active}',
            archiveUrl:'/pim/products/{externalId}/products/archive/{archived}',
            columns: [
                {
                    data: 'productName', name : 'productName' , title : 'Parent Product Name', width: '35%',
                    render: function ( data, type, row, meta ) {
                        let imgUrl = row.imageName === 'noimage.png' ? '/assets/img/' + row.imageName : '/uploads/' + row.imageName;
                        return '<div class="grid-image-holder pull-left rounded"><img  src="' + imgUrl + '" data-toggle="' + data + '" data-placement="top" title="" alt="" class="grid-main-img rounded"></div><div class="pull-left"><h6>' + data + '</h6><small>' + row.externalId + '<small></div>'
                    }
                },
                { data: 'variantCount', name : 'variantCount', title : 'Child Products', width: '20%',
                    render: function ( data, type, row, meta ) {
                        let content = '<ul class="list-unstyled variant-info">';

                        let variantImages = row.variantImages.split('|');
                        const addlVariants = parseInt(data) - variantImages.length;
                        $.each(variantImages, function(i, imageName){
                            let imgUrl = imageName === 'noimage.png' ? '/assets/img/' + imageName : '/uploads/' + imageName;
                            content += '<li><div class="grid-tiny-image-holder"><img class="grid-tiny-image" src="' + imgUrl + '" alt=""/></div></li>';
                        });

                        content += '</ul>';
                        if(addlVariants > 0) {
                            content += '<span>+ ' + addlVariants + ' more</span>';
                        }
                        return content;
                    }
                },
                { data: 'productFamilyId', name : 'productFamilyId', title : 'Product Type', width: '15%' }
            ],
        });

        $.addModal({
            selector: '#js-create-product',
            url: $.getURL('/pim/products/create'),
            name:'create-product',
            title:'Create Parent Product',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('products');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
    });
</script>
