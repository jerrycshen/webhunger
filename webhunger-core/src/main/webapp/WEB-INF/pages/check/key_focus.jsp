<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="../common/tag.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- 上述3个meta标签*必须*放在最前面，任何其他内容都*必须*跟随其后！ -->
    <title>键盘焦点陷入检测</title>

    <%@include file="../common/head.jsp" %>
</head>
<body>

<script type="text/javascript">
    var ifr = document.createElement('iframe');
    document.body.insertBefore(ifr, document.body.childNodes[0]);
    ifr.src = UrlValue("src");
    //    ifr.onload = function(){changeOnload();};
</script>

<%--<iframe height="1000" width="2000" id="iframeSrc" onload="runTabs()"></iframe>--%>
<br>

<input type="text" id="res" name="result" onblur="checkCompleted()" value="0"/>

<script type="text/javascript">
    /*window.onload = function () {
     var v = document.getElementById("iframeSrc");
     v.src = UrlValue("src");
     };*/
    function UrlValue(name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        if (r != null) return unescape(r[2]);
    }
    function checkCompleted() {
        $.ajax({
            url: '${AppContext}keyFocus/completed/' + UrlValue("host_id"),
            type: 'post'
        });
    }
    function changeOnload() {
        $.ajax({
            url: 'changeOnload',
            type: 'post'
        });
    }
</script>
</body>
</html>
