<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-catalog" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Catalog</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive scrollable-dt">
                    <table id="paginatedCatalogsTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important;">
                        <thead class="thead-dark">

                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $( document).ready(function() {
        $.initEntitiesGrid({
            selector: '#paginatedCatalogsTable',
            names: ['catalogs','catalog'],
            pageUrl: '/pim/catalogs/',
            dataUrl: '/pim/catalogs/data',
            columns: [
                { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
                { data: 'externalId', name : 'externalId', title : 'Catalog ID' }
            ]
        });
        $.addModal({
            selector: '#js-create-catalog',
            url: $.getURL('/pim/catalogs/create'),
            name:'create-catalog',
            title:'Create Catalog',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('catalogs');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });


    });

</script>