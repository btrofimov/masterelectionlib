package org.boro.example;

import org.boro.melserv.MELService;

public class Main{

  public static void main(String[] args) throws InterruptedException {
      MELService serv = new MELService();
      int i = 0;
      while(true){
          System.out.println("master="+serv.isMaster() );

          Thread.sleep(1000);
          i +=1;
          if(i==120)
              break;
      }
      System.out.println("exiting" );
      serv.shutdown();
      System.out.println("closed" );
  }
}