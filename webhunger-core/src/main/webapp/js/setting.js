$("#formatJsonBtn").on("click", function () {
    var json = $("#advanceSettingJson").val();
    var formattedJson = formatJson(json, false);
    $("#advanceSettingJson").val(formattedJson);
});

$("#previewJsonBtn").on("click", function () {
    var jsonObject = JSON.parse($("#advanceSettingJson").val());
    $("#previewJson").text(JSON.stringify(jsonObject, null, 2));
    $("#previewJsonModal").modal("show");
});

function createHostAndApplyCofig(AppContext, task_id, status) {
    $("#task_id").val(task_id);
    $("#host_id").val(-1);
    setting(AppContext, status, 0);
}

function viewAndUpdateConfig(AppContext, task_id, host_id, status) {
    $("#task_id").val(task_id);
    $("#host_id").val(host_id);
    setting(AppContext, status, 0);
}

/**
 * @param task_id
 * @param status 如果status为0（初始化），则显示默认设置，否则显示当前设置的参数
 */
function settingTask(AppContext, task_id, status) {
    $("#task_id").val(task_id);
    setting(AppContext, status, 1);
}

/**
 * @param status 如果status为0（初始化），则显示设置，否则显示当前设置的参数
 * @param model 0: 为host设置，1：为task设置
 */
function setting(AppContext, status, model) {
    if (model === 1) {
        // 如果是为task设置，则隐藏一些只与host相关的信息
        $(".host_setting").hide();
    }
    // 显示设置窗口
    $("#settingModal").modal("show");

    var config;

    // 如果是第一次为task作配置，则显示默认参数
    if (status === 0 && model === 1) {
        $("#snapshot_root").val("");
        $("#crawl_depth").val(5);
        $("#crawler_num").val(6);
        $("#politeness_delay").val(2000);
        $("input[name=type]").eq(1).attr('checked', 'true');
        $("#advanceSettingJson").val("");
    } else {
        // 如果是task作配置，且如果是需要对配置做修改
        // 或者是新建单个host的时候，
        // 显示task的配置
        if (model === 1 || (model === 0 && status === 0)) {
            $.ajax({
                "url": AppContext + "task/" + $("#task_id").val() + "/config/show",
                "type": "GET",
                "async": false,
                "success": function (data) {
                    config = JSON.parse(data.data);
                    $("#host_name").val("");
                    $("#host_index").val("");
                    // 回显基本配置
                    $("#snapshot_root").val(config.basic_setting.snapshot_root);
                    $("input[name=type][value=" + config.basic_setting.type + "]").prop("checked", true);
                    $("#crawl_depth").val(config.basic_setting.crawl_depth);
                    $("#crawler_num").val(config.basic_setting.crawler_num);
                    $("#politeness_delay").val(config.basic_setting.politeness_delay);
                    $("#advanceSettingJson").val(formatJson(config.advance_setting, false));
                }
            });
        } else {
            // 修改某一个具体的host配置信息
            $.ajax({
                "url": AppContext + "host/" + $("#host_id").val() + "/config/show",
                "type": "GET",
                "async": false,
                "success": function (data) {
                    config = JSON.parse(data.data);
                    // 回显基本配置
                    $("#host_name").val(config.host_name);
                    $("#host_index").val(config.host_index);

                    $("#snapshot_root").val(config.basic_setting.snapshot_root);
                    $("input[name=type][value=" + config.basic_setting.type + "]").prop("checked", true);
                    $("#crawl_depth").val(config.basic_setting.crawl_depth);
                    $("#crawler_num").val(config.basic_setting.crawler_num);
                    $("#politeness_delay").val(config.basic_setting.politeness_delay);
                    $("#advanceSettingJson").val(formatJson(config.advance_setting, false));
                }
            });
        }
    }
}

/**
 * model 0: 为host设置，1：为task设置
 * @param model
 * @returns {{}}
 */
function constructConfig(model) {
    if (!formValidate(model)) {
        return {};
    }

    // 构造配置JSON 文件
    var config = {};
    var basic_setting = {};

    config.task_id = $("#task_id").val();

    // host info
    if (model === 0) {
        config.host_name = $("#host_name").val();
        config.host_index = $("#host_index").val();
        if ($("#host_id").val() !== -1) {
            config.host_id = $("#host_id").val();
        }
    }

    // basic setting
    basic_setting["snapshot_root"] = $("#snapshot_root").val();
    basic_setting["type"] = $("input[name='type']:checked").val();
    basic_setting["crawl_depth"] = $("#crawl_depth").val();
    basic_setting["crawler_num"] = $("#crawler_num").val();
    basic_setting["politeness_delay"] = $("#politeness_delay").val();

    config["basic_setting"] = basic_setting;
    config["advance_setting"] = $("#advanceSettingJson").val();

    return config;
}

/**
 * @param model 0: 为host设置，1：为task设置
 * @returns {boolean}
 */
function formValidate(model) {

    // 如果为host设置，host name 和 url 不能为空
    if (model === 0) {
        var host_name = $("#host_name").val();
        if (host_name == "") {
            alert("Please input host name!");
            $("#host_name").focus();
            return false;
        }

        var host_index = $("#host_index").val();
        if (host_index == "") {
            alert("Please input host index!");
            $("#host_index").focus();
            return false;
        }
    }

    var snapshot_root = $("#snapshot_root").val();
    if (snapshot_root == "") {
        alert("Please input snapshot root!");
        $("#snapshot_root").focus();
        return false;
    }
    var crawl_depth = $("#crawl_depth").val();
    if (crawl_depth == "" || crawl_depth < -1 || crawl_depth === 0) {
        alert("Please input a valid depth!");
        $("#crawl_depth").focus();
        return false;
    }
    var crawler_num = $("#crawler_num").val();
    if (crawler_num == "" || crawler_num < 1) {
        alert("Please input a valid thread num!");
        $("#crawler_num").focus();
        return false;
    }
    var politeness_delay = $("#politeness_delay").val();
    if (politeness_delay == "" || politeness_delay < 0) {
        alert("Please input a valid crawl interval!");
        $("#politeness_delay").focus();
        return false;
    }

    return true;
}

function formatJson(txt, compress/*是否为压缩模式*/) {/* 格式化JSON源码(对象转换为JSON文本) */
    var indentChar = '    ';
    if (/^\s*$/.test(txt)) {
        // alert('数据为空,无法格式化! ');
        console.log("数据为空,无法格式化! ");
        return;
    }
    try {
        var data = eval('(' + txt + ')');
    }
    catch (e) {
        alert('数据源语法错误,格式化失败! 错误信息: ' + e.description, 'err');
        return;
    }
    ;
    var draw = [], last = false, This = this, line = compress ? '' : '\n', nodeCount = 0, maxDepth = 0;

    var notify = function (name, value, isLast, indent/*缩进*/, formObj) {
        nodeCount++;
        /*节点计数*/
        for (var i = 0, tab = ''; i < indent; i++)tab += indentChar;
        /* 缩进HTML */
        tab = compress ? '' : tab;
        /*压缩模式忽略缩进*/
        maxDepth = ++indent;
        /*缩进递增并记录*/
        if (value && value.constructor == Array) {/*处理数组*/
            draw.push(tab + (formObj ? ('"' + name + '":') : '') + '[' + line);
            /*缩进'[' 然后换行*/
            for (var i = 0; i < value.length; i++)
                notify(i, value[i], i == value.length - 1, indent, false);
            draw.push(tab + ']' + (isLast ? line : (',' + line)));
            /*缩进']'换行,若非尾元素则添加逗号*/
        } else if (value && typeof value == 'object') {/*处理对象*/
            draw.push(tab + (formObj ? ('"' + name + '":') : '') + '{' + line);
            /*缩进'{' 然后换行*/
            var len = 0, i = 0;
            for (var key in value)len++;
            for (var key in value)notify(key, value[key], ++i == len, indent, true);
            draw.push(tab + '}' + (isLast ? line : (',' + line)));
            /*缩进'}'换行,若非尾元素则添加逗号*/
        } else {
            if (typeof value == 'string') value = '"' + value + '"';
            draw.push(tab + (formObj ? ('"' + name + '":') : '') + value + (isLast ? '' : ',') + line);
        }
        ;
    };
    var isLast = true, indent = 0;
    notify('', data, isLast, indent, false);
    return draw.join('');
}