package top.flobby.mq.broker.utils;


import org.junit.jupiter.api.Test;

public class FileContentUtilTest {

    @Test
    public void testReadFromFile() {
        String content = FileContentUtil.readFromFile("/Users/flobby/IdeaProjects/flobbyMq/broker/commit_log/order_cancel_topic/00000000");
        System.out.println(content);
    }

    @Test
    public void testOverwriteToFile() {
        FileContentUtil.overwriteToFile(
                "/Users/flobby/IdeaProjects/flobbyMq/broker/commit_log/order_cancel_topic/00000000",
                "write content"
        );

    }
}