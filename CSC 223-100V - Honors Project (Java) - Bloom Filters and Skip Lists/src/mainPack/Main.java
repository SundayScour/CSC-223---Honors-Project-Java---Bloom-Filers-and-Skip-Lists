package mainPack;

import java.util.Scanner;
import java.io.PrintStream;
import java.util.Random;

import mainPack.jCustomClasses.*;
import mainPack.jCustomClasses.JTestBench.*;


public class Main
{

  public static void main(String[] args)
  {
    PrintStream o = new PrintStream(System.out);
    JTheDataSetObject t1;
    JTheDataSetObject t2;
    
    JTestBench b1;
    
    t1 = new JTheDataSetObject(43.21, -21.3, 0, JHashType.GARS);
    t2 = new JTheDataSetObject(-72.0, 64.479927, 100, JHashType.MGRS);
    
    o.println("t1 GARS Hash String: " + t1.toHashString());
    t1.setHashType(JHashType.MGRS);
    o.println("t1 MGRS Hash String: " + t1.toHashString());
    o.println("t1 MGRS: " + t1.getMyMGRS());
    
    o.println();
    
    o.println("t2 MGRS: " + t2.getMyMGRS());
    o.println("t2 MGRS Hash String: " + t2.toHashString());
    t2.setHashType(JHashType.GARS);
    o.println("t2 GARS Hash String: " + t2.toHashString());
    
    o.println();
    o.println(t1.toString());
    o.println(t2.toString());
    
    
    o.close();
  }
}