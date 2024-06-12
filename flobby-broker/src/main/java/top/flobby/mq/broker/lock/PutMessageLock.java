package top.flobby.mq.broker.lock;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 写入消息锁
 * @create : 2024-06-12 17:00
 **/

public interface PutMessageLock {

    /**
     * 加锁
     */
    void lock();

    /**
     * 解锁
     */
    void unlock();
}
