package org.example.broker.core.consumerqueue;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.CommitLogMessageModel;
import org.example.broker.model.EagleMqQueueModel;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.model.consumer.ConsumerQueueOffsetModel;
import org.example.broker.util.FileNameUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
@Slf4j
@Data
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
            String filePath = getLatestFilePath();
            log.info("load file:{}", filePath);
            doMMap(filePath, offset, length);
            this.topicName = topicName;
            this.lock = new ReentrantLock();
        } catch (IOException e) {
            log.error("load file error", e);
        }
    }

    public String getLatestFilePath() {
        EagleMqQueueModel queueModel = getEagleMqQueueModel();
        long remainLength = queueModel.getOffsetLimit() - queueModel.getLatestOffset().get();
        String fileName;
        if (remainLength <= 0) {
            log.info("topic's latestCommitLog exists topicName ={}", topicName);
            fileName = this.createNewFile();
        } else {
            // 还有机会写入文件
            fileName = queueModel.getFileName();
        }
        return FileNameUtil.buildConsumerQueueName(topicName, queueId, fileName);
    }

    private EagleMqQueueModel getEagleMqQueueModel() {
        EagleMqTopicModel topicModel = CommonCache.getTopicModel(topicName);
        if (topicModel == null) {
            log.error("topic not exists topicName ={}", topicName);
            throw new IllegalStateException("topic not exists");
        }

        List<EagleMqQueueModel> queueInfoList = topicModel.getQueueInfo();
        EagleMqQueueModel queueModel = queueInfoList.stream().filter(queueInfo -> queueInfo.getId() == queueId).findFirst().orElse(null);
        if (queueModel == null) {
            log.error("queue not exists queueId ={}", queueId);
            throw new IllegalStateException("queue not exists");
        }
        return queueModel;
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


    private String createNewFile() {
        String newFileName = FileNameUtil.incConsumerQueueFileName(fileName);
        String newFilePath = FileNameUtil.buildConsumerQueueName(topicName, queueId, newFileName);
        try {
            File newFile = new File(newFilePath);
            newFile.createNewFile();
            log.info("create new file:{}", newFilePath);
        } catch (Exception e) {
            log.error("file create has error ", e);
            throw new IllegalStateException("file create has error ");
        }
        return newFileName;
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

    public void writeContent(byte[] bytes, boolean force) {
        try {
            lock.lock();
            EagleMqQueueModel eagleMqQueueModel = getEagleMqQueueModel();
            // 校验是否可以当前写入文件有空闲空间
            checkConsumerQueueHasEnableSpace(bytes);

            mappedByteBuffer.put(bytes);
            eagleMqQueueModel.getLatestOffset().addAndGet(bytes.length);
            if (force) {
                mappedByteBuffer.force();
            }
        } finally {
            lock.unlock();
        }
    }


    private void checkConsumerQueueHasEnableSpace(byte [] bytes) {
        EagleMqQueueModel eagleMqQueueModel = getEagleMqQueueModel();

        long remainLength = eagleMqQueueModel.getOffsetLimit() - eagleMqQueueModel.getLatestOffset().get();

        if (remainLength < bytes.length) {
            // 创建信息的文件
            fileName = this.createNewFile();
            eagleMqQueueModel.setFileName(fileName);
            eagleMqQueueModel.getLatestOffset().set(0);
            eagleMqQueueModel.setOffsetLimit(BrokerConstant.COMMIT_LOG_FILE_SIZE);

            String filePath = FileNameUtil.buildConsumerQueueName(topicName, queueId, fileName);
            try {
                doMMap(filePath, 0, BrokerConstant.COMMIT_LOG_FILE_SIZE);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }
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


}
