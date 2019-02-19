<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-assetFamilies" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create AssetFamily</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive scrollable-dt">
                    <table id="paginatedAssetFamilyTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
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
            selector: '#paginatedAssetFamilyTable',
            names: ['AssetFamily','assetFamily'],
            pageUrl: '/pim/assetFamilies/',
            dataUrl: '/pim/assetFamilies/data',
            columns: [
                { data: 'assetFamilyName', name : 'assetFamilyName' , title : 'AssetFamily Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small>' + '<small>';}},
                { data: 'externalId', name : 'externalId', title : 'AssetFamily ID' }
            ],

        });
        $.addModal({
            selector: '#js-create-assetFamilies',
            url: $.getURL('/pim/assetFamilies/create'),
            name:'create-assetFamily',
            title:'Create AssetFamily',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('assetFamily');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
    });

</script>