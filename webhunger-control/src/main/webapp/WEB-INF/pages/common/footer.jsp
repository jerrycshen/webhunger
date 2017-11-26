<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="tag.jsp" %>

<!--Modals-->
<div class="modal fade bs-example-modal-sm" id="commonModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog ">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                <h4 class="modal-title" id="modalTitle">Congratulation!</h4>
            </div>
            <div class="modal-body">
                <em id="modalContent">You have created a crawler successfully~</em>
            </div>

        </div>
    </div>
</div>
<div id="modalWithSubBtn" class="modal fade" tabindex="-1" style="display: none;">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 id="modalWithSubBtnTitle" class="modal-title">错误提示</h4>
            </div>
            <div class="modal-body">
                <p id="modalWithSubBtnInfo">具体错误信息</p>
            </div>
            <div class="modal-footer">
                <a href="javascript:void(0)" class="btn btn-default" style="margin-bottom:0;" id="modalSubBtn" data-dismiss="modal">确认</a>
                <a href="javascript:void(0)" class="btn btn-primary" data-dismiss="modal" id="modalBtn">关闭</a>
            </div>
        </div>
    </div>
</div>

<script src="${AppContext}/framework/jquery/jquery-2.2.4.min.js"></script>
<script src="${AppContext}/framework/bootstrap/bootstrap.min.js"></script>

<!-- DataTables -->
<script src="${AppContext}/framework/datatable/jquery.dataTables.min.js"></script>
<script src="${AppContext}/framework/datatable/dataTables.bootstrap.min.js"></script>

<script src="${AppContext}/framework/moment/moment.min.js"></script>