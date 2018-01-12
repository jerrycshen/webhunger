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
 * @author Jerry Shen
 * @since 0.1
 */
public enum HostState {

    /**
     * 初始化完毕，接受start
     */
    Ready(0),

    Waiting(-1), Crawling(1), Completed(5),

    Suspend(3),

    /**
     * 事实上，爬取与处理是同时进行的，但是为了便于在爬虫快照中记录，两者顺序为前后关系，状态转为
     * Processing的时候就是爬取结束的时候
     */
    Processing(2);

    HostState(int state) {
        this.state = state;
    }

    private int state;

    public int getState() {
        return state;
    }

    public static HostState valueOf(int state) {
        for (HostState crawlerState : HostState.values()) {
            if (crawlerState.getState() == state) {
                return crawlerState;
            }
        }
        return null;
    }
}
