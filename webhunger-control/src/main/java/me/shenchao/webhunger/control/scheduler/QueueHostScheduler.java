package me.shenchao.webhunger.control.scheduler;

import me.shenchao.webhunger.entity.Host;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 基本的调度器，遵循FIFO，顺序返回Host
 *
 * @author Jerry Shen
 * @since 0.1
 */
public class QueueHostScheduler implements HostScheduler {

    private BlockingQueue<Host> readyHostQueue = new LinkedBlockingQueue<>();

    @Override
    public void push(Host host) {
        readyHostQueue.add(host);
    }

    @Override
    public void pushAll(List<Host> hosts) {
        readyHostQueue.addAll(hosts);
    }

    @Override
    public Host poll() {
        return readyHostQueue.poll();
    }
}
