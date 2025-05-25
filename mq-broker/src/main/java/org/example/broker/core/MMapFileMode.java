package org.example.broker.core;

import lombok.extern.slf4j.Slf4j;
import org.example.broker.cache.CommonCache;
import org.example.broker.constant.BrokerConstant;
import org.example.broker.model.CommitLogMessageModel;
import org.example.broker.model.EagleMqTopicModel;
import org.example.broker.model.TopicLatestCommitLogModel;
import org.example.broker.util.ByteConvertUtil;
import org.example.broker.util.CommonLogFileNameUtil;
import org.example.broker.util.MMapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author qushutao
 * @since 2025/5/25-0:04
 */
@Slf4j
public class MMapFileMode {
    private MappedByteBuffer mappedByteBuffer;
    private File file;
    private FileChannel fileChannel;

    public void loadFileInMMap(String topicName, int offset, int length) {
        String filePath = getLatestFileName(topicName);
        try {
            log.info("load file:{}", filePath);
            file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("file not exists");
            }
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, length);
        } catch (IOException e) {
            log.error("load file error", e);
        }
    }

    public String getLatestFileName(String topicName) {
        EagleMqTopicModel eagleMqTopicModel = CommonCache.TOPIC_MODEL_MAP.get(topicName);
        if (eagleMqTopicModel == null) {
            log.error("topic not exists topicName ={}", topicName);
            throw new IllegalStateException("topic not exists");
        }

        TopicLatestCommitLogModel latestCommitLog = eagleMqTopicModel.getLatestCommitLog();
        if (latestCommitLog == null) {
            log.error("topic's latestCommitLog not exists topicName ={}", topicName);
            throw new IllegalStateException("topic's latestCommitLog not exists");
        }
        int remainLength = latestCommitLog.getOffsetLimit() - latestCommitLog.getOffset();
        String fileName;
        if (remainLength <= 0) {
            log.info("topic's latestCommitLog exists topicName ={}", topicName);
            return this.createNewFile(topicName, latestCommitLog);
        } else {
            // 还有机会写入文件
            fileName = latestCommitLog.getFileName();
            return CommonCache.getGlobalProperties().getMqHome() +
                    BrokerConstant.BASIC_STORE_PATH +
                    topicName + "\\" + fileName;
        }

    }

    private String createNewFile(String topicName, TopicLatestCommitLogModel latestCommitLog) {
        String newFileName = CommonLogFileNameUtil.incCommitLogFileName(latestCommitLog.getFileName());
        String filePath = CommonCache.getGlobalProperties().getMqHome() +
                BrokerConstant.BASIC_STORE_PATH +
                topicName + "\\" + newFileName;
        try {
            File newFile = new File(filePath);
            newFile.createNewFile();
        } catch (Exception e) {
            log.error("file create has error ", e);
            throw new IllegalStateException("file create has error ");
        }
        return filePath;
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

        byte[] bytes = getBytes(commitLogMessageModel);
        mappedByteBuffer.put(bytes);
        if (force) {
            mappedByteBuffer.force();
        }
    }

    private static byte[] getBytes(CommitLogMessageModel commitLogMessageModel) {
        byte[] contentBytes = commitLogMessageModel.getContent();
        byte[] sizeBytes = ByteConvertUtil.intToByteArray(commitLogMessageModel.getSize());
        byte[] bytes = new byte[sizeBytes.length + contentBytes.length];
        System.arraycopy(sizeBytes, 0, bytes, 0, sizeBytes.length);
        System.arraycopy(contentBytes, 0, bytes, sizeBytes.length, contentBytes.length);
        return bytes;
    }

//    public void clear() {
//        if (!mappedByteBuffer.isDirect()) {
//            return;
//        }
//        try {
//            Method cleanerMethod = mappedByteBuffer.getClass().getMethod("cleaner");
//            cleanerMethod.setAccessible(true);
//            Object cleaner = cleanerMethod.invoke(mappedByteBuffer);
//            if (cleaner != null) {
//                Method cleanMethod = cleaner.getClass().getMethod("clean");
//                cleanMethod.invoke(cleaner);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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

    public static void main(String[] args) {
        MMapUtil mMapUtil = new MMapUtil();
        mMapUtil.loadFileInMMap("mq-broker/src/main/java/com/example/mqbroker/order/000000", 0, 1024 * 1024);
        byte[] bytes = mMapUtil.readContent(0, 1024);
        System.out.println(new String(bytes));
        mMapUtil.clean();
    }

}
