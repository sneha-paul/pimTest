<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">

                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <a href="/pim/productFamilies/create">
                                <button type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Product Family</span></button>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedTable" class="table table-hover dataTable table-custom m-b-0">
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
            selector: '#paginatedTable',
            name: 'productFamilies',
            type: 'TYPE_1',
            url: '/pim/productFamilies/',
            columns: [
                { data: 'externalId', name : 'externalId', title : 'Family ID' },
                { data: 'productFamilyName', name : 'productFamilyName' , title : 'Family Name'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>