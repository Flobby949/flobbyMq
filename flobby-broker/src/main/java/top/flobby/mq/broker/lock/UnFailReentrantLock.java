package top.flobby.mq.broker.lock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 非公平锁
 * @create : 2024-06-12 17:03
 **/

public class UnFailReentrantLock implements PutMessageLock {

    private final ReentrantLock reentrantLock = new ReentrantLock();

    @Override
    public void lock() {
        reentrantLock.lock();
    }

    @Override
    public void unlock() {
        reentrantLock.unlock();
    }
}
