package top.flobby.mq.broker.lock;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author : Flobby
 * @program : flobbyMq
 * @description : 自旋间隙锁
 * @create : 2024-06-12 17:03
 **/

public class SpinLock implements PutMessageLock {

    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public void lock() {
        do {
            index.incrementAndGet();
            if (index.get() == 1) {
                return;
            }
        } while (true);
    }

    @Override
    public void unlock() {
        index.decrementAndGet();
    }
}
