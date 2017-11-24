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
                <li><a href="${AppContext}task/list">Task</a></li>
            </ul>
            <ul class="nav navbar-nav">
                <li><a href="#">Crawler</a></li>
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
                <h4 class="modal-title">Setting</h4>
            </div>
            <div class="modal-body">
                <form class="form-horizontal">
                    <input type="hidden" id="task_id">
                    <input type="hidden" id="host_id">
                    <h3 class="host_setting">Host Info</h3>
                    <hr class="host_setting">
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

                    <h3>Basic Setting</h3>
                    <hr>
                    <div class="form-group">
                        <label for="snapshot_root" class="col-sm-3 control-label">Snapshot Root</label>
                        <div class="col-sm-9">
                            <input type="text" class="form-control" id="snapshot_root" placeholder="" required>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">Host Type</label>
                        <div class="col-sm-9">
                            <label class="radio-inline">
                                <input type="radio" name="type" value="0"> dynamic
                            </label>
                            <label class="radio-inline">
                                <input type="radio" name="type" value="1"> static
                            </label>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="crawl_depth" class="col-sm-3 control-label">Crawl Depth</label>
                        <div class="col-sm-9">
                            <input type="number" class="form-control" id="crawl_depth" required>
                            <p class="help-block">set
                                <mark>-1</mark>
                                to crawl the whole website
                            </p>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="crawler_num" class="col-sm-3 control-label">Thread Number</label>
                        <div class="col-sm-3">
                            <input type="number" class="form-control" min="1" max="10" id="crawler_num" required>
                        </div>
                        <label for="politeness_delay" class="col-sm-3 control-label">Crawl Interval</label>
                        <div class="col-sm-3">
                            <input type="number" class="form-control" min="1000" id="politeness_delay" required>
                        </div>
                    </div>

                    <%--Advance Setting--%>
                    <div class="page-header">
                        <h3>Advance Setting</h3>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-12">
                            <textarea id="advanceSettingJson" rows="10" style="width:100%;">
                            </textarea>
                        </div>
                        <div class="col-sm-6">
                            <button id="formatJsonBtn" type="button" class="btn btn-default">Format</button>
                            <button id="previewJsonBtn" type="button" class="btn btn-default">Preview</button>
                            <button id="advanceSettingDocBtn" onclick="window.open('${AppContext}/doc/config.html')"
                                    type="button" class="btn btn-default">Doc
                            </button>
                        </div>
                    </div>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Cancel</button>
                <button type="button" id="applySettingBtn" class="btn btn-primary">Save & Apply</button>
            </div>
        </div>
    </div>
</div>

<div class="modal fade" id="previewJsonModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
                </button>
                <h4 class="modal-title">Setting</h4>
            </div>
            <div class="modal-body">
                <pre id="previewJson"></pre>
            </div>
        </div>
    </div>
</div>
