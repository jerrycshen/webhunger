var cpu_chart = echarts.init(document.getElementById('cpu_chart'));
option = {
    tooltip: {
        trigger: 'item',
        formatter: "{b} : {d}%"
    },
    series: [
        {
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: 0,
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
                    value: 100,
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
};
// 使用刚指定的配置项和数据显示图表。
cpu_chart.setOption(option);


var memory_chart = echarts.init(document.getElementById('memory_chart'));
option = {
    tooltip: {
        trigger: 'item',
        formatter: "{b} : {d}%"
    },
    series: [
        {
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: 0,
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
                    value: 100,
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
};
memory_chart.setOption(option);


var swap_chart = echarts.init(document.getElementById('swap_chart'));
option = {
    tooltip: {
        trigger: 'item',
        formatter: "{b} : {d}%"
    },
    series: [
        {
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: 0, name: 'Swap',
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
                    value: 100,
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
};
swap_chart.setOption(option);


var disk_chart = echarts.init(document.getElementById('disk_chart'));
option = {
    tooltip: {
        trigger: 'item',
        formatter: "{b} : {d}%"
    },
    series: [
        {
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: 0, name: 'Disk',
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
                    value: 100,
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
};
disk_chart.setOption(option);

var net_chart = echarts.init(document.getElementById('net_chart'));
option = {
    tooltip: {
        trigger: 'item',
        formatter: "{b} : {d}%"
    },
    series: [
        {
            type: 'pie',
            radius: ['40%', '55%'],
            label: {
                normal: {
                    position: 'center'
                }
            },
            data: [
                {
                    value: 0, name: 'NetInterface',
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
                    value: 100,
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
};
net_chart.setOption(option);