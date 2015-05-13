package com.mitisky.zookeeper;

import com.mitisky.retry.RetryLoop;
import com.mitisky.retry.RetryPolicy;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Sean on 2015/5/7.
 * this class doesn't extend the zookeeper,but contain a zookeeper handler.
 * it will rewrite all interface of zookeeper client.
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

    public String create(final String path, final byte[] content, final List<ACL> acl, final CreateMode mode) throws Exception {
        RetryLoop.retry(new Callable<String>() {
            public String call() throws Exception {
                return zookeeperHandler.getZooKeeper().create(path, content, acl, mode);
            }
        }, this);
        return null;
    }

    public void delete(final String path, final int version) throws Exception {
        RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                zookeeperHandler.getZooKeeper().delete(path, version);
                return null;
            }
        }, this);
    }

    public List<String> getChildren(final String path, final Boolean isWatcher) throws Exception {
        RetryLoop.retry(new Callable<List<String>>() {
            public List<String> call() throws Exception {
                return zookeeperHandler.getZooKeeper().getChildren(path, isWatcher);
            }
        }, this);
        return null;
    }

    public Stat exists(final String path, final Boolean isWatcher) throws Exception {
        RetryLoop.retry(new Callable<Stat>() {
            public Stat call() throws Exception {
                return zookeeperHandler.getZooKeeper().exists(path, isWatcher);
            }
        }, this);
        return null;
    }

    public Stat exists(final String path, final Watcher watcher) throws Exception {
        RetryLoop.retry(new Callable<Stat>() {
            public Stat call() throws Exception {
                return zookeeperHandler.getZooKeeper().exists(path, watcher);
            }
        }, this);
        return null;
    }

    public Stat setData(final String path, final byte data[], final int version) throws Exception {
        RetryLoop.retry(new Callable<Stat>() {
            public Stat call() throws Exception {
                return zookeeperHandler.getZooKeeper().setData(path, data, version);
            }
        }, this);
        return null;
    }

    public byte[] getData(final String path, final boolean watch, final Stat stat) throws Exception {
        RetryLoop.retry(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return zookeeperHandler.getZooKeeper().getData(path, watch, stat);
            }
        }, this);
        return null;
    }

    public byte[] getData(final String path, final Watcher watcher, final Stat stat) throws Exception {
        RetryLoop.retry(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return zookeeperHandler.getZooKeeper().getData(path, watcher, stat);
            }
        }, this);
        return null;
    }

    public Long getSessionId() throws Exception {
        RetryLoop.retry(new Callable<Long>() {
            public Long call() throws Exception {
                return zookeeperHandler.getZooKeeper().getSessionId();
            }
        }, this);
        return null;
    }

}
