package org.boro.melserv;

public class MELEngine {
  private MELEngine(HazelcastImpl hazelcastImpl){
      this.hazelcastImpl = hazelcastImpl;
  }
  
  public static MELEngine runInstance(){
      return new MELEngine(new HazelcastImpl(hazelcastFactory.create(),1));
  }
  private static void setHazelcastFactory(HazelcastFactory newFactory) {
    hazelcastFactory = newFactory;
  }
  public static  HazelcastFactory hazelcastFactory = new DefaultHazelcastFactory();

  public boolean isMaster() {
      return hazelcastImpl.isMaster();
  }

  public void shutdown(){
      hazelcastImpl.shutDown();
  }

    HazelcastImpl hazelcastImpl;
}