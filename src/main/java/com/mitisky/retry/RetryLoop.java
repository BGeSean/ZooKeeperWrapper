package com.mitisky.retry;

import com.mitisky.zookeeper.ZooKeeperWrapper;
import org.apache.zookeeper.KeeperException;

import java.util.concurrent.Callable;

/**
 * Created by Sean on 2015/5/7.
 * implement the retry loop
 */
public class RetryLoop {
    private boolean isDone;
    private RetryPolicy retryPolicy;
    private long startTime;
    private int retryCount;
    private Sleeper sleeper = new Sleeper();
    private ZooKeeperWrapper wrapper;

    public RetryLoop() {
        reset();
    }

    public void initial(RetryPolicy policy, ZooKeeperWrapper wrapper) {
        this.retryPolicy = policy;
        this.wrapper = wrapper;
    }


    /**
     * �ж��Ƿ����ִ��
     *
     * @return
     */
    public boolean shouldRetry() {
        return !isDone;
    }


    /**
     * ǿ�ƽ�����ǰ����
     */
    public void markComplete() {
        isDone = true;
    }

    public void reset() {
        retryCount = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * �����쳣
     *
     * @param exception
     * @throws Exception
     */
    public void takeException(Exception exception) throws Exception {
        boolean throwException = false;
        if (exception instanceof KeeperException.ConnectionLossException) {
            System.out.println("���Ӷ�ʧ");
//            wrapper.getZookeeperHandler().reconnect();
        }
        if (retryPolicy.allowRetry(retryCount++, System.currentTimeMillis() - startTime, sleeper)) {
            throwException = false;
        } else {
            throwException = true;
        }
        if (throwException) {
            throw exception;
        }
    }

    public static <T> T retry(Callable<T> proc, ZooKeeperWrapper zookeeperWrapper) throws Exception {
        RetryLoop retryLoop = zookeeperWrapper.getRetryLoop();
        T result = null;
        retryLoop.reset();
        while (retryLoop.shouldRetry()) {
            try {
                result = proc.call();
                retryLoop.markComplete();
            } catch (Exception ex) {
                ex.printStackTrace();
                retryLoop.takeException(ex);
            }
        }
        return result;

    }
}
