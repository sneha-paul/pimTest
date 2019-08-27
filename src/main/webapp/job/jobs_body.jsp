<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="table-responsive scrollable-dt">
                    <table id="paginatedJobsTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
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
            selector: '#paginatedJobsTable',
            names: ['jobs', 'job'],
            pageUrl: '/pim/jobs/',
            dataUrl: '/pim/jobs/jobData',
            hideStatus:'true',
            columns: [
                { data: 'createdDateTime', name : 'createdDateTime' , title : 'Time'},
                { data: 'jobName', name : 'jobName', title : 'Job', orderable: false},
                { data: 'userName', name : 'userName' , title : 'User', orderable: false},
                { data: 'status', name : 'status', title : 'Status', orderable: false, render: function(data, type, row, meta){if(row.status === 'Completed') {return  row.status + ' <a href="/pim/catalogs/export">Download File</a>'} else {return  row.status}}},
            ],
            buttons: ['DETAILS']
        });

    });
</script>