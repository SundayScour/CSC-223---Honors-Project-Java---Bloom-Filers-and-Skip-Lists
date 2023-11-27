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
    long seed = 0;
    String trash = "";
    boolean goodSeed = false;
    int strt;
    int nd;

    
    JTestBench b1;
    JTestBench b2;
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
    
    testRandFail(seed, o, 25);
    
    myPause();
    
    strt  = 6;
    nd    = 6;
    b1 = new JTestBench(seed, JBloomType.Lovasoa, JGridSysType.GARS, JSkipListType.LP2, strt, nd, 25);
    b1.startup();
    
    myPause();
    
    b2 = new JTestBench(seed, JBloomType.Sangupta, JGridSysType.GARS, JSkipListType.LP2, strt, nd, 25);
    b2.startup();
    
    i.close();
    o.close();
  }
  private static void myPause()
  { 
         System.out.println("Press Enter key to continue...");
         try
         {
             System.in.read();
         }  
         catch(Exception e)
         {}  
  }
  
  private static void testRandFail(long seed, PrintStream o, double failRate)
  {
    Random rng = new Random(seed);
    boolean tf;
    int numRuns = 100_000;
    double trueFailRate = -0.0;
    
    int failCount = 0;
    for (int k = 0; k < numRuns; k++)
    {
      tf = JTestBench.randFail(rng, failRate);
      if (!tf) {failCount++;}
      //o.println(tf);
    }
    trueFailRate  = failCount / (double)numRuns;
    trueFailRate *= 100; // Make a percentage
    
    o.println("Number of Fails:   " + failCount);
    o.println("Number of runs:    " + numRuns);
    o.println("True Failure Rate: " + String.format("%.2f", trueFailRate));
  }

}

// Old Stuff
/*   
JTheDataSetObject t1;
JTheDataSetObject t2;
*/
/*    
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
*/    


/*
--------------------------------------------------------------------------------
----  The blank lines below are to aid me in keeping the last line of this  ----
----  file vertically centered and above the very bottom of my very large   ---- 
----  monitor.                                                              ----
--------------------------------------------------------------------------------
*/


















































































