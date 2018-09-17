<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">

                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <a href="/pim/catalogs/create">
                                <button type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Catalog</span></button>
                            </a>
                        </div>
                    </div>
                </div>
                <div class="table-responsive">
                    <table id="paginatedCatalogsTable" class="table table-hover dataTable table-custom m-b-0">
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
            selector: '#paginatedCatalogsTable',
            name: 'catalogs',
            type: 'TYPE_1',
            url: '/pim/catalogs/',
            columns: [
                { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
                { data: 'externalId', name : 'externalId', title : 'Catalog ID' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();
    });
</script>