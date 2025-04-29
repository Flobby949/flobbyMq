package top.flobby.mq.broker.utils;

import junit.framework.TestCase;

public class FileContentUtilTest extends TestCase {

    public void testReadFromFile() {
        String content = FileContentUtil.readFromFile("/Users/flobby/IdeaProjects/flobbyMq/broker/commit_log/order_cancel_topic/00000000");
        System.out.println(content);
    }

    public void testOverwriteToFile() {
        FileContentUtil.overwriteToFile(
                "/Users/flobby/IdeaProjects/flobbyMq/broker/commit_log/order_cancel_topic/00000000",
                "write content"
        );

    }
}