<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">

            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-website" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Website</span></button>
                            <button id="js-sync-updatedWebsites" type="button" class="btn btn-primary"><i class="fa fa-plus"></i> <span class="p-l-5">Sync Websites</span></button>
                            <button id="js-advanced-search" type="button" class="btn btn-primary"><i class="fa fa-gears"></i> <span class="p-l-5">Advanced Search</span></button>
                            <button id="ui-advanced-search" type="button" class="btn btn-primary"><i class="fa fa-gears"></i> <span class="p-l-5">Search</span></button>
                        </div>
                    </div>
                </div>
                <div class="table-responsive scrollable-dt">
                    <table id="paginatedWebsitesTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
                        <thead class="thead-dark">

                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<div class="overlay-holder">
    <div class="overlay"></div>
    <div class="overlay-sheet">
        <div class="sheet-header">
            <h4>Advanced Search</h4>
            <div class="sheet-btns">
                <button class="btn btn-primary">SEARCH</button>
                <button class="btn btn-danger overlay-close">CLOSE</button>
            </div>
        </div>
        <div class="sheet-body">
            <div class="sidebar-scroll">
                <div class="row clearfix">
                    <div class="col-lg-12 col-md-12">
                        <div class="row">
                            <div class="col">
                                <div id="Criteria-UI"></div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    $( document).ready(function() {
        $('#Criteria-UI').CriteriaBuilder();
        $.initEntitiesGrid({
            selector: '#paginatedWebsitesTable',
            names: ['websites','website'],
            pageUrl: '/pim/websites/',
            dataUrl: '/pim/websites/data',
            columns: [
                { data: 'websiteName', name : 'websiteName' , title : 'Website Name', render: function ( data, type, row, meta ) {return '<h6>' + data + '</h6><small>' + row.url + '<small>';}},
                { data: 'externalId', name : 'externalId', title : 'Website ID' }
            ],
            buttons: ['DETAILS']
        });
        $.addModal({
            selector: '#js-create-website',
            url: $.getURL('/pim/websites/create'),
            name:'create-website',
            title:'Create Website',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('websites');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
        $.addModal({
            selector: '#js-advanced-search',
            url: $.getURL('/pim/websites/search'),
            name:'advanced-search',
            title:'Advanced Search',
            buttons: [
                {text: 'SEARCH', style: 'primary', close: false, click: function(){$.getDataTable('websites').search('').draw();$.closeModal();}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });

        $('#js-sync-updatedWebsites').on("click", function () {
            $.syncUpdatedInstance(
                $.getURL("/pim/websites/syncUpdatedWebsites"), "websites");
        });

        $('button#ui-advanced-search').click(function(){
            $('body').addClass('overlay-open');
        });
        $('button.overlay-close').click(function(){
            $('body').removeClass('overlay-open');
        });


    });

</script>