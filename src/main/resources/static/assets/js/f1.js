var dataSet = [{"DT_RowId": "1","level": 0,"key": "1","parent": 0,"name": "Adam","isParent": true},
    {"DT_RowId": "2","level": 1,"key": "2","parent": 1,"name": "Nelenil Adam","isParent": false},
    {"DT_RowId": "3","level": 1,"key": "3","parent": 1,"name": "Skakal Adam","isParent": true},
    {"DT_RowId": "4","level": 2,"key": "4","parent": 3,"name": "Skakal *st* Adam","isParent": false},
    {"DT_RowId": "5","level": 2,"key": "5","parent": 3,"name": "Skakal *ml* Adam","isParent": false},
    {"DT_RowId": "6","level": 0,"key": "6","parent": 0,"name": "Ivan","isParent": true},
    {"DT_RowId": "7","level": 1,"key": "7","parent": 6,"name": "Nelenil Ivan","isParent": false},
    {"DT_RowId": "8","level": 0,"key": "8","parent": 0,"name": "Karol","isParent": true},
    {"DT_RowId": "9","level": 1,"key": "9","parent": 8,"name": "Hufnagel Karol","isParent": false},
    {"DT_RowId": "10","level": 1,"key": "10","parent": 8,"name": "Sipeky Karol","isParent": false}];
$(document).ready(function () {

    var dt = $('#example2').DataTable( {
        data: dataSet,
        ordering: false,
        info: false,
        searching: true,
        "paging":   false,
        "createdRow": function (row, data, index) {
            $(row).addClass('disable-select parent-' + data.parent);

            if(data.isParent) {
                $(row).addClass('parent-node');
            }
        },
        "columns": [
            {
            "class": "details-control",
            "orderable": false,
            "data": null,
            "visible": false,
            "title": 'Select',
            "defaultContent": "",
            "width": 50,
            "createdCell": function (td, cellData, rowData) {
                if (rowData.level > 0) {
                    td.className = td.className + ' level-' + rowData.level;
                }
            },
        }, {
            "data": "level",
            "title": 'Level1',
            "visible": false
        }, {
            "data": "key",
            "visible": false
        }, {
            "data": "parent",
            "visible": false
        }, {
            "data": "name",
            "title": 'Name',
            render: function ( data, type, row, meta ) {
                var level = row.level;
                return '<div style="padding-left:' + (level * 25) + 'px"><div class="float-left"><span class="collapsed-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-right p-r-5 js-ctrl"  style="cursor: pointer"></i><i class="fa fa-folder"></i></span><span class="expanded-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-down p-r-5 js-ctrl"  style="cursor: pointer"></i><i class="fa fa-folder-open"></i></span></div><div class="float-left p-l-10"><h6>' + data + '</h6></div></div>';
            }
        }]
    } );

    dt.columns([3]).search('^(0)$', true, false).draw();
    var displayed = new Set([]);
    $('#example2 tbody').on('click', 'tr td:first-child .js-ctrl', function () {
        var _tr = $(this).closest('tr');
        var _row = dt.row(_tr);
        var _key = _row.data().key;
        if (displayed.has(_key)) {
            function doAll(trs) {
                for(var i = 0; i < trs.length; i ++) {
                    var tr = $(trs[i]);
                    var row = dt.row(tr);
                    var key = row.data().key;

                    var childTrs = tr.parent().find('.parent-' + key);
                    if(childTrs.length > 0) {
                        doAll(childTrs);
                    }
                    displayed.delete(key);
                    tr.removeClass('details');
                }
            }
            doAll([_tr]);

        } else {
            displayed.add(_key);
            _tr.addClass('details');
        }
        var regex = "^(0";
        displayed.forEach(function (value) {
            regex = regex + "|" + value;
        });
        regex = regex + ")$";
        dt.columns([3]).search(regex, true, false).draw();
    });
});