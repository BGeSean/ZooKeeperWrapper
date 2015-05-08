package com.mitisky.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * Created by Sean on 2015/5/7.
 * zookeeper handler.care about the connection with zookeeper server.
 */
public class ZooKeeperHandler {
    private ZooKeeper zkHandler;
    private ZooKeeperConfig config;
    private Watcher watcher;

    public ZooKeeper getZooKeeper() {
        if (zkHandler.getState() == ZooKeeper.States.CLOSED) {
            System.out.println("��ǰZookeeper�ͻ���״̬Ϊ��" + zkHandler.getState() + "��������������");
            reconnect();
        }
        waitUntilConnected(zkHandler);
        System.out.println("��ǰZookeeper�ͻ���״̬Ϊ��" + zkHandler.getState());
        return zkHandler;
    }

    public ZooKeeperHandler(ZooKeeperConfig config, Watcher watcher) {
        connect(config, watcher);

    }

    public void reconnect(ZooKeeperConfig config, Watcher watcher) {
        connect(config, watcher);
    }

    public void reconnect() {
        System.out.println("������������");
        connect(config, watcher);
    }

    private void connect(ZooKeeperConfig config, Watcher watcher) {
        try {
            zkHandler = new ZooKeeper(config.getConnectString(), config.getTickTime(), watcher);
            this.config = config;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void waitUntilConnected(ZooKeeper zooKeeper) {
        CountDownLatch connectedLatch = new CountDownLatch(1);
        Watcher watcher = new ConnectedWatcher(connectedLatch);
        zooKeeper.register(watcher);
        if (ZooKeeper.States.CONNECTING == zooKeeper.getState()) {
            try {
                connectedLatch.await();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    static class ConnectedWatcher implements Watcher {

        private CountDownLatch connectedLatch;

        ConnectedWatcher(CountDownLatch connectedLatch) {
            this.connectedLatch = connectedLatch;
        }

        public void process(WatchedEvent event) {
            if (event.getState() == Event.KeeperState.SyncConnected) {
                connectedLatch.countDown();
            }
        }
    }

}
