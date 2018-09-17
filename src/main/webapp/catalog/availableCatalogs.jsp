

        <div class="popup-content" style="padding:20px">
            <div class="body">
                <div class="table-responsive">
                <table id="paginatedAvailableCatalogsTable" class="table table-hover dataTable table-custom" style="width:100%">
                    <thead class="thead-dark">
                    <tr>
                        <th>Catalog Name</th>
                        <th>Catalog ID</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                </table>
            </div>
            </div>
        </div>
        <img src="/assets/img/tiny.png" onload="$.initPopup('.js-add-catalog')"/>
        <script>
            $( document ).ready(function() {
                $.initDataTable({
                    selector: '#paginatedAvailableCatalogsTable',
                    name: 'availableCatalogs',
                    type: 'TYPE_3',
                    url: $.getURL('/pim/websites/{websiteId}/catalogs/available/list'),
                    columns: [
                        { data: 'catalogName', name : 'catalogName' , title : 'Catalog Name'},
                        { data: 'externalId', name : 'externalId', title : 'Catalog ID' },
                        { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
                    ]
                });
            });
            var state420 = '';
        </script>
