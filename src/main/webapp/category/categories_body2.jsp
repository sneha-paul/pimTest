<style>
    .expanded-icons, tr.details .collapsed-icons {
        display: none;
    }
    .collapsed-icons, tr.details .expanded-icons {
        display: block;
    }
    .js-ctrl {
        visibility: hidden;
    }
    tr.parent-node .js-ctrl {
        visibility: visible;
    }
</style>
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
                <div class="table-responsive">
                    <table id="example2" class="table table-hover dataTable table-custom m-b-0" style="width: 100% !important">
                        <thead class="thead-dark">
                        </thead>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>
