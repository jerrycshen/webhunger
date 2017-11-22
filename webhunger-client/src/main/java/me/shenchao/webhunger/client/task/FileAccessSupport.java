package me.shenchao.webhunger.client.task;

import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Jerry Shen
 * @since 0.1
 */
class FileAccessSupport {

    /**
     * 从指定目录下找到所有以task为后缀的文件
     * @return 所有以task为后缀的文件
     */
    static File[] getTaskFiles(String parentDir) {
        File taskDirFile = new File(parentDir);

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

    static Date transferDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static HostSnapshot getLatestSnapshot(String hostSnapshotDir) {
        File hostSnapshotFile = new File(hostSnapshotDir + File.separator + "host.snapshot");
        if (hostSnapshotFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(hostSnapshotFile));
                String line;
                String prev = null;
                while ((line = bufferedReader.readLine()) != null) {
                    prev = line;
                }
                if (prev == null) {
                    return null;
                }
                return FileParser.parseSnapshot(prev);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 根据taskId 与 站点域名 生成一个唯一id
     * @return taskId-host.index
     */
    static String createHostId(Host host) {
        return host.getTask().getTaskId() + "-" + host.getHostDomain();
    }

}
