package org.boro.melserv;

import com.hazelcast.core.HazelcastInstance;

interface HazelcastFactory{
  HazelcastInstance create();
}