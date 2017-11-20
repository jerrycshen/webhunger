function running_statistics(url) {
    $.ajax({
        url: url,
        success: function (data) {
            var success_pages = data.success_pages;
            var error_pages = data.error_pages;
            var crawled_pages = success_pages + error_pages;
            var error_rate = error_pages * 100 / crawled_pages;
            $("#title").text(data.host_name);
            $("#started_time").text("Started Time: " + moment(data.start_time).format("YYYY-MM-DD HH:mm:ss"));
            $("#completed_time").text("Completed Time: " + moment(data.finish_time).format("YYYY-MM-DD HH:mm:ss"));
            $("#success_pages").text(success_pages);
            $("#error_pages").text(error_pages);
            if (error_pages == 0) {
                $("#error_num").text("No Error Page");
            } else if (error_pages == 1) {
                $("#error_num").text(1 + " Error Page");
            } else {
                $("#error_num").text(error_pages + " Error Pages");
            }
            $("#hostName").html("<a target='_blank' href='" + data.host_index + "'>" + data.host_name + "</a>");

            $("#error_rate").text(error_rate.toFixed(3));
            $("#crawled_pages").text(crawled_pages);
            $("#total_pages").text(crawled_pages);
            // $("#checkLog").text(data.check_log);

            // server explorer
            setCpuChart(data.cpu);
            setMemoryChart(data.memory);
            setSwapChart(data.swap);
            setDiskChart(data.disk);
            setNetInterfaceChart(data.net);
        },
        error: function (xhr, textStatus) {
            console.log('错误');
            console.log(xhr);
            console.log(textStatus);
        }
    });
}

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
        "pageLength": 10,
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

function setPageCategory(data) {
    var category_chart = echarts.getInstanceByDom(document.getElementById('category_chart'));
    category_chart.setOption({
        "series": [{
            "name": "Violate",
            "type": "bar",
            "barMaxWidth": 60,
            "barGap": "2%",
            "itemStyle": {
                "normal": {
                    "color": "#FFCC99",
                    "label": {
                        "show": true,
                        "position": "top"
                    }
                }
            },
            "data": data.violateNumList
        },
            {
                "name": "Conform",
                "type": "bar",
                "barMaxWidth": 60,
                "barGap": "2%",
                "itemStyle": {
                    "normal": {
                        "color": "#99CC99",
                        "barBorderRadius": 0,
                        "label": {
                            "show": true,
                            "position": "top"
                        }
                    }
                },
                "data": data.conformNumList
            }
        ]
    });
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