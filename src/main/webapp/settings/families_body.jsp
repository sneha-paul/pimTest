<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                                <button id="js-create-family" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Product Type</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive scrollable-dt">
                    <table id="paginatedFamiliesTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
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
            selector: '#paginatedFamiliesTable',
            names: ['families','family'],
            pageUrl: '/pim/families/',
            dataUrl: '/pim/families/data',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Product Type ID' },
                { data: 'familyName', name : 'familyName' , title : 'Product Type Name'}
            ]
        });

    });

    $.addModal({
        selector: '#js-create-family',
        url: $.getURL('/pim/families/create'),
        name:'create-family',
        title:'Create Product Type',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('families');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
</script>