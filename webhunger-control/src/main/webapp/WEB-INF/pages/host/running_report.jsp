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
                <span class="label label-warning" id="startTime"></span>
            </li>
        </ul>

    </div>

    <div class="page-header">
        <h1>Crawling Progress</h1>
        <div class="progress" style="margin-top: 20px">
            <div id="crawlingProgressBar" class="progress-bar progress-bar-success progress-bar-striped active"
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
        <div class="progress" style="margin-top: 20px">
            <div id="processingProgressBar" class="progress-bar progress-bar-success progress-bar-striped active"
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
        <ul class="nav nav-pills" role="tablist">
            <li>
                <h1 id="error_num"></h1>
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

    function pullProgress() {
        $.ajax({
            "url": "${AppContext}host/${hostId}/progress",
            "type": "POST",
            "success": function (data) {
                if (data.data === null) {
                    return;
                }
                var crawlingSnapshot = data.data;
                var successPageNum = crawlingSnapshot.successPageNum;
                var errorPageNum = crawlingSnapshot.errorPageNum;
                var crawlingErrorRate = errorPageNum * 100 / (successPageNum + errorPageNum);
                var leftCrawlingPageNum = crawlingSnapshot.leftPageNum;
                var totalCrawlingPageNum = crawlingSnapshot.totalPageNum;
                var crawledPageNum = totalCrawlingPageNum - leftCrawlingPageNum;
                var crawlingProgress = crawledPageNum *100 / totalCrawlingPageNum;

                $("#hostName").html("<a target='_blank' href='" + crawlingSnapshot.hostIndex + "'>" + crawlingSnapshot.hostName + "</a>");
                $("#title").text(crawlingSnapshot.hostName);
                $("#startTime").text("Started Time: " + moment(crawlingSnapshot.startTime).format("YYYY-MM-DD HH:mm:ss"));
                $("#successCrawlingPageNum").text(successPageNum);
                $("#errorCrawlingPageNum").text(errorPageNum);
                $("#crawlingErrorRate").text(crawlingErrorRate.toFixed(3));
                $("#crawledPageNum").text(crawledPageNum);
                $("#leftCrawlingPageNum").text(leftCrawlingPageNum);
                $("#totalCrawlingPageNum").text(totalCrawlingPageNum);
                $("#crawlingProgressBar").text(Math.ceil(crawlingProgress) + "%");
                $("#crawlingProgressBar").attr("style", "min-width: 2em; " + "width:" + crawlingProgress + "%");

                // 如果是单机爬虫，由于一个线程所作的工作包括了爬取和页面处理，所以也在这里更新页面处理的进度条
                if (!${isDistributed}) {
                    $("#processingProgressBar").text(Math.ceil(crawlingProgress) + "%");
                    $("#processingProgressBar").attr("style", "min-width: 2em; " + "width:" + crawlingProgress + "%");
                }
            }
        });
    }

    window.setInterval(pullProgress, ${flushInterval});

</script>

</body>
</html>