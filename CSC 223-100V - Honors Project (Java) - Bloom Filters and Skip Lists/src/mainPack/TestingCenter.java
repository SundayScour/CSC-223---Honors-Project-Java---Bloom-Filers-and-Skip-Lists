/**
 * 
 */
package mainPack;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Scanner;

import mainPack.jCustomClasses.JBloomType;
import mainPack.jCustomClasses.JGridSysType;
import mainPack.jCustomClasses.JSkipListType;
import mainPack.jCustomClasses.JTestBench;

/**
 * 
 */
public class TestingCenter
{
  public static void doTesting()
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
    JTestBench b3;
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
    
    final int sizeAll = 4;
    
    strt  = sizeAll;
    nd    = sizeAll;
    b1 = new JTestBench(seed, JBloomType.Lovasoa, JGridSysType.GARS, JSkipListType.LP2, strt, nd, 25);
    b1.startup();
    
    
    b2 = new JTestBench(seed, JBloomType.Sangupta, JGridSysType.GARS, JSkipListType.LP2, strt, nd, 25);
    b2.startup();
    
    
    b3 = new JTestBench(seed, JBloomType.R_Tree, JGridSysType.GARS, JSkipListType.LP2, strt, nd, 25);
    b3.startup();
    
    i.close();
    o.close();    
  }
  
  public static void myPause()
  { 
         System.out.println("Press Enter key to continue...");
         try
         {
             System.in.read();
         }  
         catch(Exception e)
         {}  
  }
}
