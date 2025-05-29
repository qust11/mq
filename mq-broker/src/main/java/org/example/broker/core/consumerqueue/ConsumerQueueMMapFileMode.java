package org.example.broker.core.consumerqueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.CommitLogMessageModel;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.model.TopicLatestCommitLogModel;
import org.example.broker.model.consumer.ConsumerQueueDetailModel;
import org.example.broker.util.CommitLogFileNameUtil;
import org.example.broker.util.MMapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
@Slf4j
public class ConsumerQueueMMapFileMode {
    private MappedByteBuffer mappedByteBuffer;

    private FileChannel fileChannel;

    private String topicName;

    private int queueId;

    private String fileName;

    private Lock lock;

    public void loadFileInMMap(String topicName, int queueId, String fileName, int offset, int length) {
        this.fileName = fileName;
        this.queueId = queueId;

        try {
            log.info("load file:{}", filePath);
            doMMap(filePath, offset, length);
            this.topicName = topicName;
            this.lock = new ReentrantLock();
        } catch (IOException e) {
            log.error("load file error", e);
        }
    }

    private void doMMap(String filePath, int offset, int length) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("file not exists");
        }
        try (FileChannel ignored = this.fileChannel = new RandomAccessFile(file, "rw").getChannel()) {
            this.mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, length);
        }

    }

    public String getLatestFilePath(String topicName) {
        TopicLatestCommitLogModel latestCommitLog = getTopicLatestCommitLogModel(topicName);

        long remainLength = latestCommitLog.getOffsetLimit() - latestCommitLog.getOffset().get();
        String fileName;
        if (remainLength <= 0) {
            log.info("topic's latestCommitLog exists topicName ={}", topicName);
            CommitLogFileInfo newFile = this.createNewFile(topicName, latestCommitLog);
            fileName = newFile.getFileName();
        } else {
            // 还有机会写入文件
            fileName = latestCommitLog.getFileName();
        }
        return CommitLogFileNameUtil.buildCommitLogFileName(topicName, fileName);

    }

    private CommitLogFileInfo createNewFile(String topicName, TopicLatestCommitLogModel latestCommitLog) {
        String newFileName = CommitLogFileNameUtil.incCommitLogFileName(latestCommitLog.getFileName());
        String newFilePath = CommitLogFileNameUtil.buildCommitLogFileName(topicName, newFileName);
        try {
            File newFile = new File(newFilePath);
            newFile.createNewFile();
        } catch (Exception e) {
            log.error("file create has error ", e);
            throw new IllegalStateException("file create has error ");
        }
        latestCommitLog.setOffset(new AtomicInteger(0));
        latestCommitLog.setOffsetLimit(BrokerConstant.COMMIT_LOG_FILE_SIZE);
        latestCommitLog.setFileName(newFileName);
        return new CommitLogFileInfo(newFileName, newFilePath);
    }

    public byte[] readContent(int offset, int size) {
        mappedByteBuffer.position(offset);
        byte[] bytes = new byte[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            bytes[j++] = mappedByteBuffer.get(offset + i);
        }
        return bytes;
    }

    public void writeContent(CommitLogMessageModel commitLogMessageModel, boolean force) {
        TopicLatestCommitLogModel latestCommitLog = getTopicLatestCommitLogModel(topicName);
        try {
            lock.lock();
            // 校验是否可以当前写入文件有空闲空间
            checkCommitLogHasEnableSpace(commitLogMessageModel);

            byte[] bytes = commitLogMessageModel.getBytes();
            mappedByteBuffer.put(bytes);
            int sourceIndex = latestCommitLog.getOffset().get();
            latestCommitLog.getOffset().addAndGet(bytes.length);
            this.dispatcher(bytes, sourceIndex);
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            lock.unlock();
        }
    }

    private void dispatcher(byte[] bytes, int msgIndex) {
        TopicLatestCommitLogModel latestCommitLog = getTopicLatestCommitLogModel(topicName);
        String fileName = latestCommitLog.getFileName();

        ConsumerQueueDetailModel consumerQueueDetailModel = new ConsumerQueueDetailModel();
        consumerQueueDetailModel.setCommitLogFileName(Integer.parseInt(fileName));
        consumerQueueDetailModel.setMsgIndex(msgIndex);
        consumerQueueDetailModel.setMsgLength(bytes.length);

    }

    private TopicLatestCommitLogModel getTopicLatestCommitLogModel(String topicName) {
        EagleMqTopicModel topicModel = CommonCache.getTopicModel(topicName);
        if (topicModel == null) {
            log.error("topic not exists topicName ={}", topicName);
            throw new IllegalStateException("topic not exists");
        }
        TopicLatestCommitLogModel latestCommitLog = topicModel.getLatestCommitLog();
        if (latestCommitLog == null) {
            log.error("topic's latestCommitLog not exists topicName ={}", topicName);
            throw new IllegalStateException("topic's latestCommitLog not exists");
        }
        return latestCommitLog;
    }

    private void checkCommitLogHasEnableSpace(CommitLogMessageModel commitLogMessageModel) {

        EagleMqTopicModel topicModel = CommonCache.getTopicModel(topicName);
        TopicLatestCommitLogModel latestCommitLog = topicModel.getLatestCommitLog();

        long remainLength = latestCommitLog.getOffsetLimit() - latestCommitLog.getOffset().get();

        if (remainLength < commitLogMessageModel.getContent().length) {
            // 创建信息的文件
            CommitLogFileInfo commitLogFileInfo = this.createNewFile(topicName, latestCommitLog);

            try {
                doMMap(commitLogFileInfo.getFilePath(), 0, BrokerConstant.COMMIT_LOG_FILE_SIZE);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }


    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0)
            return;
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    private Object invoke(final Object target, final String methodName, final Class<?>... args) {
        try {
            Method method = method(target, methodName, args);
            method.setAccessible(true); // 替代 AccessController.doPrivileged
            return method.invoke(target);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private Method method(Object target, String methodName, Class<?>[] args)
            throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals("attachment")) {
                methodName = "attachment";
                break;
            }
        }

        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        if (viewedBuffer == null)
            return buffer;
        else
            return viewed(viewedBuffer);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommitLogFileInfo {

        private String fileName;

        private String filePath;
    }

    public static void main(String[] args) {
        MMapUtil mMapUtil = new MMapUtil();
        mMapUtil.loadFileInMMap("mq-broker/src/main/java/com/example/mqbroker/order/000000", 0, 1024 * 1024);
        byte[] bytes = mMapUtil.readContent(0, 1024);
        log.info(new String(bytes));
        mMapUtil.clean();
    }

}
