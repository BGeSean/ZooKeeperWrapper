package com.mitisky.zookeeper;

import com.mitisky.retry.RetryLoop;
import com.mitisky.retry.RetryPolicy;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Sean on 2015/5/7.
 * this class doesn't extend the zookeeper,but contain a zookeeper handler.
 * it will rewrite all interface of zookeeper client.
 *
 */
public class ZooKeeperWrapper {
    private RetryLoop retryLoop = new RetryLoop();
    private ZooKeeperHandler zookeeperHandler;

    public ZooKeeperWrapper(ZooKeeperHandler zookeeperHandler) {
        this.zookeeperHandler = zookeeperHandler;

    }

    public ZooKeeperHandler getZookeeperHandler() {
        return zookeeperHandler;
    }

    public void initial(RetryPolicy biRetryPolicy) {
        retryLoop.initial(biRetryPolicy, this);
    }

    public RetryLoop getRetryLoop() {
        return retryLoop;
    }

    public void create(final String path, final byte[] content, final CreateMode mode) throws Exception {
        RetryLoop.retry(new Callable<String>() {
            public String call() throws Exception {
                return zookeeperHandler.getZooKeeper().create(path, content, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
            }
        }, this);
    }

    public void delete(final String path) throws Exception {
        RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                zookeeperHandler.getZooKeeper().delete(path, -1);
                return null;
            }
        }, this);
    }

    public void getChildren(final String path, final Boolean isWatcher) throws Exception {
        RetryLoop.retry(new Callable<List<String>>() {
            public List<String> call() throws Exception {
                return zookeeperHandler.getZooKeeper().getChildren(path, isWatcher);
            }
        }, this);
    }
}
