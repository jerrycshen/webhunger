/**
 * websocket
 */
// TODO 地址要修改
// 实验室爬虫服务器 10.214.52.186
function initWebSocket(AppContext, hostId, server_ip) {
    var websocket;
    if ('WebSocket' in window) {
        websocket = new WebSocket("ws://" + server_ip + ":8080" + AppContext + "running_report?host_id=" + hostId);
    } else {
        websocket = new SockJS("http://" + server_ip + ":8080" + AppContext + "sockjs/running_report?host_id=" + hostId);
    }
    websocket.onopen = function (event) {
        console.log("connect successfully!!!")
    };
    websocket.onmessage = function (event) {
        var running_state = jQuery.parseJSON(event.data);
        var running_time = running_state.running_time;
        var success_pages = running_state.success_pages;
        var error_pages = running_state.error_pages;
        var crawled_pages = success_pages + error_pages;
        var error_rate = error_pages * 100 / crawled_pages;
        var left_pages = running_state.left_pages;
        var total_pages = running_state.total_pages;
        var progress = crawled_pages * 100 / total_pages;
        var speed = Math.ceil(crawled_pages / running_time);

        $("#hostName").html("<a target='_blank' href='" + running_state.host_index + "'>" + running_state.host_name + "</a>");

        $("#title").text(running_state.host_name);
        $("#running_time").text("Started Time: " + Math.floor(running_time / 60) + " minutes ago");
        $("#success_pages").text(success_pages);
        $("#error_pages").text(error_pages);
        $("#error_rate").text(error_rate.toFixed(3));
        $("#crawled_pages").text(crawled_pages);
        $("#total_pages").text(total_pages);
        $("#left_pages").text(left_pages);
        $("#progress_bar").text(Math.ceil(progress) + "%");
        $("#progress_bar").attr("style", "min-width: 2em; " + "width:" + progress + "%");
        $("#crawl_speed").text(speed + " pages per second");
        $("#left_time").text(Math.ceil(left_pages / speed / 60) + " minutes needed");

        if (error_pages == 0) {
            $("#error_num").text("No Error");
        } else if (error_pages == 1) {
            $("#error_num").text(1 + " Error");
        } else {
            $("#error_num").text(error_pages + " Errors");
        }

        if (running_state.state == 'Completed') {
            $("#crawler_state").text("Completed");
        }

        // server explorer
        setCpuChart(running_state.cpu);
        setMemoryChart(running_state.memory);
        setSwapChart(running_state.swap);
        setDiskChart(running_state.disk);
        setNetInterfaceChart(running_state.net);
    };

    websocket.onerror = function (event) {
        // TODO
    };
    websocket.onclose = function (event) {
        // TODO
    };

    $("#crawler_state").bind('DOMNodeInserted', function () {
        window.location.reload();
    });
}

$("#refreshBtn").click(function () {
    // 这里有个大坑，主要的是dataTable（） 与 DataTable（） 两个方法的作用也不相同，唉，搞了一晚上
    var table = $("#errorPageTable").dataTable();
    table.fnStandingRedraw();
});

function error_pages(url) {
    var table = $('#errorPageTable').DataTable({

        "serverSide": true,

        "ajax": {
            "url": url,
            "type": "POST",
            "data": function (data) {
                planify(data);
            }
        },
        "columns": [
            {
                "data": "status_code",
                "render": function (data) {
                    if (data === 0)
                        return "<i>Unknown</i>";
                    else
                        return data;
                }
            },
            {
                "data": 'ep_url',
                "render": function (data) {
                    return "<a target='_blank' href=\'" + data + "\'>" + data + "</a>"
                }
            },
            {
                "data": 'error_msg',
                "defaultContent": "<i>Unknown</i>"
            },
            {
                "data": 'ep_depth'
            }
        ],
        "ordering": false,
        "lengthChange": false,
        "pageLength": 5,
        "pagingType": "full_numbers",
        "processing": true,
        "searching": false
    });
}

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


function setCpuChart(cpu_data) {
    var cpu_chart = echarts.getInstanceByDom(document.getElementById('cpu_chart'));
    cpu_chart.setOption({
        series: [
            {
                data: [
                    {
                        value: cpu_data,
                        name: 'CPU',
                        itemStyle: {
                            normal: {
                                color: '#5CB85C'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '{d} %',
                                textStyle: {
                                    fontSize: 15
                                }
                            }
                        }
                    },
                    {
                        value: 100 - cpu_data,
                        tooltip: {
                            show: false
                        },
                        itemStyle: {
                            normal: {
                                color: '#EEEEEE'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '\nCPU',
                                textStyle: {
                                    color: '#5CB85C'
                                }
                            }
                        }
                    }
                ]
            }
        ]
    });
}

function setMemoryChart(memory_data) {
    var memory_chart = echarts.getInstanceByDom(document.getElementById('memory_chart'));
    memory_chart.setOption({
        series: [
            {
                data: [
                    {
                        value: memory_data,
                        name: 'Memory',
                        itemStyle: {
                            normal: {
                                color: '#428BCA'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '{d} %',
                                textStyle: {
                                    fontSize: 15
                                }
                            }
                        }
                    },
                    {
                        value: 100 - memory_data,
                        tooltip: {
                            show: false
                        },
                        itemStyle: {
                            normal: {
                                color: '#EEEEEE'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '\nMemory',
                                textStyle: {
                                    color: '#428BCA'
                                }
                            }
                        }
                    }
                ]
            }
        ]
    });
}

function setSwapChart(swap_data) {
    var swap_chart = echarts.getInstanceByDom(document.getElementById('swap_chart'));
    swap_chart.setOption({
        series: [
            {
                data: [
                    {
                        value: swap_data,
                        name: 'Swap',
                        itemStyle: {
                            normal: {
                                color: '#F39C12'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '{d} %',
                                textStyle: {
                                    fontSize: 15
                                }
                            }
                        }
                    },
                    {
                        value: 100 - swap_data,
                        tooltip: {
                            show: false
                        },
                        itemStyle: {
                            normal: {
                                color: '#EEEEEE'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '\nSwap',
                                textStyle: {
                                    color: '#F39C12'
                                }
                            }
                        }
                    }
                ]
            }
        ]
    });
}

function setDiskChart(disk_data) {
    var disk_chart = echarts.getInstanceByDom(document.getElementById('disk_chart'));
    disk_chart.setOption({
        series: [
            {
                data: [
                    {
                        value: disk_data,
                        name: 'Disk',
                        itemStyle: {
                            normal: {
                                color: '#FF9E8C'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '{d} %',
                                textStyle: {
                                    fontSize: 15
                                }
                            }
                        }
                    },
                    {
                        value: 100 - disk_data,
                        tooltip: {
                            show: false
                        },
                        itemStyle: {
                            normal: {
                                color: '#EEEEEE'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '\nDisk',
                                textStyle: {
                                    color: '#FF9E8C'
                                }
                            }
                        }
                    }
                ]
            }
        ]
    });
}

function setNetInterfaceChart(net_data) {
    var net_chart = echarts.getInstanceByDom(document.getElementById('net_chart'));
    net_chart.setOption({
        series: [
            {
                data: [
                    {
                        value: net_data,
                        name: 'NetInterface',
                        itemStyle: {
                            normal: {
                                color: '#00D2C9'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '{d} %',
                                textStyle: {
                                    fontSize: 15
                                }
                            }
                        }
                    },
                    {
                        value: 100 - net_data,
                        tooltip: {
                            show: false
                        },
                        itemStyle: {
                            normal: {
                                color: '#EEEEEE'
                            }
                        },
                        label: {
                            normal: {
                                formatter: '\nNetInterface',
                                textStyle: {
                                    color: '#00D2C9'
                                }
                            }
                        }
                    }
                ]
            }
        ]
    });
}