package me.shenchao.webhunger.entity;

/**
 * 目前系统，爬虫总共有六种状态<br>
 *     <ul>
 *         <li>Ready</li>
 *         <li>Waiting</li>
 *         <li>Crawling</li>
 *         <li>Completed</li>
 *         <li>Suspend</li>
 *         <li>Processing</li>
 *     </ul>
 *
 *     <note>
 *         Completed 表示对整个站点的操作结束；
 *         Crawled 只是表示爬取完毕，页面可能并没有处理完;
 *     </note>
 *
 * @author Jerry Shen
 * @since 0.1
 */
public enum HostState {

    Ready(0), Waiting(-1), Crawling(1), Completed(5),  Suspend(3), Processing(2);

    HostState(int state) {
        this.state = state;
    }

    private int state;

    public int getState() {
        return state;
    }

    public static HostState getCrawlerState(int state) {
        for (HostState crawlerState : HostState.values()) {
            if (crawlerState.getState() == state) {
                return crawlerState;
            }
        }
        return null;
    }
}
