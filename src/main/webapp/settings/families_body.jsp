<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                                <button id="js-create-family" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Family</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
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
        $.initDataTable({
            selector: '#paginatedFamiliesTable',
            names: ['families','family'],
            type: 'TYPE_1',
            url: '/pim/families/',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Family ID' },
                { data: 'familyName', name : 'familyName' , title : 'Family Name'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });

    });

    $.addModal({
        selector: '#js-create-family',
        url: $.getURL('/pim/families/create'),
        name:'create-family',
        title:'Create Family',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('families');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
</script>