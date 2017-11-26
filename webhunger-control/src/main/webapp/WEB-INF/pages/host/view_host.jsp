<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/tag.jsp"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>Host List</title>

    <%@include file="../common/head.jsp"%>

    <style>
        body {
            padding-top: 50px;
            padding-bottom: 30px;
        }
    </style>
</head>
<body>

<%@include file="../common/navbar.jsp"%>


<div class="container">

    <div class="page-header">
        <ul class="nav nav-pills" role="tablist">
            <li>
                <button id="totalBtn" class="btn btn-default" type="button">
                    Total <span id="totalBadge" class="badge"></span>
                </button>
            </li>
            <li>
                <button id="readyBtn" class="btn btn-info" type="button" style="margin-left: 5px">
                    Ready <span id="readyBadge" class="badge" ></span>
                </button>
            </li>
            <li>
                <button id="waitingBtn" class="btn btn-warning" type="button" style="margin-left: 5px">
                    Waiting <span id="waitingBadge" class="badge"></span>
                </button>
            </li>
            <li>
                <button id="runningBtn" class="btn btn-primary" type="button" style="margin-left: 5px">
                    Running <span id="runningBadge" class="badge"></span>
                </button>
            </li>
            <li>
                <button id="suspendBtn" class="btn btn-success" type="button" style="margin-left: 5px">
                    Processing <span id="suspendBadge" class="badge"></span>
                </button>
            </li>
            <li>
                <button id="canceledBtn" class="btn btn-danger" type="button" style="margin-left: 5px">
                    Suspend <span id="canceledBadge" class="badge"></span>
                </button>
            </li>
            <li>
                <button id="completedBtn" class="btn btn-default" type="button" style="margin-left: 5px">
                    Completed <span id="completedBadge" class="badge"></span>
                </button>
            </li>
            <li class="navbar-right" style="margin-left: 5px; margin-right: 5px">
                <button id="taskBtn" class="btn btn-primary" type="button" onclick="startTask('${task_id}')">
                    Start Task
                </button>
            </li>

        </ul>
    </div>

    <table id="host_table" class="table table-hover" cellspacing="0" width="100%">
        <thead>
        <tr>
            <th width="4%">#</th>
            <th>Host Name</th>
            <th width="15%">Crawler Status</th>
            <th width="30%">Operation</th>
            <th width="15%">Crawler Report</th>
        </tr>
        </thead>
    </table>
</div>


<%@include file="../common/footer.jsp"%>
<script src="${AppContext}/js/datatable_extension.js"></script>
<!-- 自定义 -->
<script src="${AppContext}js/setting.js"></script>
<script type="text/javascript">

    hostTable = $("#host_table").DataTable({
        "ajax": {
            "url": "${AppContext}task/${task_id}/host/list",
            "type": "POST"
        },
        "columns": [
            {"data": "hostId"},
            {
                "data": "hostName",
                "render": function (data, type, row) {
                    return "<a target='_blank' href='"+row.hostIndex+"'>"+data+"</a>";
                }
            },
            {
                "data": "state",
                "render": function (data) {
                    if (data === 0) {
                        return "<span class='label label-info'>Ready</span>";
                    } else if (data === 1) {
                        return "<span class='label label-primary'>Crawing</span>";
                    } else if (data >= 5) {
                        return "<span class='label label-default'>Completed</span>";
                    } else if (data === -1) {
                        return "<span class='label label-success'>Waiting</span>";
                    } else if (data === -2) {
                        return "<span class='label label-danger'>Canceled</span>";
                    } else if (data === -3) {
                        return "<span class='label label-warn'>Suspend</span>";
                    }
                }
            },
            {
                "data": "hostId",
                "render": function (data, type, row) {
                    var buttonStr = "";
                    // Ready
                    if (row.state === 0) {
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' onclick='startCrawler("+data+")'>Start</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Suspend</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Stop</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>ReStart</button>";
                    } else if (row.state === 1) {
                        // 正在爬取
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Start</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' >Suspend</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' onclick='stopCrawler("+data+")'>Stop</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>ReStart</button>";
                    } else if (row.state === 5) {
                        // 结束状态
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Start</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Suspend</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Stop</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' onclick='reStartCrawler("+data+")'>ReStart</button>";
                    } else if (row.state === -1) {
                        // Waiting
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Start</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Suspend</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Stop</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>ReStart</button>";
                    } else if (row.state === 2) {
                        // 爬取结束，正在页面处理阶段
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Start</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Suspend</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Stop</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>ReStart</button>";
                    } else if (row.state === 3) {
                        // 暂停阶段
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Start</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Resume</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Stop</button> ";
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>ReStart</button>";
                    }
                    return buttonStr;
                }
            },
            {
                "data": "hostId",
                "render": function (data, type, row) {
                    var buttonStr = "";
                    if (row.state === 0) {
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' disabled>Report</button> ";
                    } else {
                        buttonStr += "<button type='button' class='btn btn-default btn-sm' onclick='viewReport("+data+")'>Report</button> ";
                    }
                    buttonStr += "<button type='button' class='btn btn-default btn-sm' onclick='viewConfig(" + row.task_id + "," + data + ")'>Config</button>";
                    return buttonStr;
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
            "targets": [0,3,4]
        }]
    });

    hostTable.on( 'order.dt search.dt', function () {
        hostTable.column(0, {search:'applied', order:'applied'}).nodes().each( function (cell, i) {
            cell.innerHTML = i+1;
        });
    } ).draw();

    hostTable.on('xhr', function () {
        var json = hostTable.ajax.json();
        var readyNum = 0;
        var waitingNum = 0;
        var runningNum = 0;
        var canceledNum = 0;
        var suspendNum = 0;
        var completedNum = 0;
        for (var i = 0; i < json.data.length; ++i) {
            var state = json.data[i].state;
            if (state === 0)
                ++readyNum;
            else if (state === 1)
                ++runningNum;
            else if (state >= 5)
                ++completedNum;
            else if (state === -1)
                ++waitingNum;
            else if (state === -2)
                ++canceledNum;
            else if (state === -3)
                ++suspendNum;

        }
        $("#readyBadge").html(readyNum);
        $("#waitingBadge").html(waitingNum);
        $("#runningBadge").html(runningNum);
        $("#suspendBadge").html(suspendNum);
        $("#canceledBadge").html(canceledNum);
        $("#completedBadge").html(completedNum);
        $("#totalBadge").html(json.data.length);
    });

    /**
     * 更新hostTable数据
     */
    function updateHostTable() {
        hostTable.ajax.reload();
    }

    // 该页面每隔一分钟刷新一次，获取最新各个站点的状态
    window.setInterval(updateHostTable, 60000);

    // 2秒后再次获取各个站点的状态，因为可能有站点从waiting 变为running
    function updateHostTableAfter2SecAgain() {
        window.setTimeout(function () {
            updateHostTable();
        }, 2000);
    }

    $("#totalBtn").on("click", function () {
        hostTable.search("").draw();
    });
    $("#readyBtn").on("click", function () {
        hostTable.search("Ready").draw();
    });
    $("#waitingBtn").on("click", function () {
        hostTable.search("Waiting").draw();
    });
    $("#runningBtn").on("click", function () {
        hostTable.search("Running").draw();
    });
    $("#completedBtn").on("click", function () {
        hostTable.search("Completed").draw();
    });
    $("#suspendBtn").on("click", function () {
        hostTable.search("Suspend").draw();
    });
    $("#canceledBtn").on("click", function () {
        hostTable.search("Canceled").draw();
    });

    function startTask(task_id) {
        $.ajax({
            "url": "${AppContext}task/" + task_id + "/start",
            "type": "POST",
            "success": function () {
                updateHostTable();
                $("#modalContent").html("You have started a task successfully~");
                $("#commonModal").modal('show');

                updateHostTableAfter2SecAgain();
            }
        });
    }

    function createHost(task_id) {
        createHostAndApplyCofig("${AppContext}", task_id, 0);
    }

    $("#applySettingBtn").on("click", function () {
        var data = JSON.stringify(constructConfig(0));
        if (data == "{}") {
            return;
        }
        $.ajax({
            "url": "${AppContext}host/config/apply",
            "type": "POST",
            "data": data,
            "contentType": "application/json;charset=utf-8",
            "success": function (data) {
                $("#settingModal").modal("hide");
                hostTable.ajax.reload();
            }
        });
    });

    function startCrawler(host_id) {
        $.ajax({
            "url": "${AppContext}host/" + host_id + "/start",
            "type": "POST",
            "success": function () {
                updateHostTable();
                $("#modalContent").html("You have started a crawler successfully~");
                $("#commonModal").modal('show');

                updateHostTableAfter2SecAgain();
            }
        });
    }

    function stopCrawler(host_id) {
        $.ajax({
            "url": "${AppContext}host/" + host_id + "/stop",
            "type": "POST",
            "success": function () {
                updateHostTable();
                $("#modalContent").html("You have stopped a crawler successfully~");
                $("#commonModal").modal('show');

                updateHostTableAfter2SecAgain();
            }
        });
    }

    function reStartCrawler(host_id) {
        $.ajax({
            "url": "${AppContext}host/" + host_id + "/restart",
            "type": "POST",
            "success": function () {
                updateHostTable();
                $("#modalContent").html("You have re-started a crawler successfully~");
                $("#commonModal").modal('show');

                updateHostTableAfter2SecAgain();
            }
        });
    }

    /**
     * 查看爬虫运行报告<br>
     *  1. 当爬虫在运行时，查看爬虫实时进度
     *  2. 档爬虫运行完成时，查看最终结果
     * @param host_id host_id
     */
    function viewReport(host_id) {
        var url = "${AppContext}host/" + host_id + "/report";
        window.open(url);
    }

    function viewConfig(task_id, host_id) {
        viewAndUpdateConfig("${AppContext}", task_id, host_id, 1);
    }

</script>
</body>
</html>