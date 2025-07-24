package top.flobby.mq.common.remote;

import top.flobby.mq.common.cache.NameServerSyncFutureManager;

import java.util.concurrent.*;

/**
 * @author : flobby
 * @program : flobbyMq
 * @description : rpc调用封装
 * @create : 2025-07-21 10:17
 **/

public class SyncFuture implements Future {

    /**
     * rpc响应
     */
    private Object response;
    private String msgId;

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
        countDownLatch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        // 响应不为空，则返回true
        return response != null;
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        try {
            countDownLatch.await();
            return response;
        } finally {
            NameServerSyncFutureManager.remove(msgId);
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        try {
            countDownLatch.await(timeout, unit);
            return response;
        } finally {
            NameServerSyncFutureManager.remove(msgId);
        }
    }
}
