package org.boro.melserv;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.IOException;
import java.util.*;

/**
 * Another Implementation based on Zookeeper and cache-driven approach
 */
class ZookeperImpl {

    private static final Logger logger = LoggerFactory.getLogger(ZookeperImpl.class);


    private String getMD5(String data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    private String getUniqueID(){
        return getMD5(String.valueOf(startTime));//getFirstNetworkInterface()
    }

    /**
     *
     */
    private static final String ZK_ROOT = "/tmp/masterelection";

    /**
     *
     */
    private static final String ZK_CREATE_DATE = "createDate";
    private static final String ZK_KEEPALIVE_DATE = "keepaliveDate";

    /**
     *
     */
    private Thread nodeThread;
    /**
     *
     */
    private int masterSleepTime = 5;
    /**
     *
     */
    ZooKeeper zk;
    /**
     *
     */
    String id;
    /**
     *
     */
    String masterId;

    long startTime;

    public ZookeperImpl(String host, Integer port, int masterSleepTime) {
        try {
            this.masterSleepTime = masterSleepTime;
            zk = new ZooKeeper(host, port, null);
            this.startTime = (new Date()).getTime();
            this.id = getUniqueID();
            logger.info("unique id for this instance is {}", this.id);
            init();
        } catch (IOException e) {
            throw new RuntimeException("cannot connect to zk server", e);
        }
    }

    /**
     *
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void initializeHazelcastEntryPoint() throws KeeperException, InterruptedException {
        // update keep alive date for this node
        long now = (new Date()).getTime();
        updateNode(id, new Pair(startTime, now));
    }

    /**
     *
     * @param id
     * @param value
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void updateNode(String id, Pair<Long,Long> value) throws KeeperException, InterruptedException {
        zk.setData(ZK_ROOT + "/" + id + "/" + ZK_CREATE_DATE, value.getFirst().toString().getBytes(), -1);
        zk.setData(ZK_ROOT + "/" + id + "/" + ZK_KEEPALIVE_DATE, value.getSecond().toString().getBytes(), -1);
    }

    /**
     *
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    private Map<String, Pair<Long,Long>> getMembers() throws KeeperException, InterruptedException {
        Map<String, Pair<Long,Long>> ret = new HashMap<>();
        List<String> ids = zk.getChildren(ZK_ROOT ,null);
        for(String id : ids){
            Long createDate = Long.valueOf(new String(zk.getData(ZK_ROOT + "/" + id + "/" + ZK_CREATE_DATE, null , new Stat())));
            Long keepAliveDate = Long.valueOf(new String(zk.getData(ZK_ROOT + "/" + id + "/" + ZK_KEEPALIVE_DATE, null , new Stat())));
            ret.put(id, new Pair(createDate,keepAliveDate));
        }
        return ret;
    }

    private void init(){

        nodeThread = new Thread(new Runnable() {
    	@Override
    	public void run() {
    	    while(!Thread.currentThread().isInterrupted()){
    		try {

                initializeHazelcastEntryPoint();
                Map<String, Pair<Long,Long>> members = getMembers();

                long now = (new Date()).getTime();

                List<String> deadNodes = new LinkedList<String>();
                //clean up dead nodes
                for( String i : members.keySet()){
                    Long val = members.get(i).getFirst();
                    if( (now - val) > (masterSleepTime * 2 * 1000))
                        deadNodes.add(i);
                }
                for(String i : deadNodes){
                    members.remove(i);
                    zk.delete(ZK_ROOT + "/" + id, -1);
                }

                //elect master assuming that all nodes in cache are alive
                String eldestNodeId = null;
                Long eldestNodeVal = null;
                for( String i : members.keySet()){
                    Long val = members.get(i).getSecond();

                    if(eldestNodeId ==null || val < eldestNodeVal){
                        eldestNodeId = i;
                        eldestNodeVal = val;
                    }

                }
                // update internal cache field with just elected eldest node
                masterId = eldestNodeId;

                Thread.sleep(masterSleepTime * 1000);
	    	}catch (InterruptedException e) {
	    	    logger.info("InterruptedException. Exiting cycle");
	    	    Thread.currentThread().interrupt();
	    	    break;
	    	} catch (KeeperException e) {
                e.printStackTrace();
            } finally{
	    	    //logger.info("finally master->false");
	    	}
	        }
    	 }
         });
	 nodeThread.start();
    }
    
    public boolean isMaster() {
        return id.equals(masterId);
    }

    public void shutDown() {
	nodeThread.interrupt();
	try {
	    nodeThread.join();
	} catch (InterruptedException e) {
	    logger.error("Thread not joined properly. {}", e);
	}
    }
}
