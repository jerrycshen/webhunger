<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="tag.jsp"%>

<!-- Static navbar -->
<nav class="navbar navbar-default navbar-fixed-top navbar-inverse" role="navigation">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="${AppContext}task/list">WebHunger</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li id="taskMenu"><a href="${AppContext}task/list">Task</a></li>
                <li id="crawlerMenu"><a href="${AppContext}crawler/list">Crawler</a></li>
                <li id="processorMenu"><a href="${AppContext}processor/list">Processor</a></li>
            </ul>
        </div><!--/.nav-collapse -->
    </div>
</nav>

<div class="modal fade" id="settingModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">Host Configuration</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <div class="form-group host_setting">
                        <label for="host_name" class="col-sm-3 control-label">Host Name</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="host_name" placeholder="" required>
                        </div>
                    </div>
                    <div class="form-group host_setting">
                        <label for="host_index" class="col-sm-3 control-label">Host Index</label>
                        <div class="col-sm-9">
                            <input type="url" class="form-control" id="host_index" placeholder="" required>
                        </div>
                    </div>
                    <div class="form-group host_setting">
                        <label for="time" class="col-sm-3 control-label">Time</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="time" required>
                        </div>
                    </div>

                    <hr>
                    <%--<div class="form-group">--%>
                        <%--<label class="col-sm-3 control-label">Host Type</label>--%>
                        <%--<div class="col-sm-9">--%>
                            <%--<label class="radio-inline">--%>
                                <%--<input type="radio" name="type" value="0"> dynamic--%>
                            <%--</label>--%>
                            <%--<label class="radio-inline">--%>
                                <%--<input type="radio" name="type" value="1"> static--%>
                            <%--</label>--%>
                        <%--</div>--%>
                    <%--</div>--%>
                    <div class="form-group">
                        <label for="crawlDepth" class="col-sm-3 control-label">Crawl Depth</label>
                        <div class="col-sm-3">
                            <input type="number" class="form-control" id="crawlDepth" required>
                        </div>
                        <label for="leastInterval" class="col-sm-3 control-label">Interval</label>
                        <div class="col-sm-3">
                            <input type="number" class="form-control" min="1000" id="leastInterval" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="processorJarDir" class="col-sm-3 control-label">ProcessorJar Dir</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="processorJarDir" required>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    </div>
</div>
