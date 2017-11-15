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

    <!-- daterange picker -->
    <link href="${AppContext}/framework/datarangepicker/daterangepicker.css" rel="stylesheet">

    <style>
        body {
            padding-top: 50px;
            padding-bottom: 30px;
        }
    </style>
</head>
<body>

<%@include file="../common/navbar.jsp"%>

<!-- Modal -->
<div class="modal fade" id="taskModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title" id="modalTitle"></h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group">
                        <label for="description" class="col-sm-3 control-label">Task Description</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="description" placeholder="" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="owner" class="col-sm-3 control-label">Task Owner</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="owner" placeholder="" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="dateSelect" class="col-sm-3 control-label">Task Time</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="dateSelect">
                        </div>
                    </div>
                    <div class="form-group" id="hostsListDiv">
                        <label for="hostsList" class="col-sm-3 control-label">Hosts List</label>
                        <div class="col-sm-9">
                            <input type="file" id="hostsList" placeholder="" required>
                            <p class="help-block">Please import Excel file, click <a
                                    href="${AppContext}download/hosts_template.xlsx">here</a> to download template</p>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" id="saveTaskBtn" class="btn btn-primary"></button>
            </div>
        </div>
    </div>
</div>

<div class="container">

    <div class="page-header">
        <ul class="nav nav-pills" role="tablist">
            <li>
                <button id="createTaskBtn" class="btn btn-default" type="button">
                    Create Task
                </button>
            </li>
        </ul>
    </div>

    <table id="task_table" class="table table-hover" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th width="4%">#</th>
            <th>Task Description</th>
            <th width="15%">Task Status</th>
            <th width="15%">Task Owner</th>
            <th width="15%">Create Time</th>
            <th width="20%">Operation</th>
        </tr>
        </thead>
    </table>
</div>


<%@include file="../common/footer.jsp"%>
<script src="${AppContext}/framework/datarangepicker/daterangepicker.js"></script>
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
            {"data": "create_time"},
            {
                "data": "description",
                "render": function (data, type, row) {
                    <%--return "<a href='${AppContext}task/"+row.task_id+"/host/list'>"+data+"</a>";--%>
                    return data;
                }
            },
            {
                "data": "status",
                "render": function (data) {
                    if (data === 0) {
                        return "<span class='label label-success'>Initializing</span>";
                    } else if (data === 1) {
                        return "<span class='label label-primary'>Running</span>";
                    } else {
                        return "<span class='label label-default'>Completed</span>";
                    }
                }
            },
            {"data": "owner"},
            {
                "data": "create_time",
                "render": function (data) {
                    return moment(data).format('YYYY-MM-DD');
                }
            },
            {
                "data": "status",
                "render": function (data, type, row) {
                    var returnStr = "<button type='button' onclick='updateTask(" + row.task_id + ")' class='btn btn-default btn-sm'>Update</button> " +
                        "<button type='button' onclick='settingTask(" + ${AppContext} +"," + row.task_id + "," + row.status + ")' class='btn btn-default btn-sm'>Setting</button> ";
                    if (row.status === 0) {
                        returnStr += "<button type='button' class='btn btn-default btn-sm' disabled>Detail</button>";
                    } else {
                        returnStr += "<button type='button' onclick='viewDetail(" + row.task_id + ")' class='btn btn-default btn-sm'>Detail</button>";
                    }
                    return returnStr;
                }
            }
        ],

        "lengthMenu": [15, 30, 50, 100],
        "pagingType": "full_numbers",
        "processing": true,
        "showRowNumber": true,
        // 第0列不需要排序与搜索
        "columnDefs": [{
            "searchable": false,
            "orderable": false,
            "targets": [0, 5]
        }],
        "order": [[4, 'desc']]
    });

    taskTable.on( 'order.dt search.dt', function () {
        taskTable.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
            cell.innerHTML = i+1;
        } );
    } ).draw();

    function viewDetail(task_id) {
        window.open("${AppContext}task/" + task_id + "/host/list", "_self");
    }

    function updateTask(task_id) {
        $("#modalTitle").html("Update Task");
        $("#saveTaskBtn").html("Update Task");
        $("#hostsListDiv").hide();
        $("#taskModal").modal("show");

        $.ajax({
            "url": "${AppContext}task/" + task_id + "/detail",
            "type": "GET",
            "success": function (data) {
                var task = data.task;
                $("#task_id").val(task.task_id);
                $("#description").val(task.description);
                $("#owner").val(task.owner);
                $("#dateSelect").val(moment(task.check_start_time).format('YYYY-MM-DD HH:mm:ss') + " - " + moment(task.deadline).format('YYYY-MM-DD HH:mm:ss'));
            }
        });

    }

    $("#applySettingBtn").on("click", function () {
        var data = JSON.stringify(constructConfig(1));
        if (data == "") {
            return;
        }
        $.ajax({
            "url": "${AppContext}task/" + $("#task_id").val() + "/config/apply",
            "type": "POST",
            "data": data,
            "contentType": "application/json;charset=utf-8",
            "success": function (data) {
                $("#settingModal").modal("hide");
                taskTable.ajax.reload();
            }
        });
    });

    $("#createTaskBtn").on("click", function () {
        $("#modalTitle").html("Create Task");
        $("#saveTaskBtn").html("Create Task");
        $("#hostsListDiv").show();

        // 清除之前数据
        $("#description").val("");
        $("#owner").val("");
        $("#dateSelect").val("");
        $("#hostsList").val("");

        $("#taskModal").modal("show");
    });

    $("#dateSelect").daterangepicker({
        "timePicker": true,
        "timePickerSeconds": true,
        "timePicker24Hour": true,
        "autoUpdateInput": false,
        "locale": {
            "format": "YYYY-MM-DD HH:mm:ss"
        }
    });

    $('#dateSelect').on('apply.daterangepicker', function (ev, picker) {
        $(this).val(picker.startDate.format('YYYY-MM-DD HH:mm:ss') + ' - ' + picker.endDate.format('YYYY-MM-DD HH:mm:ss'));
    });

    $("#saveTaskBtn").on("click", function () {
        var description = $("#description").val();
        var owner = $("#owner").val();
        var hostsList = $("#hostsList").val();
        var dateSelect = $("#dateSelect").val();
        if (description === "") {
            alert("please input task description!");
            $("#description").focus();
            return;
        }
        if (owner === "") {
            alert("please input task owner!");
            $("#owner").focus();
            return;
        }
        if (dateSelect === "") {
            alert("please input task time!");
            $("#dateSelect").focus();
            return;
        }
        if (hostsList === "" && !$("#hostsListDiv").is(":hidden")) {
            alert("please upload hosts list!");
            return;
        }

        var form = new FormData();
        var submitJson = {};
        submitJson["description"] = description;
        submitJson["owner"] = owner;

        // date
        var drp = $("#dateSelect").data('daterangepicker');
        submitJson["check_start_time"] = drp.startDate.format('YYYY-MM-DD HH:mm:ss');
        submitJson["deadline"] = drp.endDate.format('YYYY-MM-DD HH:mm:ss');

        var url;
        if (!$("#hostsListDiv").is(":hidden")) {
            form.append("hostsList", $("#hostsList").prop('files')[0]);
            url = "${AppContext}task/create";
        } else {
            submitJson["task_id"] = $("#task_id").val();
            url = "${AppContext}task/update";
        }
        form.append("data", JSON.stringify(submitJson));

        $.ajax({
            "url": url,
            "type": "POST",
            "data": form,
            "processData": false,
            "dataType": "json",
            "contentType": false,
            "success": function (data) {
                if (data.returnCode === 0) {
                    alert("Operated successfully!");
                    taskTable.ajax.reload();
                } else {
                    alert("There is something wrong about excel file!")
                }

                $("#taskModal").modal("hide");
            },
            "error": function (xhr, textStatus) {
                console.log('错误');
                console.log(xhr);
                console.log(textStatus);
            }
        });
    });

</script>
</body>
</html>