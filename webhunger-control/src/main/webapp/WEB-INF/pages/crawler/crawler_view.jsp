<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>Crawler List</title>

    <%@include file="../common/head.jsp"%>

    <style>
        body {
            padding-top: 80px;
            padding-bottom: 30px;
        }
    </style>
</head>
<body>

<%@include file="../common/navbar.jsp"%>

<div class="container">

    <table id="crawler_table" class="table table-hover" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th width="4%">#</th>
            <th>Host</th>
            <th>IP</th>
            <th width="15%">State</th>
            <th width="20%">Operation</th>
        </tr>
        </thead>
    </table>
</div>


<%@include file="../common/footer.jsp"%>
<!-- 自定义 -->
<script type="text/javascript">

    $("#crawlerMenu").addClass("active");

    crawlerTable = $("#crawler_table").DataTable({
        "ajax": {
            "url": "${AppContext}crawler/list",
            "type": "POST"
        },
        "columns": [
            {"data": "hostName"},
            {
                "data": "hostName"
            },
            {
                "data": "ip"
            },
            {
                "data": "state",
                "render": function (data) {
                    if (data === 0) {
                        return "<span class='label label-info'>Ready</span>";
                    } else if (data === 1){
                        return "<span class='label label-primary'>Running</span>";
                    }
                }
            },
            {
                "data": "state",
                "render": function (data, type, row) {
                    var buttonStr = "";
                    if (data === 0) {
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' onclick='runCrawler(\""+row.ip+"\")'>Run</button> ";
                    } else {
                        buttonStr += "<button type='button' class='btn btn-warning btn-sm'>Halt</button> ";
                    }
                    buttonStr += "<button type='button' class='btn btn-default btn-sm'>Detail</button>";
                    return buttonStr;
                }
            }
        ],

        "lengthMenu": [15, 30, 50, 100],
        "pagingType": "full_numbers",
        "processing": true,
        "showRowNumber": true
    });

    crawlerTable.on( 'order.dt search.dt', function () {
        crawlerTable.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
            cell.innerHTML = i+1;
        });
    } ).draw();

    function updateCrawlerTable() {
        crawlerTable.ajax.reload();
    }
    
    function runCrawler(crawlerIP) {
        $.ajax({
            "url": "${AppContext}crawler/" + crawlerIP + "/run",
            "type": "POST",
            "success": function (data) {
                data = JSON.parse(data);
                if (data.res_code === 0) {
                    updateCrawlerTable();
                    $("#modalContent").html("You have added a crawler to run successfully~");
                    $("#commonModal").modal('show');
                }
            }
        });
    }

</script>
</body>
</html>