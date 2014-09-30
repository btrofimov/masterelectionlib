package org.boro.melserv;

import com.hazelcast.core.HazelcastInstance;

import java.util.Map;

public class MELEngine {
    private MELEngine(HazelcastInstance instance, boolean localInstance){
        this.instance = instance;
        this.localInstance = localInstance;
    }

    public static final String HAZELCAST_INSTANCE = "HAZELCAST_INSTANCE";

    public static MELEngine runInstance(Map<String,Object> params){
        HazelcastInstance instance = (HazelcastInstance) params.get(HAZELCAST_INSTANCE);
        boolean localInstance = instance==null;
        if(instance==null)
            instance = new DefaultHazelcastFactory().create();
        return new MELEngine(instance, localInstance);
    }

    public boolean isMaster() {
        return instance.getCluster().getMembers().iterator().next().localMember();
    }

    public void shutdown(){
        // if we are responsible for hazelcast instance then we have to shutudown it
        if(localInstance)
            instance.shutdown();
    }

    HazelcastInstance instance;
    boolean localInstance;
}