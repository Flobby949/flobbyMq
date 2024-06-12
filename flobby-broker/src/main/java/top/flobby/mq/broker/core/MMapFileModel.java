package top.flobby.mq.broker.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 基础 mmap 对象模型
 * @create : 2024-06-12 09:42
 **/

public class MMapFileModel {

    private File file;
    private MappedByteBuffer mappedByteBuffer;
    private FileChannel fileChannel;

    /**
     * 支持指定 offset 的文件映射
     * 结束映射 offset - 开始映射 offset = 映射的内存体积
     *
     * @param filePath    文件路径
     * @param startOffset 起始偏移量
     * @param mappedSize  映射体积
     */
    public void loadFileInMMap(String filePath, long startOffset, int mappedSize) throws IOException {
        file = new File(filePath);
        // 文件不存在，抛出异常
        if (!file.exists()) {
            throw new FileNotFoundException("filePath is " + filePath + "inValid！");
        }
        fileChannel = new RandomAccessFile(file, "rw").getChannel();
        mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, startOffset, mappedSize);
    }

    /**
     * 文件从指定的 offset 开始读取
     *
     * @param readOffset 读取偏移量
     * @param size       大小
     * @return {@link byte[] }
     */
    public byte[] readContent(int readOffset, int size) {
        // 定位到指定 offset
        mappedByteBuffer.position(readOffset);
        byte[] content = new byte[size];
        int j = 0;
        for (int i = 0; i < size; i++) {
            // 从内存中访问，效率非常高
            byte b = mappedByteBuffer.get(readOffset + i);
            content[j++] = b;
        }
        return content;
    }

    /**
     * 文件从指定的 offset 开始写入
     *
     * @param content 内容
     * @param force   是否强制刷盘
     */
    public void writeContent(byte[] content, boolean force) {
        // 默认刷到 page cache 中（异步）
        mappedByteBuffer.put(content);
        if (force) {
            // 强制刷盘
            mappedByteBuffer.force();
        }
    }

    public void writeContent(byte[] content) {
        this.writeContent(content, false);
    }

    /**
     * 释放资源，推荐方式
     * 通过反射安全机制释放内存资源
     */
    public void clean() {
        if (mappedByteBuffer == null || !mappedByteBuffer.isDirect() || mappedByteBuffer.capacity() == 0) {
            return;
        }
        // 调用 DirectByteBuffer 类中的 Cleaner 属性中的 clean 方法
        invoke(invoke(viewed(mappedByteBuffer), "cleaner"), "clean");
    }

    /**
     * 反射机制调用方法
     *
     * @param target     目标
     * @param methodName 方法名称
     * @param args       参数
     * @return {@link Object }
     */
    public Object invoke(final Object target, final String methodName, final Class<?>... args) {
        return AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = method(target, methodName, args);
                    method.setAccessible(true);
                    return method.invoke(target);
                } catch (Exception e) {
                    throw new IllegalStateException(e);
                }
            }
        });
    }

    /**
     * 反射获取方法
     *
     * @param target     目标
     * @param methodName 方法名称
     * @param args       参数
     * @return {@link Method }
     * @throws NoSuchMethodException 异常
     */
    private Method method(Object target, String methodName, Class<?>[] args) throws NoSuchMethodException {
        try {
            return target.getClass().getMethod(methodName, args);
        } catch (NoSuchMethodException e) {
            return target.getClass().getDeclaredMethod(methodName, args);
        }
    }

    /**
     * 解析 ByteBuffer 中的 attachment 属性
     *
     * @param buffer 缓冲区
     * @return {@link ByteBuffer }
     */
    private ByteBuffer viewed(ByteBuffer buffer) {
        String methodName = "viewedBuffer";
        Method[] methods = buffer.getClass().getMethods();
        for (Method method : methods) {
            if ("attachment".equals(method.getName())) {
                methodName = "attachment";
                break;
            }
        }
        ByteBuffer viewedBuffer = (ByteBuffer) invoke(buffer, methodName);
        return viewedBuffer == null ? buffer : viewed(buffer);
    }

}
