package me.shenchao.webhunger.client.task;

import com.google.common.base.Charsets;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
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

    static Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Date parsePreciseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String formatPreciseDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return formatter.format(date);
    }

    /**
     * 获取最新的快照记录
     */
    static HostSnapshot getLatestSnapshot(String snapshotPath) throws IOException {
        File snapshotFile = new File(snapshotPath);
        if (snapshotFile.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(snapshotFile));
            String line;
            String prev = null;
            while ((line = bufferedReader.readLine()) != null) {
                prev = line;
            }
            if (prev == null) {
                return null;
            }
            return FileParser.parseSnapshot(prev);
        }
        return null;
    }

    /**
     * 添加新快照记录
     */
    static void createSnapshot(String snapshotPath, HostSnapshot snapshot) throws IOException {
        Files.createParentDirs(new File(snapshotPath));
        StringBuilder sb = new StringBuilder();
        sb.append(snapshot.getHost().getHostId()).append("\t")
            .append(snapshot.getState()).append("\t")
            .append(formatPreciseDate(snapshot.getCreateTime()))
            .append("\n");
        Files.asCharSink(new File(snapshotPath), Charsets.UTF_8, FileWriteMode.APPEND).write(sb.toString());
    }

}
