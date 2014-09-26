package org.boro.melserv;

import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class MELEngine {
    private MELEngine(HazelcastImpl hazelcastImpl){
        this.hazelcastImpl = hazelcastImpl;
    }

    public static final String HAZELCAST_INSTANCE = "HAZELCAST_INSTANCE";
    public static final String THREAD_DELAY = "THREAD_DELAY";

    public static MELEngine runInstance(Map<String,Object> params){
        HazelcastInstance instance = (HazelcastInstance) params.get(HAZELCAST_INSTANCE);
        if(instance==null)
            instance = new DefaultHazelcastFactory().create();
        Integer delay = (Integer) params.get(THREAD_DELAY);
        if(delay==null)
            delay = 1; // 1 minute, default value
        return new MELEngine(new HazelcastImpl(instance, delay));
    }

    public boolean isMaster() {
        return hazelcastImpl.isMaster();
    }

    public void shutdown(){
        hazelcastImpl.shutDown();
    }

    HazelcastImpl hazelcastImpl;
}