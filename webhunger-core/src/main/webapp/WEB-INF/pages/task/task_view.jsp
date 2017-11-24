<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>Task List</title>

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

    <table id="task_table" class="table table-hover" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th width="20%">Task Name</th>
            <th>Task Description</th>
            <th width="15%">Task Status</th>
            <th width="15%">Task Owner</th>
            <th width="20%">Task Time</th>
        </tr>
        </thead>
    </table>
</div>


<%@include file="../common/footer.jsp"%>
<script src="${AppContext}/framework/moment/moment.min.js"></script>
<script src="${AppContext}js/setting.js"></script>
<!-- 自定义 -->
<script type="text/javascript">

    taskTable = $("#task_table").DataTable({
        "ajax": {
            "url": "${AppContext}task/list",
            "type": "POST"
        },
        "columns": [
            {
                "data": "taskName",
                "render": function (data, type, row) {
                    return "<a href='${AppContext}task/"+row.taskId+"/host/list'>"+data+"</a>";
                }
            },
            {
                "data": "description",
                "defaultContent": "",
                "render": function (data, type, row) {
                    <%--return "<a href='${AppContext}task/"+row.task_id+"/host/list'>"+data+"</a>";--%>
                    return data;
                }
            },
            {
                "data": "state",
                "render": function (data) {
                    if (data === 1) {
                        return "<span class='label label-primary'>Active</span>";
                    } else if (data === 2){
                        return "<span class='label label-default'>Completed</span>";
                    }
                }
            },
            {
                "data": "author",
                "defaultContent": ""
            },
            {
                "data": "startTime",
                "defaultContent": "-",
                "render": function (data, type, row) {
                    return moment(data).format('YYYY-MM-DD') + " ~ " + moment(row.finishTime).format('YYYY-MM-DD');
                }
            }
        ],

        "lengthMenu": [15, 30, 50, 100],
        "pagingType": "full_numbers",
        "processing": true,
        "showRowNumber": true,
        "order": [[4, 'desc']]
    });

</script>
</body>
</html>