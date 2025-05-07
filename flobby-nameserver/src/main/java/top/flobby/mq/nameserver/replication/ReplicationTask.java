package top.flobby.mq.nameserver.replication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 复制任务抽象接口
 * @create : 2025-05-07 16:27
 **/

public abstract class ReplicationTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReplicationTask.class);

    private String taskName;

    public ReplicationTask(String taskName) {
        this.taskName = taskName;
    }

    public void startTaskAsync() {
        Thread task = new Thread(() -> {
            LOGGER.info("启动任务：{}", taskName);
            initTask();
        });
        task.setName(taskName);
        task.start();
    }

     abstract void initTask();
}
