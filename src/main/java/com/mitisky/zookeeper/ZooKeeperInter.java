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

    /**
     * Manage watchers & handle events generated by the ClientCnxn object.
     *
     * We are implementing this as a nested class of ZooKeeper so that
     * the public methods will not be exposed as part of the ZooKeeper client
     * API.
     */
    public static class ZKWatchManager implements ClientWatchManager {
        private final Map<String, Set<Watcher>> dataWatches =
                new HashMap<String, Set<Watcher>>();
        private final Map<String, Set<Watcher>> existWatches =
                new HashMap<String, Set<Watcher>>();
        private final Map<String, Set<Watcher>> childWatches =
                new HashMap<String, Set<Watcher>>();

        private volatile Watcher defaultWatcher;

        final private void addTo(Set<Watcher> from, Set<Watcher> to) {
            if (from != null) {
                to.addAll(from);
            }
        }

        /* (non-Javadoc)
         * @see org.apache.zookeeper.ClientWatchManager#materialize(Event.KeeperState, Event.EventType, java.lang.String)
         */
        @Override
        public Set<Watcher> materialize(Watcher.Event.KeeperState state,
                                        Watcher.Event.EventType type,
                                        String clientPath)
        {
            Set<Watcher> result = new HashSet<Watcher>();

            switch (type) {
                case None:
                    result.add(defaultWatcher);
                    for(Set<Watcher> ws: dataWatches.values()) {
                        result.addAll(ws);
                    }
                    for(Set<Watcher> ws: existWatches.values()) {
                        result.addAll(ws);
                    }
                    for(Set<Watcher> ws: childWatches.values()) {
                        result.addAll(ws);
                    }

                    // clear the watches if auto watch reset is not enabled
                    if (ClientCnxn.getDisableAutoResetWatch() &&
                            state != Watcher.Event.KeeperState.SyncConnected)
                    {
                        synchronized(dataWatches) {
                            dataWatches.clear();
                        }
                        synchronized(existWatches) {
                            existWatches.clear();
                        }
                        synchronized(childWatches) {
                            childWatches.clear();
                        }
                    }

                    return result;
                case NodeDataChanged:
                case NodeCreated:
                    synchronized (dataWatches) {
                        addTo(dataWatches.remove(clientPath), result);
                    }
                    synchronized (existWatches) {
                        addTo(existWatches.remove(clientPath), result);
                    }
                    break;
                case NodeChildrenChanged:
                    synchronized (childWatches) {
                        addTo(childWatches.remove(clientPath), result);
                    }
                    break;
                case NodeDeleted:
                    synchronized (dataWatches) {
                        addTo(dataWatches.remove(clientPath), result);
                    }
                    // XXX This shouldn't be needed, but just in case
                    synchronized (existWatches) {
                        Set<Watcher> list = existWatches.remove(clientPath);
                        if (list != null) {
                            addTo(existWatches.remove(clientPath), result);
                            LOG.warn("We are triggering an exists watch for delete! Shouldn't happen!");
                        }
                    }
                    synchronized (childWatches) {
                        addTo(childWatches.remove(clientPath), result);
                    }
                    break;
                default:
                    String msg = "Unhandled watch event type " + type
                            + " with state " + state + " on path " + clientPath;
                    LOG.error(msg);
                    throw new RuntimeException(msg);
            }

            return result;
        }
    }
}
