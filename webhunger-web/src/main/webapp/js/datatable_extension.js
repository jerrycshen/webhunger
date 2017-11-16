/**
 * Created by Jerry on 6/29/2017.
 * datatable 扩展插件，使得表格在刷新的时候，不会丢失当前的分页，排序，过滤信息
 *
 * @refer http://www.craiglotter.co.za/2012/05/28/how-to-refresh-a-datatable-without-losing-your-current-page-or-ordering/
 * @since 3.0
 * @author Jerry Shen
 */

(function ($) {
    $.fn.dataTableExt.oApi.fnStandingRedraw = function (oSettings) {
        if (oSettings.oFeatures.bServerSide === false) {
            var before = oSettings._iDisplayStart;

            oSettings.oApi._fnReDraw(oSettings);

            // iDisplayStart has been reset to zero - so lets change it back
            oSettings._iDisplayStart = before;
            oSettings.oApi._fnCalculateEnd(oSettings);
        }

        // draw the 'current' page
        oSettings.oApi._fnDraw(oSettings);
    };
})(window.jQuery);

// 这里有个大坑，主要的是dataTable（） 与 DataTable（） 两个方法的作用也不相同，唉，搞了一晚上, 调用时必须使用dataTable（）