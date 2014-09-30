package org.boro.melserv;

import java.util.Map;

public class MELEngine {
    private MELEngine(ZookeperImpl zkImpl){
        this.zktImpl = zkImpl;
    }

    public static final String ZK_HOST = "ZK_HOST";
    public static final String ZK_PORT = "ZK_PORT";
    public static final String THREAD_DELAY = "THREAD_DELAY";

    public static MELEngine runInstance(Map<String,Object> params){
        String host = (String) params.get(ZK_HOST);
        Integer port = (Integer) params.get(ZK_PORT);

        Integer delay = (Integer) params.get(THREAD_DELAY);
        if(delay==null)
            delay = 1; // 1 minute, default value
        return new MELEngine(new ZookeperImpl(host, port, delay));
    }

    public boolean isMaster() {
        return zktImpl.isMaster();
    }

    public void shutdown(){
        zktImpl.shutDown();
    }

    ZookeperImpl zktImpl;
}