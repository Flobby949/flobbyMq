package top.flobby.mq.broker.utils;


import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class MMapUtilTest {

    MMapUtil mMapUtil;
    String filePath = "F:\\flobbyMq\\broker\\store\\order_cancel_topic\\00000000";

    @Test
    public void readContent() throws IOException {
        mMapUtil = new MMapUtil();
        mMapUtil.loadFileInMMap(filePath, 0, 10 * 1024 * 1024);
        String str = "this is a test content";
        byte[] content = str.getBytes();
        mMapUtil.writeContent(content);
        byte[] readContent = mMapUtil.readContent(0, content.length);
        System.out.println(new String(readContent));
    }

    @Test
    public void concurrentWriteTest() throws IOException, InterruptedException {
        MMapUtil mMapUtil = new MMapUtil();
        //映射1mb
        mMapUtil.loadFileInMMap(filePath, 0, 1024 * 1024 * 1);
        CountDownLatch count = new CountDownLatch(1);
        CountDownLatch allWriteSuccess = new CountDownLatch(10);
        for(int i =0;i<10;i++) {
            int finalI = i;
            Thread task = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        count.await();
                        //多线程并发写
                        mMapUtil.writeContent(("test-content-" + finalI).getBytes());
                        allWriteSuccess.countDown();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            task.start();;
        }
        System.out.println("准备执行并发写入mmap测试");
        count.countDown();
        allWriteSuccess.await();
        System.out.println("并发测试完毕,读取文件内容测试");
        byte[] content = mMapUtil.readContent(0,1000);
        System.out.println("内容："+new String(content));
    }
}