package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.util.SystemUtil;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jerry Shen
 * @since 0.1
 */
class FileAccessSupport {

    private String taskDataDir;

    FileAccessSupport(String taskDataDir) {
        this.taskDataDir = taskDataDir;
    }

    /**
     * 从指定目录下找到所有以task为后缀的文件
     * @return 所有以task为后缀的文件
     */
    File[] getTaskFiles() {
        File taskDirFile = new File(taskDataDir);

        File[] taskFiles = taskDirFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".task");
            }
        });

        if (taskFiles == null || taskFiles.length == 0) {
            return new File[]{};
        }
        return taskFiles;
    }

    HostSnapshot getLatestSnapshot(File snapshotFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(snapshotFile));
            String line = null;
            String prev = null;
            while ((line = bufferedReader.readLine()) != null) {
                prev = line;
            }
            if (prev == null) {
                return null;
            }
            String[] fields = prev.split("\t");
            HostSnapshot hostSnapshot = new HostSnapshot();
            hostSnapshot.setHostId(fields[0]);
            hostSnapshot.setSuccessPageNum(Integer.parseInt(fields[1]));
            hostSnapshot.setErrorPageNum(Integer.parseInt(fields[2]));
            hostSnapshot.setProcessedPageNum(Integer.parseInt(fields[3]));
            hostSnapshot.setState(Integer.parseInt(fields[4]));
            hostSnapshot.setCreateTime(transferDate(fields[5]));

            return hostSnapshot;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Date transferDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getLatestHostState(Host host) {
        HostSnapshot hostSnapshot = getLatestSnapshot(host);
        return hostSnapshot == null ? 0 : hostSnapshot.getState();
    }

    private HostSnapshot getLatestSnapshot(Host host) {
        String hostSnapshotDir = getHostResultDir(host);
        File hostSnapshotFile = new File(hostSnapshotDir + File.separator + "host.snapshot");
        if (hostSnapshotFile.exists()) {
            return getLatestSnapshot(hostSnapshotFile);
        }
        return null;
    }

    /**
     * 通过UUID算法将可读性ID生成22位的ID
     */
    static String createHostId(String readableId) {
        return SystemUtil.generateId(readableId);
    }

    /**
     * 根据taskId 与 站点域名 生成一个可读性id
     * @return taskId-host.index
     */
    static String createReadableHostId(Host host) {
        return host.getTask().getTaskId() + "-" + host.getHostDomain();
    }

    private String getHostResultDir(Host host) {
        return taskDataDir + File.separator + "result" + File.separator + host.getTask().getTaskId() +
                File.separator + createReadableHostId(host);
    }
}
