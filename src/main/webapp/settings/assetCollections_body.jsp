<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-assetCollection" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Collection</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedAssetCollectionsTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
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
            selector: '#paginatedAssetCollectionsTable',
            names: ['assetCollections','assetCollection'],
            pageUrl: '/pim/assetCollections/',
            dataUrl: '/pim/assetCollections/data',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Collection ID' },
                { data: 'collectionName', name : 'collectionName' , title : 'Collection Name'}
            ]
        });

    });

    $.addModal({
        selector: '#js-create-assetCollection',
        url: $.getURL('/pim/assetCollections/create'),
        name:'create-assetCollection',
        title:'Create Asset Collection',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('assetCollections');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
</script>