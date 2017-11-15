// 基于准备好的dom，初始化echarts实例
var chart = echarts.init(document.getElementById('category_chart'));
option = {
    "tooltip": {
        "trigger": "axis",
        "axisPointer": {
            "type": "shadow",
            "shadowStyle": {
                "color": "rgba(120,120,120,0.1)"
            }
        }
    },
    "grid": {
        "x": "8%",
        "x2": "8%",
        "borderWidth": 0,
        "top": 40,
        "bottom": 30
    },
    "legend": {
        "right": '8%',
        "top": 20,
        "textStyle": {
            color: '#90979c'
        },
        "data": ['Violate', 'Conform']
    },

    "calculable": true,
    "xAxis": [{
        "type": "category",
        "axisLine": {
            lineStyle: {
                color: '#90979c'
            }
        },
        "splitLine": {
            "show": false
        },
        "axisTick": {
            "show": false
        },
        "splitArea": {
            "show": false
        },
        "axisLabel": {
            "interval": 0

        },
        "data": ['非文本链接', '非文本控件', '非文本内容', '验证码', '多媒体', '表单']
    }],
    "yAxis": [{
        "type": "value",
        "splitLine": {
            "show": false
        },
        "axisLine": {
            lineStyle: {
                color: '#90979c'
            }
        },
        "axisTick": {
            "show": false
        },
        "axisLabel": {
            "interval": 0

        },
        "splitArea": {
            "show": false
        }

    }],
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
        "data": []
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
            "data": []
        }
    ]
};
// 使用刚指定的配置项和数据显示图表。
chart.setOption(option);
