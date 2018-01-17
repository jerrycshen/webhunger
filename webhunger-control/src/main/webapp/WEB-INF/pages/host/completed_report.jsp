<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->

    <title id="title"></title>

    <%@include file="../common/head.jsp" %>

    <style>
        body {
            padding-top: 50px;
            padding-bottom: 30px;
        }
    </style>

    <link rel="stylesheet" href="${AppContext}css/completed_report.css"/>

</head>
<body>

<%@include file="../common/navbar.jsp" %>

<div class="container">
    <div class="page-header">
        <h1 id="hostName"></h1>

        <ul class="nav nav-pills" role="tablist" style="margin-top: 20px">
            <li>
                <span class="label label-default" id="crawler_state">Completed</span>
            </li>
            <li class="navbar-right" style="margin-right: 5px">
                <span class="label label-info" id="endTime"></span>
            </li>

            <li class="navbar-right">
                <span class="label label-info" id="startTime"></span>
            </li>
        </ul>

    </div>

    <div class="page-header">
        <h1>Crawling Progress</h1>
        <ul class="nav nav-pills" role="tablist">
            <li class="navbar-right" style="margin-right: 5px">
                <span class="label label-info" id="crawledTime"></span>
            </li>
        </ul>
        <div class="progress" style="margin-top: 20px">
            <div id="crawlingProgressBar" class="progress-bar progress-bar-success"
                 role="progress-bar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                0%
            </div>
        </div>
        <div>
            <ul class="nav nav-pills" role="tablist">
                <li style="margin-left: 5px">
                    Success Pages: <strong id="successCrawlingPageNum">0</strong>
                </li>
                <li style="margin-left: 15px">
                    Error Pages: <strong id="errorCrawlingPageNum">0</strong>
                </li>
                <li style="margin-left: 15px">
                    Error Rate: <strong id="crawlingErrorRate">0</strong>%
                </li>
                <li class="navbar-right" style="margin-left: 15px; margin-right: 5px">
                    Total Pages: <strong id="totalCrawlingPageNum">0</strong>
                </li>
                <li class="navbar-right" style="margin-left: 15px; margin-right: 5px">
                    Left Pages: <strong id="leftCrawlingPageNum">0</strong>
                </li>
                <li class="navbar-right">
                    Crawled Pages: <strong id="crawledPageNum">0</strong>
                </li>
            </ul>
        </div>
    </div>

    <br><br>

    <div class="page-header">
        <h1>Processing Progress</h1>
        <ul class="nav nav-pills" role="tablist">
            <li class="navbar-right" style="margin-right: 5px">
                <span class="label label-info" id="processedTime"></span>
            </li>
        </ul>
        <div class="progress" style="margin-top: 20px">
            <div id="processingProgressBar" class="progress-bar progress-bar-success"
                 role="progress-bar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                0%
            </div>
        </div>
    </div>

    <div class="page-header">
        <ul class="nav nav-pills" role="tablist">
            <li>
                <h1 id="errorPageNum"></h1>
            </li>
        </ul>
        <div>
            <div>
                <table id="errorPageTable" class="table table-hover" style="margin-top: 20px">
                    <thead>
                    <th width="15%">Status Code</th>
                    <th width="40%">Url</th>
                    <th width="45%">Error Msg</th>
                    <th width="5%">Depth</th>
                    </thead>
                </table>
            </div>
        </div>
    </div>

</div>

<%@include file="../common/footer.jsp" %>
<script src="${AppContext}js/datatable_extension.js"></script>
<script type="text/javascript">

    $.ajax({
        "url": "${AppContext}host/${hostId}/result",
        "type": "POST",
        "success": function (data) {
            if (data.data === null || data.data == undefined) {
                return;
            }
            var result = data.data;
            var errorPageNum = result.crawledResult.errorPageNum;
            var totalCrawlingPageNum = result.crawledResult.totalPageNum;
            var successPageNum = totalCrawlingPageNum - errorPageNum;
            var crawlingErrorRate = errorPageNum * 100 / (successPageNum + errorPageNum);

            $("#hostName").html("<a target='_blank' href='" + result.host.hostIndex + "'>" + result.host.hostName + "</a>");
            $("#title").text(result.host.hostName);
            $("#startTime").text("Started: " + moment(result.startTime).format("YYYY-MM-DD HH:mm:ss"));
            $("#endTime").text("Completed: " + moment(result.endTime).format("YYYY-MM-DD HH:mm:ss"));
            $("#successCrawlingPageNum").text(successPageNum);
            $("#errorCrawlingPageNum").text(errorPageNum);
            $("#crawlingErrorRate").text(crawlingErrorRate.toFixed(3));
            $("#crawledPageNum").text(successPageNum);
            $("#leftCrawlingPageNum").text("0");
            $("#totalCrawlingPageNum").text(totalCrawlingPageNum);
            $("#crawlingProgressBar").text("100%");
            $("#crawlingProgressBar").attr("style", "min-width: 2em; " + "width:" + 100 + "%");
            $("#crawledTime").text(moment(result.crawledResult.startTime).format("YYYY-MM-DD HH:mm:ss") + "  ~  "
                + moment(result.crawledResult.endTime).format("YYYY-MM-DD HH:mm:ss"));

            $("#processingProgressBar").text("100%");
            $("#processingProgressBar").attr("style", "min-width: 2em; " + "width:" + 100 + "%");
            $("#processedTime").text(moment(result.processedResult.startTime).format("YYYY-MM-DD HH:mm:ss") + "  ~  "
                + moment(result.processedResult.endTime).format("YYYY-MM-DD HH:mm:ss"));

            // update error page table
            if (errorPageNum === 0) {
                $("#errorPageNum").text("No Error");
            } else if (errorPageNum === 1) {
                $("#errorPageNum").text("1 Error");
            } else {
                $("#errorPageNum").text(errorPageNum + " Errors");
            }
        }
    });

    var errorPageTable = $('#errorPageTable').DataTable({

        "serverSide": true,

        "ajax": {
            "url": "${AppContext}host/${hostId}/error_pages",
            "type": "POST",
            "data": function (data) {
                planify(data);
            }
        },
        "columns": [
            {
                "data": "responseCode",
                "render": function (data) {
                    if (data === 0)
                        return "<i>Unknown</i>";
                    else
                        return data;
                }
            },
            {
                "data": 'url',
                "render": function (data) {
                    return "<a target='_blank' href=\'" + data + "\'>" + data + "</a>"
                }
            },
            {
                "data": 'errorMsg',
                "defaultContent": "<i>Unknown</i>"
            },
            {
                "data": 'depth'
            }
        ],
        "ordering": false,
        "lengthChange": false,
        "pageLength": 5,
        "pagingType": "full_numbers",
        "processing": true,
        "searching": false
    });

    //处理datatables数据
    function planify(data) {
        var column;//对datatables某些特殊列（三维列）进行处理
        for (var i = 0; i < data.columns.length; i++) {
            column = data.columns[i];
            column.searchRegex = column.search.regex;
            column.searchValue = column.search.value;
            delete(column.search);
        }
    }

</script>

</body>
</html>