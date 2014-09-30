package org.boro.melserv;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

class MELEngine {
  private MELEngine(){}

  public static MELEngine runInstance(Map<String,Object> params){
      throw new NotImplementedException();
  }
  
  public boolean isMaster() { return true; }

  public void shutdown(){}

}