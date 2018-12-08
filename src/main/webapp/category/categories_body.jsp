<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-category" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Category</span></button>
                            <button type="button" class="btn btn-sm btn-secondary js-category-grid-view" title="Grid View"><i class="fa fa-list"></i></button>
                            <button type="button" class="btn btn-sm btn-outline-secondary js-category-tree-view"  title="Tree View"><i class="fa fa-sitemap"></i></button>
                        </div>
                    </div>
                </div>
                <ul class="nav nav-tabs-new2" style="position: absolute; top: -1000px">
                    <li class="nav-item"><a class="nav-link active show" data-toggle="tab" href="#categoryGrid">Grid</a></li>
                    <li class="nav-item"><a class="nav-link" data-toggle="tab" href="#categoryTree">Tree</a></li>
                </ul>
                <div class="tab-content">
                    <div class="tab-pane show active" id="categoryGrid">
                        <div id="categoryGridView" class="table-responsive js-grid-view">
                            <table id="paginatedCategoriesTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
                                <thead class="thead-dark">
                                </thead>
                            </table>
                        </div>
                    </div>
                    <div class="tab-pane" id="categoryTree">
                        <div class="table-responsive no-filter">
                            <table id="categoriesHierarchy" class="table table-hover dataTable treeDataTable table-custom m-b-0" style="width: 100% !important">
                                <thead class="thead-dark">
                                </thead>
                            </table>
                        </div>
                    </div>
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
                { data: 'categoryName', name : 'categoryName' , title : 'Category Name',
                    render: function(data, type, row, meta) {
                        return '<h6>' + data + '</h6>'
                    }
                },
                { data: 'externalId', name : 'externalId', title : 'Category ID' },
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });

        $.initTreeDataTable({
            selector: '#categoriesHierarchy',
            names: ['categoriesHierarchy', 'category'],
            url: '/pim/categories/hierarchy/',
            url2: '/pim/categories/',
            collapsed: false,
            collapsible: true,
            urlParams: {hash: '1'}
        });

        $('.js-category-grid-view').on('click', function(){
            $.refreshDataTable('categories');
            $('a.nav-link[href*="categoryGrid"]').trigger('click');
            $(this).removeClass('btn-outline-secondary').addClass('btn-secondary');
            $(this).parent().find('.js-category-tree-view').removeClass('btn-secondary').addClass('btn-outline-secondary');
        });

        $('.js-category-tree-view').on('click', function(e, view){
            if(1 !== view) {
                $.refreshDataTable('categoriesHierarchy');
            }
            $('a.nav-link[href*="categoryTree"]').trigger('click');
            $(this).removeClass('btn-outline-secondary').addClass('btn-secondary');
            $(this).parent().find('.js-category-grid-view').removeClass('btn-secondary').addClass('btn-outline-secondary');
        });

        if(window.location.hash === '#1') {
            $('.js-category-tree-view').trigger('click', 1);
        }
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