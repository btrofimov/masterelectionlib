package org.boro.melserv;

import java.util.Map;

class MELEngine {
  private MELEngine(){}

  public static MELEngine runInstance(Map<String,Object> params){
      return null;
  }
  
  public boolean isMaster() { return true; }

  public void shutdown(){}

}