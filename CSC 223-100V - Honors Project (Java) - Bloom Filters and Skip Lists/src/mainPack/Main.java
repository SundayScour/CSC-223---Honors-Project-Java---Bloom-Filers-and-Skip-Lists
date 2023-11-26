/**
 * @Project: CSC 223-100V - Honors Project (Java) - Bloom Filters and Skip Lists
 * 
 * @FileName:                 Main.java
 * @OriginalFileCreationDate: Nov 25, 2023
 * 
 * @Author:           Jonathan Wayne Edwards
 * @GitHubUserName:   SundayScour
 * @GutHubUserEmail:  sunday_scour@aol.com
 * 
 * @EnclosingPackage: mainPack
 * @ClassName:        Main
 */

package mainPack;

import java.util.Scanner;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Random;

import mainPack.jCustomClasses.*;
import mainPack.jCustomClasses.JTestBench.*;


public class Main
{

  public static void main(String[] args)
  {
    PrintStream o = new PrintStream(System.out);
    Scanner     i = new Scanner(System.in);
    
    JTheDataSetObject t1;
    JTheDataSetObject t2;
    
    JTestBench b1;
    
    t1 = new JTheDataSetObject(43.21, -21.3, 0, JHashType.GARS);
    t2 = new JTheDataSetObject(-72.0, 64.479927, 100, JHashType.MGRS);
    
    long seed = 0;
    String trash = "";
    boolean goodSeed = false;
    
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
    
    while (!goodSeed)
    {
      try
      {
        o.println();
        o.println("**********");
        o.println();
        o.println("Type in the seed to initialize Random(): ");
        o.println();
        o.print("Seed = ");

        seed = i.nextLong();
        goodSeed = true;
      }
      catch (InputMismatchException iME)
      {
        o.println();
        o.println("Incorrect input. Please, try again.");
        trash = i.next(); // Get rid of bad input
        
        goodSeed = false;
        continue;
      }
    }
    
    b1 = new JTestBench(seed);
    
    i.close();
    o.close();
  }
}