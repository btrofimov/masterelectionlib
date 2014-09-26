package org.boro.melserv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Another Implementation based on Hazelcast and cache-driven approach
 */
class HazelcastImpl{


    private byte[] getFirstNetworkInterface(){
        try {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (NetworkInterface netint : Collections.list(nets)){
                out.write( netint.getHardwareAddress());
            }
            out.close();
            return out.toByteArray();
        } catch (SocketException e) {
            throw new RuntimeException("InterfaceReader", e);
        } catch (IOException e) {
            throw new RuntimeException("InterfaceReader", e);
        }
    }

    private String getMD5(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data);
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
        return getMD5(getFirstNetworkInterface());
    }

    private static final Logger logger = LoggerFactory.getLogger(HazelcastImpl.class);
    private static final String HAZLECAST_SERVICE_MAP1 = "distributed_map1";
    private static final String HAZLECAST_SERVICE_MAP2 = "distributed_map2";


    private Thread nodeThread;
    private int masterSleepTime = 5;
    HazelcastInstance hazelcastInstance;
    String id;
    String masterId;
    public HazelcastImpl(HazelcastInstance hazelcastInstance, int masterSleepTime){
        this.masterSleepTime = masterSleepTime;
        this.hazelcastInstance = hazelcastInstance;
        this.id = getUniqueID();
        init();
    }


    private void initializeHAzelcastEntryPoint(){
        long now = (new Date()).getTime();
        final Map<String, Long> map1 = hazelcastInstance.getMap(HAZLECAST_SERVICE_MAP1);
        map1.put(id, now);
        final Map<String, Long> map2 = hazelcastInstance.getMap(HAZLECAST_SERVICE_MAP1);
        map2.put(id, now);
    }

    private void init(){
        initializeHAzelcastEntryPoint();

        nodeThread = new Thread(new Runnable() {
    	@Override
    	public void run() {
    	    while(!Thread.currentThread().isInterrupted()){
    		try {
                final Map<String, Long> map1 = hazelcastInstance.getMap(HAZLECAST_SERVICE_MAP1);
                final Map<String, Long> map2 = hazelcastInstance.getMap(HAZLECAST_SERVICE_MAP2);

                long now = (new Date()).getTime();

                /// update keep alive date for this node
                map2.put(id, now);

                List<String> deadNodes = new LinkedList<String>();
                //clean up dead nodes
                for( String i : map2.keySet()){
                    Long val = map2.get(i);
                    if( now - val > masterSleepTime * 60 * 1000)
                        deadNodes.add(i);
                }
                for(String i : deadNodes){
                    map1.remove(i);
                    map2.remove(i);
                }

                //elect master id assuming that all nodes in cache are alive
                String eldestNodeId = null;
                Long eldestNodeVal = null;
                for( String i : map1.keySet()){
                    Long val = map1.get(i);

                    if(eldestNodeId ==null || val < eldestNodeVal){
                        eldestNodeId = i;
                        eldestNodeVal = val;
                    }

                }
                // update internal cache field with just elected eldest node
                masterId = eldestNodeId;

                Thread.sleep(masterSleepTime * 60 * 1000);
	    	}catch (InterruptedException e) {
	    	    logger.info("InterruptedException. Exiting cycle");
	    	    Thread.currentThread().interrupt();
	    	    break;
	    	}
	    	catch(HazelcastInstanceNotActiveException e){
	    	    logger.info("HazelcastInstanceNotActiveException. Exiting cycle");
    			Thread.currentThread().interrupt();
	    		break;
		    }
	    	finally{
	    	    logger.info("finally master->false");
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
