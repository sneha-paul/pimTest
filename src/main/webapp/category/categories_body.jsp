<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                        <button id="js-create-category" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Category</span></button>
                    </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedCategoriesTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
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
            selector: '#paginatedCategoriesTable',
            names: ['categories', 'category'],
            type: 'TYPE_1',
            url: '/pim/categories/',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Category ID' },
                { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
    $.addModal({
        selector: '#js-create-category',
        url: $.getURL('/pim/categories/create'),
        name:'create-category',
        title:'Create Category',
        buttons: [
            {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('categories');$.closeModal();});}},
            {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
        ]
    });
</script>