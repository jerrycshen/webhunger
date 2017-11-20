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

    <link rel="stylesheet" href="${AppContext}css/running_report.css"/>

    <script src="${AppContext}/framework/echart/echarts.min.js"></script>

</head>
<body>

<%@include file="../common/navbar.jsp" %>

<div class="container">
    <div class="page-header">
        <h1 id="hostName"></h1>

        <ul class="nav nav-pills" role="tablist" style="margin-top: 20px">
            <li>
                <span class="label label-primary" id="crawler_state">Running</span>
            </li>
            <li class="navbar-right" style="margin-right: 5px">
                <span class="label label-warning" id="running_time"></span>
            </li>
        </ul>

    </div>

    <div class="page-header">
        <h1>Progress </h1>
        <ul class="nav nav-pills" role="tablist">
            <li class="navbar-right" style="margin-right: 5px">
                <span class="label label-primary" id="left_time"></span>
            </li>
            <li class="navbar-right" style="margin-right: 5px">
                <span class="label label-primary" id="crawl_speed"></span>
            </li>
        </ul>
        <div class="progress" style="margin-top: 20px">
            <div id="progress_bar" class="progress-bar progress-bar-success progress-bar-striped active"
                 role="progress-bar" aria-valuemin="0" aria-valuemax="100" style="width: 0%;">
                0%
            </div>
        </div>
        <div>
            <ul class="nav nav-pills" role="tablist">
                <li style="margin-left: 5px">
                    Success Pages: <strong id="success_pages">0</strong>
                </li>
                <li style="margin-left: 15px">
                    Error Pages: <strong id="error_pages">0</strong>
                </li>
                <li style="margin-left: 15px">
                    Error Rate: <strong id="error_rate">0</strong>%
                </li>
                <li class="navbar-right" style="margin-left: 15px; margin-right: 5px">
                    Total Pages: <strong id="total_pages">0</strong>
                </li>
                <li class="navbar-right" style="margin-left: 15px; margin-right: 5px">
                    Left Pages: <strong id="left_pages">0</strong>
                </li>
                <li class="navbar-right">
                    Crawled Pages: <strong id="crawled_pages">0</strong>
                </li>
            </ul>
        </div>
    </div>

    <div class="page-header">
        <h1>Server Explorer</h1>
        <div class="container">
            <div class="row centered">
                <div class="col-md-3">
                    <div id="cpu_chart" style="width: 200px;height:200px;"></div>
                </div>
                <div class="col-md-3">
                    <div id="memory_chart" style="width: 200px;height:200px;"></div>
                </div>
                <div class="col-md-3">
                    <div id="swap_chart" style="width: 200px;height:200px;"></div>
                </div>
                <div class="col-md-3">
                    <div id="disk_chart" style="width: 200px;height:200px;"></div>
                </div>
                <div class="col-md-3">
                    <div id="net_chart" style="width: 200px;height:200px;"></div>
                </div>

            </div>
        </div>
    </div>

    <div class="page-header">
        <ul class="nav nav-pills" role="tablist">
            <li>
                <h1 id="error_num"></h1>
            </li>
            <li class="navbar-right" style="margin-top: 20px; margin-right: 5px">
                <button id="refreshBtn" type="button" class="btn btn-default"><span class="glyphicon glyphicon-refresh"
                                                                                    aria-hidden="true"></span> Refresh
                </button>
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
<!-- Placed at the end of the document so the pages load faster -->
<script src="${AppContext}/framework/socketjs/sockjs.min.js"></script>
<script src="${AppContext}js/server_chart.js"></script>
<script src="${AppContext}/js/datatable_extension.js"></script>
<script src="${AppContext}js/running_report.js"></script>

<script>
    initWebSocket("${AppContext}", "${host_id}", "${server_ip}");
    error_pages("${AppContext}host/${host_id}/error_pages");
</script>

</body>
</html>