package org.boro.melserv;

import java.util.Collections;
import java.util.Map;

/**
 *
 */
public class MELService {

    public MELService(Map<String,Object> params){
        engine = MELEngine.runInstance(params);
    }

    public MELService(){
        Map<String,Object> params = Collections.emptyMap();
        engine = MELEngine.runInstance(params);
    }

    /**
     *
     */
    final MELEngine engine;

    /**
     *
     */
    MEListener listener;

    public void settListener(MEListener listener){
        this.listener = listener;
    }
  
    public boolean isMaster(){
        return engine.isMaster();
    }

    public void shutdown(){
        engine.shutdown();
    }
}