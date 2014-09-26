package org.boro.melserv;

/**
 *
 */
public class MELService {

    /**
     *
     */
    MELEngine engine = MELEngine.runInstance();

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