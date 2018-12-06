var dataSet = [
    {"level": 0,"key": "1","parent": "0","name": "Adam","isParent": true},
    {"level": 1,"key": "2","parent": "1","name": "Nelenil Adam","isParent": false},
    {"level": 1,"key": "3","parent": "1","name": "Skakal Adam","isParent": true},
    {"level": 2,"key": "4","parent": "3","name": "Skakal *st* Adam","isParent": false},
    {"level": 2,"key": "5","parent": "3","name": "Skakal *ml* Adam","isParent": false},
    {"level": 0,"key": "6","parent": "0","name": "Ivan","isParent": true},
    {"level": 1,"key": "7","parent": "6","name": "Nelenil Ivan","isParent": false},
    {"level": 0,"key": "8","parent": "0","name": "Karol","isParent": true},
    {"level": 1,"key": "9","parent": "8","name": "Hufnagel Karol","isParent": false},
    {"level": 1,"key": "10","parent": "8","name": "Sipeky Karol","isParent": false}
];
$(document).ready(function () {

    $.initTreeDataTable({
        selector: '#categoriesHierarchy',
        url: '/pim/categories/hierarchy',
        collapsed: false,
        collapsible: true
    });

    /*var displayed = new Set([]);
    var collapsed = false;
    var collapsible =true;
    var dt = $('#example2').DataTable( {
        data: dataSet,
        ordering: false,
        info: false,
        searching: true,
        paging:   false,
        createdRow: function (row, data, index) {
            $(row).addClass('disable-select parent-' + data.parent);
            if(data.isParent) {
                if(collapsible || collapsed) {
                    $(row).addClass('parent-node');
                }
                if(!collapsed) {
                    displayed.add(data.key);
                    $(row).addClass('details');
                }
            }
        },
        columns: [
            { data: 'level', visible: false },
            { data: 'key', visible: false },
            { data: 'parent', visible: false },
            { data: 'name', title: 'Name',
                render: function ( data, type, row, meta ) {
                    var level = row.level;
                    return '<div style="padding-left:' + (level * 25) + 'px"><div class="float-left"><span class="collapsed-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-right p-r-10 js-ctrl"  style="cursor: pointer"></i><i class=" text-primary fa fa-folder"></i></span><span class="expanded-icons" style="position: relative; top: -5px; font-size: 20px"><i class="fa fa-caret-down p-r-5 js-ctrl"  style="cursor: pointer"></i><i class="text-primary fa fa-folder-open"></i></span></div><div class="float-left p-l-10"><h6>' + data + '</h6></div></div>';
                }
            }
        ]
    } );

    if(collapsed) {
        dt.columns([2]).search('^(0)$', true, false).draw();
    }


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
        dt.columns([2]).search(regex, true, false).draw();
    });*/
});