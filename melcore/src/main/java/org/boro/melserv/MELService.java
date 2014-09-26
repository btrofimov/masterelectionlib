package org.boro.melserv;

import java.util.Collections;
import java.util.Map;

/**
 *
 */
public class MELService {

    Map<String,Object> params;

    public MELService(Map<String,Object> params){
        this.params = params;
    }

    public MELService(){
        this.params = Collections.emptyMap();
    }

    /**
     *
     */
    MELEngine engine = MELEngine.runInstance(params);

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