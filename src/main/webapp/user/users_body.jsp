<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="row p-b-25">
                <div class="col-lg-12 col-md-12">
                    <div class="pull-right">
                        <button id="js-create-users" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Users</span></button>
                    </div>
                </div>
            </div>
            <div class="body">
                <div class="table-responsive">
                    <table id="paginatedUsersTable" class="table table-hover dataTable table-custom" style="width: 100% !important;">
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
            selector: '#paginatedUsersTable',
            names: ['users','user'],
            pageUrl: '/pim/users/',
            dataUrl: '/pim/users/data',
            columns: [
                { data: 'userName', name : 'userName' , title : 'User Name'},
                { data: 'externalId', name : 'externalId', title : 'Email Id' }
            ],
        });
        $.addModal({
            selector: '#js-create-users',
            url: $.getURL('/pim/users/create/inside'),
            name:'create-users',
            title:'Create Users',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('users');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
    });

</script>