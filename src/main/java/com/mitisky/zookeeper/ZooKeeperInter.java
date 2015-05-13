package com.mitisky.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.util.*;

/**
 * Created by FineSoft on 2015/5/11.
 */
public interface ZooKeeperInter {
    String ZOOKEEPER_CLIENT_CNXN_SOCKET = "zookeeper.clientCnxnSocket";

    long getSessionId();

    byte[] getSessionPasswd();

    int getSessionTimeout();

    void addAuthInfo(String scheme, byte auth[]);

    void register(Watcher watcher);

    void close() throws InterruptedException;

    String create(String path, byte data[], List<ACL> acl,
                  CreateMode createMode)
            throws KeeperException, InterruptedException;

    void create(String path, byte data[], List<ACL> acl,
                CreateMode createMode, AsyncCallback.StringCallback cb, Object ctx);

    void delete(String path, int version)
            throws InterruptedException, KeeperException;

    void deleteRecursive(String pathRoot)
            throws InterruptedException, KeeperException;

    void deleteRecursive(String pathRoot, AsyncCallback.VoidCallback cb,
                         Object ctx)
            throws InterruptedException, KeeperException;

    List<String> listSubTreeBFS(String pathRoot) throws KeeperException, InterruptedException;

    void delete(String path, int version, AsyncCallback.VoidCallback cb,
                Object ctx);

    Stat exists(String path, Watcher watcher)
            throws KeeperException, InterruptedException;

    Stat exists(String path, boolean watch) throws KeeperException,
            InterruptedException;

    void exists(String path, Watcher watcher,
                AsyncCallback.StatCallback cb, Object ctx);

    void exists(String path, boolean watch, AsyncCallback.StatCallback cb, Object ctx);

    byte[] getData(String path, Watcher watcher, Stat stat)
            throws KeeperException, InterruptedException;

    byte[] getData(String path, boolean watch, Stat stat)
            throws KeeperException, InterruptedException;

    void getData(String path, Watcher watcher,
                 AsyncCallback.DataCallback cb, Object ctx);

    void getData(String path, boolean watch, AsyncCallback.DataCallback cb, Object ctx);

    Stat setData(String path, byte data[], int version)
            throws KeeperException, InterruptedException;

    void setData(String path, byte data[], int version,
                 AsyncCallback.StatCallback cb, Object ctx);

    List<ACL> getACL(String path, Stat stat)
            throws KeeperException, InterruptedException;

    void getACL(String path, Stat stat, AsyncCallback.ACLCallback cb,
                Object ctx);

    Stat setACL(String path, List<ACL> acl, int version)
            throws KeeperException, InterruptedException;

    void setACL(String path, List<ACL> acl, int version,
                AsyncCallback.StatCallback cb, Object ctx);

    List<String> getChildren(String path, Watcher watcher)
            throws KeeperException, InterruptedException;

    List<String> getChildren(String path, boolean watch)
            throws KeeperException, InterruptedException;

    void getChildren(String path, Watcher watcher,
                     AsyncCallback.ChildrenCallback cb, Object ctx);

    void getChildren(String path, boolean watch, AsyncCallback.ChildrenCallback cb,
                     Object ctx);

    List<String> getChildren(String path, Watcher watcher,
                             Stat stat)
            throws KeeperException, InterruptedException;

    List<String> getChildren(String path, boolean watch, Stat stat)
            throws KeeperException, InterruptedException;

    void getChildren(String path, Watcher watcher,
                     AsyncCallback.Children2Callback cb, Object ctx);

    void getChildren(String path, boolean watch, AsyncCallback.Children2Callback cb,
                     Object ctx);

    void sync(String path, AsyncCallback.VoidCallback cb, Object ctx);

    States getState();

    @Override
    String toString();

    public enum States {
        CONNECTING, ASSOCIATING, CONNECTED, CLOSED, AUTH_FAILED;

        public boolean isAlive() {
            return this != CLOSED && this != AUTH_FAILED;
        }
    }


}
