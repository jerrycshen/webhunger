package me.shenchao.webhunger.client.control;

import com.google.common.base.Charsets;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import me.shenchao.webhunger.dto.ErrorPageDTO;
import me.shenchao.webhunger.entity.CrawledResult;
import me.shenchao.webhunger.entity.Host;
import me.shenchao.webhunger.entity.HostSnapshot;
import me.shenchao.webhunger.entity.ProcessedResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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

    static File getTaskFile(String parentDir, String fileName) {
        File[] allTaskFiles = getTaskFiles(parentDir);
        for (File file : allTaskFiles) {
            if (file.getName().equals(fileName + ".task")) {
                return file;
            }
        }
        return null;
    }

    /**
     * 获取最新的快照记录
     */
    static HostSnapshot getLatestSnapshot(Host host, String snapshotPath) throws IOException {
        List<HostSnapshot> allSnapshots = getAllSnapshots(host, snapshotPath);
        if (allSnapshots.size() == 0) {
            return null;
        }
        return allSnapshots.get(allSnapshots.size() - 1);
    }

    static List<HostSnapshot> getAllSnapshots(Host host, String snapshotPath) throws IOException {
        List<HostSnapshot> snapshots = new ArrayList<>();
        File snapshotFile = new File(snapshotPath);
        if (snapshotFile.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(snapshotFile));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                snapshots.add(FileParser.parseSnapshot(host, line));
            }
        } else {
            Files.touch(snapshotFile);
        }
        return snapshots;
    }

    static CrawledResult getCrawledResult(Host host, String resultFilePath) throws IOException {
        File file = new File(resultFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        return FileParser.parseCrawledResult(host, bufferedReader.readLine());
    }

    static ProcessedResult getProcessedResult(Host host, List<HostSnapshot> snapshots) {
        return FileParser.parseProcessedResult(host, snapshots);
    }

    static List<ErrorPageDTO> getErrorPages(String hostId, String errorFilePath) throws IOException {
        File file = new File(errorFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        List<ErrorPageDTO> errorPages = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            errorPages.add(FileParser.parseErrorPages(hostId, line));
        }
        return errorPages;
    }

    static int getErrorPageNum(String errorFilePath) throws IOException {
        File file = new File(errorFilePath);
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        return (int) bufferedReader.lines().count();
    }

    /**
     * 添加新快照记录
     */
    static void createSnapshot(String snapshotPath, HostSnapshot snapshot) throws IOException {
        Files.createParentDirs(new File(snapshotPath));
        StringBuilder sb = new StringBuilder();
        sb.append(snapshot.getHost().getHostId()).append("\t")
            .append(snapshot.getState()).append("\t")
            .append(FileParser.formatPreciseDate(snapshot.getCreateTime()))
            .append("\n");
        Files.asCharSink(new File(snapshotPath), Charsets.UTF_8, FileWriteMode.APPEND).write(sb.toString());
    }

    static void saveErrorPages(String errorFilePath, List<ErrorPageDTO> errorPages) throws IOException {
        StringBuilder builder = new StringBuilder();
        for (ErrorPageDTO errorPage : errorPages) {
            builder.append(errorPage.getDepth()).append('\t').append(errorPage.getResponseCode()).append('\t')
                    .append(errorPage.getUrl()).append('\t').append(errorPage.getParentUrl()).append('\t')
                    .append(errorPage.getErrorMsg()).append('\n');
        }
        write(builder.toString(), errorFilePath, false);
    }

    static void saveCrawlingResult(String resultFilePath, CrawledResult crawledResult) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(crawledResult.getHost().getHostId()).append('\t').append(crawledResult.getTotalPageNum()).append('\t')
                .append(crawledResult.getErrorPageNum()).append('\t').append(FileParser.formatPreciseDate(crawledResult.getStartTime())).append('\t')
                .append(FileParser.formatPreciseDate(crawledResult.getEndTime())).append('\t');
        write(builder.toString(), resultFilePath, false);
    }

    static void clearSnapshot(String snapshotPath) {
        new File(snapshotPath).delete();
    }

    static void clearResult(String resultPath) {
        new File(resultPath).delete();
    }

    static void clearErrorPages(String errorFilePath) {
        new File(errorFilePath).delete();
    }

    private static void write(String content, String filePath, boolean isAppend) throws IOException {
        initFilePath(filePath);
        if (isAppend) {
            Files.asCharSink(new File(filePath), Charsets.UTF_8, FileWriteMode.APPEND).write(content);
        } else {
            Files.asCharSink(new File(filePath), Charsets.UTF_8).write(content);
        }
    }

    private static void initFilePath(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            Files.createParentDirs(file);
        }
    }

}
