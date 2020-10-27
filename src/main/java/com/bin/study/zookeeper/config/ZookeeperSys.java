package com.bin.study.zookeeper.config;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperSys implements Watcher {

    private static CountDownLatch  connectedSemaphore=new CountDownLatch(1);
    private  static ZooKeeper zk= null;
    private static Stat stat=new Stat();
    private static String userName="/config/userName";

    public static void main(String[] args) throws InterruptedException, KeeperException, IOException {

           zk=new ZooKeeper("localhost:2181",5000,new ZookeeperSys());
        connectedSemaphore.await();//等待ZK连接成功通知
        System.out.println(new String(zk.getData(userName,true,stat)));
        Thread.sleep(80000);


    }


    @Override
    public void process(WatchedEvent watchedEvent) {

        if(Event.KeeperState.SyncConnected==watchedEvent.getState())//zk连接成功事件
        {
            if(Event.EventType.None==watchedEvent.getType() && null==watchedEvent.getPath())
            {
                System.out.println("watchedEvent.getType():"+watchedEvent.getType());
                System.out.println("watchedEvent.getPath():"+watchedEvent.getPath());
                connectedSemaphore.countDown();
            }
            else if(Event.EventType.NodeDataChanged==watchedEvent.getType() )
            {

                try {
                    System.out.println("username已修改为："+new String(zk.getData(userName,true,stat)));
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
