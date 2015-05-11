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
                return getZooKeeper().create(path, content, ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
            }
        }, this);
    }

    public void delete(final String path) throws Exception {
        RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                getZooKeeper().delete(path, -1);
                return null;
            }
        }, this);
    }

    public void getChildren(final String path, final Boolean isWatcher) throws Exception {
        RetryLoop.retry(new Callable<List<String>>() {
            public List<String> call() throws Exception {
                return getZooKeeper().getChildren(path, isWatcher);
            }
        }, this);
    }

    private ZooKeeper getZooKeeper() {
        return zookeeperHandler.getZooKeeper();
    }

    public long getSessionId() throws Exception {
        return RetryLoop.retry(new Callable<Long>() {
            public Long call() throws Exception {
                return getZooKeeper().getSessionId();
            }
        }, this);
    }

    public byte[] getSessionPasswd() throws Exception {
        return RetryLoop.retry(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return getZooKeeper().getSessionPasswd();
            }
        }, this);
    }

    public int getSessionTimeout() throws Exception {
        return RetryLoop.retry(new Callable<Integer>() {
            public Integer call() throws Exception {
                return getZooKeeper().getSessionTimeout();
            }
        }, this);
    }

    public void addAuthInfo(final String scheme, final byte[] auth) throws Exception {
        RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                getZooKeeper().addAuthInfo(scheme, auth);
                return null;
            }
        }, this);
    }

    public void register(final Watcher watcher) throws Exception {
        RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                getZooKeeper().register(watcher);
                return null;
            }
        }, this);
    }

    public void close() throws InterruptedException, Exception {
        RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                if (!(getZooKeeper().getState() == ZooKeeper.States.CLOSED)) {
                    getZooKeeper().close();
                }
                return null;
            }
        }, this);
    }

    /**
     * TODO PERSISTENT_SEQUENTIAL and EPHEMERAL_SEQUENTIAL can't retry which need to be treated specially
     *
     * @param path
     * @param data
     * @param acl
     * @param createMode
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     * @throws Exception
     */
    public String create(final String path, final byte[] data, final List<ACL> acl, final CreateMode createMode) throws KeeperException, InterruptedException, Exception {
        return RetryLoop.retry(new Callable<String>() {
            public String call() throws Exception {
                return getZooKeeper().create(path, data, acl, createMode);
            }
        }, this);

    }

    public void create(String path, byte[] data, List<ACL> acl, CreateMode createMode, AsyncCallback.StringCallback cb, Object ctx) {

    }

    public void delete(final String path, final int version) throws InterruptedException, KeeperException, Exception {
         RetryLoop.retry(new Callable<Void>() {
            public Void call() throws Exception {
                getZooKeeper().delete(path, version);
                return null;
            }
        }, this);
    }

    public void deleteRecursive(String pathRoot) throws InterruptedException, KeeperException {

    }

    public void deleteRecursive(String pathRoot, AsyncCallback.VoidCallback cb, Object ctx) throws InterruptedException, KeeperException {

    }

    public List<String> listSubTreeBFS(String pathRoot) throws KeeperException, InterruptedException {
        return null;
    }

    public void delete(String path, int version, AsyncCallback.VoidCallback cb, Object ctx) {

    }

    public Stat exists(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return null;
    }

    public Stat exists(String path, boolean watch) throws KeeperException, InterruptedException {
        return null;
    }

    public void exists(String path, Watcher watcher, AsyncCallback.StatCallback cb, Object ctx) {

    }

    public void exists(String path, boolean watch, AsyncCallback.StatCallback cb, Object ctx) {

    }

    public byte[] getData(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return new byte[0];
    }

    public byte[] getData(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return new byte[0];
    }

    public void getData(String path, Watcher watcher, AsyncCallback.DataCallback cb, Object ctx) {

    }

    public void getData(String path, boolean watch, AsyncCallback.DataCallback cb, Object ctx) {

    }

    public Stat setData(String path, byte[] data, int version) throws KeeperException, InterruptedException {
        return null;
    }

    public void setData(String path, byte[] data, int version, AsyncCallback.StatCallback cb, Object ctx) {

    }

    public List<ACL> getACL(String path, Stat stat) throws KeeperException, InterruptedException {
        return null;
    }

    public void getACL(String path, Stat stat, AsyncCallback.ACLCallback cb, Object ctx) {

    }

    public Stat setACL(String path, List<ACL> acl, int version) throws KeeperException, InterruptedException {
        return null;
    }

    public void setACL(String path, List<ACL> acl, int version, AsyncCallback.StatCallback cb, Object ctx) {

    }

    public List<String> getChildren(String path, Watcher watcher) throws KeeperException, InterruptedException {
        return null;
    }

    public List<String> getChildren(String path, boolean watch) throws KeeperException, InterruptedException {
        return null;
    }

    public void getChildren(String path, Watcher watcher, AsyncCallback.ChildrenCallback cb, Object ctx) {

    }

    public void getChildren(String path, boolean watch, AsyncCallback.ChildrenCallback cb, Object ctx) {

    }

    public List<String> getChildren(String path, Watcher watcher, Stat stat) throws KeeperException, InterruptedException {
        return null;
    }

    public List<String> getChildren(String path, boolean watch, Stat stat) throws KeeperException, InterruptedException {
        return null;
    }

    public void getChildren(String path, Watcher watcher, AsyncCallback.Children2Callback cb, Object ctx) {

    }

    public void getChildren(String path, boolean watch, AsyncCallback.Children2Callback cb, Object ctx) {

    }

    public void sync(String path, AsyncCallback.VoidCallback cb, Object ctx) {

    }


}
