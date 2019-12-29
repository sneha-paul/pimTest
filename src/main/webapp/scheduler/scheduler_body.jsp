<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row clearfix">
    <div class="col-lg-12 col-md-12">
        <div class="card">
            <div class="body">
                <div class="row p-b-25">
                    <div class="col-lg-12 col-md-12">
                        <button id="js-create-simpleJob" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Simple Schedule</span></button>
                        <button id="js-create-cronJob" type="button" class="btn btn-success"><i class="fa fa-plus"></i> <span class="p-l-5">Cron Schedule</span></button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
    $( document).ready(function() {
        $.addModal({
            selector: '#js-create-simpleJob',
            url: $.getURL('/pim/scheduler/simpleJob/create'),
            name:'create-simpleJob',
            title:'Create SimpleJob',
            buttons: [
                {text: 'SAVE', id: 'scheduleTime', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
        $.addModal({
            selector: '#js-create-cronJob',
            url: $.getURL('/pim/scheduler/cronJob/create'),
            name:'create-cronJob',
            title:'Create CronJob',
            buttons: [
                {text: 'SAVE', style: 'primary', close: false, click: function(){$.submitForm($(this).closest('.modal-content').find('form'), function(){$.reloadDataTable('websites');$.closeModal();});}},
                {text: 'CLOSE', style: 'danger', close: true, click: function(){}}
            ]
        });
    });

</script>