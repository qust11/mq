package org.example.broker.util;


import lombok.extern.slf4j.Slf4j;
import org.example.broker.constant.BrokerConstant;
import sun.misc.Unsafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qushutao
 * @since 2025-05-24
 * --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.ref=ALL-UNNAMED
 */
@Slf4j
public class MMapUtil {
    private MappedByteBuffer mappedByteBuffer;
    private File file;
    private FileChannel fileChannel;
    public void loadFileInMMap(String filePath, long offset, long length) {
        try {
            file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException("file not exists");
            }
            fileChannel = new RandomAccessFile(file, "rw").getChannel();
            mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, length);
        } catch (IOException e) {
//            log.error("load file error", e);
        }
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
        mappedByteBuffer.put(bytes);
        if (force) {
            mappedByteBuffer.force();
        }
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
        mMapUtil.loadFileInMMap("E:\\developeCode\\java\\bastmq\\broker\\store\\order_cancel_topic/00000000", 0, 1024 * 1024);
        byte[] bytes = mMapUtil.readContent(0, 1024);

//        List<Thread> threads = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            int finalI = i;
//            Thread thread = new Thread(() -> {
//                mMapUtil.writeContent(("test-content" + finalI).getBytes(), true);
//            });
//            threads.add(thread);
//        }
//        threads.forEach(Thread::start);
//        threads.forEach(thread -> {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        });
//        byte [] bytes = mMapUtil.readContent(0, 1024);
//        log.info(new String(bytes));
    }
}
