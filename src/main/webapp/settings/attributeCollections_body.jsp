<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-attributeCollection" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Collection</span></button>
<%--                            <button id="js-advanced-search" type="button" class="btn btn-primary"><i class="fa fa-gears"></i> <span class="p-l-5">Advanced Search</span></button>--%>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedAttributeCollectionsTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
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
            selector: '#paginatedAttributeCollectionsTable',
            names: ['attributeCollections','attributeCollection'],
            pageUrl: '/pim/attributeCollections/',
            dataUrl: '/pim/attributeCollections/data',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Collection ID' },
                { data: 'collectionName', name : 'collectionName' , title : 'Collection Name'}
            ]
        });
        $.addModal({
            selector: '#js-advanced-search',
            url: $.getURL('/pim/attributeCollections/search'),
            name:'advanced-search',
            title:'Advanced Search',
            buttons: [
                {text: 'SEARCH', style: 'primary', close: false, click: function(){$.getDataTable('attributeCollections').search('').draw();$.closeModal();}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });

    });

    $.addModal({
        selector: '#js-create-attributeCollection',
        url: $.getURL('/pim/attributeCollections/create'),
        name:'create-attributeCollection',
        title:'Create Attribute Collection',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('attributeCollections');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });

</script>