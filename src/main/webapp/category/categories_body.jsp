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
                <div class="row m-b--50">
                    <div class="col-lg-12">
                        <div class="pull-left">
                            <h2>Categories</h2>
                        </div>
                        <div style="position: absolute; right: 0; margin-right: 15px;">
                            <button type="button" class="btn btn-sm btn-secondary js-category-grid-view" title="Grid View"><i class="fa fa-list"></i></button>
                            <button type="button" class="btn btn-sm btn-outline-secondary js-category-tree-view"  title="Tree View"><i class="fa fa-sitemap"></i></button>
                        </div>
                    </div>
                </div>

                <div class="card inner overflowhidden m-t-50">
                    <div class="body">
                        <div class="card overflowhidden">
                            <div class="body">
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
                                        <div id="categoryTreeView" class="js-tree-view" style="min-height: 800px"></div>
                                    </div>
                                </div>
                            </div>
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
                { data: 'externalId', name : 'externalId', title : 'Category ID' },
                { data: 'categoryName', name : 'categoryName' , title : 'Category Name'},
                { data: 'active', name : 'active' , title : 'Status', orderable: false},
                { data: 'actions', name : 'actions' , title : 'Actions', orderable: false}
            ]
        });
//        $('#paginatedTable').dataTable().fnSetFilteringEnterPress();

        $('.js-category-grid-view').on('click', function(){
            $('a.nav-link[href*="categoryGrid"]').trigger('click');
            $(this).removeClass('btn-outline-secondary').addClass('btn-secondary');
            $(this).parent().find('.js-category-tree-view').removeClass('btn-secondary').addClass('btn-outline-secondary');
        });

        $('.js-category-tree-view').on('click', function(){
            $('a.nav-link[href*="categoryTree"]').trigger('click');
            $(this).removeClass('btn-outline-secondary').addClass('btn-secondary');
            $(this).parent().find('.js-category-grid-view').removeClass('btn-secondary').addClass('btn-outline-secondary');
        });

        var defaultData = [
            {
                text: 'Parent 1',
                href: '#parent1',
                key: 'PARENT_1',
                tags: ['2'],
                icon: 'fa fa-caret-down',
                nodes: [
                    {
                        text: 'Child 1',
                        href: '#child1',
                        key: 'CHILD_1',
                        tags: ['2'],
                        icon: 'fa fa-caret-down',
                        nodes: [
                            {
                                text: 'Grandchild 1',
                                href: '#grandchild1',
                                key: 'GRAND_CHILD_1',
                                tags: ['0'],
                                icon: 'fa fa-folder'
                            },
                            {
                                text: 'Grandchild 2',
                                href: '#grandchild2',
                                key: 'GRAND_CHILD_2',
                                tags: ['0'],
                                icon: 'fa fa-folder'
                            }
                        ]
                    },
                    {
                        text: 'Child 2',
                        href: '#child2',
                        key: 'CHILD_2',
                        tags: ['0'],
                        icon: 'fa fa-folder'
                    }
                ]
            },
            {
                text: 'Parent 2',
                href: '#parent2',
                key: 'PARENT_2',
                tags: ['0'],
                icon: 'fa fa-folder'
            },
            {
                text: 'Parent 3',
                href: '#parent3',
                key: 'PARENT_3',
                tags: ['0'],
                icon: 'fa fa-folder'
            },
            {
                text: 'Parent 4',
                href: '#parent4',
                key: 'PARENT_4',
                tags: ['0'],
                icon: 'fa fa-folder'
            },
            {
                text: 'Parent 5',
                href: '#parent5',
                key: 'PARENT_5',
                tags: ['0'],
                icon: 'fa fa-folder'
            }
        ];

        $('#categoryTreeView').treeview({
            data: defaultData,
            levels: 99,
            showBorder: true,
            showTags: false,
            showKey: true,
            expandIcon: 'fa fa-folder',
            collapseIcon: 'fa fa-folder-open',
//        nodeIcon: 'fa fa-folder',
            onNodeCollapsed: function(event, node) {
//            $('.node-treeview1[data-nodeid="' + node.nodeId +'"]').find('.node-icon').removeClass('fa-folder fa-folder-open').addClass('fa-folder');
                node.icon = 'fa fa-caret-right';
            },
            onNodeExpanded: function (event, node) {
                node.icon = 'fa fa-caret-down';
//            $('.node-treeview1[data-nodeid="' + node.nodeId +'"]').find('.node-icon').removeClass('fa-folder fa-folder-open').addClass('fa-folder-open');
            }
        });
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