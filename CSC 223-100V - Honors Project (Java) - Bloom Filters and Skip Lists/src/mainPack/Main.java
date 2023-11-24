package mainPack;

import java.util.Scanner;
import java.io.PrintStream;
import java.util.Random;
import mainPack.jCustomClasses.JTheDataSetObject;
import mainPack.jCustomClasses.JTheDataSetObject.JHashType;


public class Main
{

  public static void main(String[] args)
  {
    PrintStream o = new PrintStream(System.out);
    JTheDataSetObject t1;
    JTheDataSetObject t2;
    
    t1 = new JTheDataSetObject(43.21, -21.3, 0, JHashType.GARS);
    t2 = new JTheDataSetObject(-72.0, 64.479927, 100, JHashType.MGRS);
    
    o.println("t1 Hash String: " + t1.toHashString());
    o.println("t2 Hash String: " + t2.toHashString());
    o.close();
  }

}
