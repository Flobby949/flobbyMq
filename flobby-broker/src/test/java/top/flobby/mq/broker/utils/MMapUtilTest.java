package top.flobby.mq.broker.utils;


import org.junit.Test;

import java.io.IOException;

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
}