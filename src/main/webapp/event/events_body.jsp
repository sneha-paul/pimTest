<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="table-responsive">
                    <table id="paginatedEventsTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
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
            selector: '#paginatedEventsTable',
            names: ['events', 'event'],
            pageUrl: '/pim/events/',
            dataUrl: '/pim/events/data',
            columns: [
                { data: 'user', name : 'user' , title : 'User'},
                //{ data: 'details', name : 'details', title : 'Details'},
                { data: 'entity', name : 'entity', title : 'Entity'},
                { data: 'eventType', name : 'eventType' , title : 'Event Type'}

            ]
        });

    });
</script>