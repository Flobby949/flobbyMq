package top.flobby.mq.nameserver.replication;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.flobby.mq.common.coder.TcpMsg;
import top.flobby.mq.common.enums.NameServerEventCodeEnum;
import top.flobby.mq.nameserver.cache.CommonCache;
import top.flobby.mq.nameserver.event.model.ReplicationMsgEvent;

import java.util.Map;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : 主从同步专用的数据发送任务
 * @create : 2025-05-07 14:49
 **/

public class MasterReplicationMsgSendTask extends ReplicationTask{
    public static final Logger LOGGER = LoggerFactory.getLogger(MasterReplicationMsgSendTask.class);

    public MasterReplicationMsgSendTask(String taskName) {
        super(taskName);
    }

    @Override
    public void initTask() {
            try {
                while (true) {
                    ReplicationMsgEvent event = CommonCache.getReplicationMsgQueueManager().getReplicationQueue().take();
                    byte[] body = JSON.toJSONBytes(event);
                    Map<String, ChannelHandlerContext> ctxMap =
                            CommonCache.getReplicationChannelManager().getChannelHandlerContextMap();
                    /*
                     * sync - 同步复制
                     * async - 异步复制
                     * 半同步复制
                     */
                    for (ChannelHandlerContext ctx : ctxMap.values()) {
                        LOGGER.info("主节点发送数据：{}", JSON.toJSONString(event));
                        TcpMsg msg = new TcpMsg(NameServerEventCodeEnum.MASTER_REPLICATION_MSG.getCode(), body);
                        ctx.writeAndFlush(msg);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("发送数据异常：{}", e.getMessage());
            }
    }
}
