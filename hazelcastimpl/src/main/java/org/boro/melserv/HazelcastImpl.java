package org.boro.melserv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.ILock;

/**
 * The Basic Implementation based on Hazelcast and lock-driven approach
 */
class HazelcastImpl{

    private static final Logger logger = LoggerFactory.getLogger(HazelcastImpl.class);
    private static final String HAZLECAST_SERVICE_LOCK = "distributed_lock";


    volatile private Boolean master = false;
    private Thread nodeThread;
    private int masterSleepTime = 5;
    HazelcastInstance hazelcastInstance;

    public HazelcastImpl(HazelcastInstance hazelcastInstance, int masterSleepTime){
        this.masterSleepTime = masterSleepTime;
        this.hazelcastInstance = hazelcastInstance;
        init();
    }

    private void init(){
	    logger.info("Initializing cluster manager: {}", this);
	    final ILock lock = hazelcastInstance.getLock(HAZLECAST_SERVICE_LOCK);
        nodeThread = new Thread(new Runnable() {
    	@Override
    	public void run() {
    	    while(!Thread.currentThread().isInterrupted()){
                try {
                    logger.info("Acquaring lock, master = {}", master);
                lock.lock();
                logger.info("Lock acquared");
                master = true;
                logger.info("master = {}", master);
                logger.info("waiting");
                    Thread.sleep(masterSleepTime*60*1000);
                    logger.info("unlocking");
                    lock.unlock();
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
                    master = false;
                    logger.info("finally master->false");
                }
            }}
                });
	    nodeThread.start();
    }
    
    public boolean isMaster() {
	    logger.info("{} is master = {}", this.toString(), master);
	    return master;
    }

    public void shutDown() {
	    nodeThread.interrupt();
	    try {
	        nodeThread.join();
            logger.info("Thread joined properly.");
	    } catch (InterruptedException e) {
	        logger.error("Thread not joined properly. {}", e);
    	}
    }
}
