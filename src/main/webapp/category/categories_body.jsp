<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <div class="pull-right">
                            <button id="js-create-category" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Create Category</span></button>
                            <button id="js-sync-updatedCategories" type="button" class="btn btn-primary"><i class="fa fa-plus"></i> <span class="p-l-5">Sync Categories</span></button>
<%--                            <button id="js-advanced-search" type="button" class="btn btn-primary"><i class="fa fa-gears"></i> <span class="p-l-5">Advanced Search</span></button>--%>
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
                        <div id="categoryGridView" class="table-responsive js-grid-view scrollable-dt">
                            <table id="paginatedCategoriesTable" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
                                <thead class="thead-dark">
                                </thead>
                            </table>
                        </div>
                    </div>
                    <div class="tab-pane" id="categoryTree">
                        <div class="table-responsive no-filter scrollable-dt">
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
<script src="/assets/js/pages/ui/category/categories.js"></script>