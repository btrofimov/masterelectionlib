package org.boro.melserv;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

class DefaultHazelcastFactory implements HazelcastFactory{

  @Override
  public HazelcastInstance create(){
    return Hazelcast.newHazelcastInstance();
  }
}