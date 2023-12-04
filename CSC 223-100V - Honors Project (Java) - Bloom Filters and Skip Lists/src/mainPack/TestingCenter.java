/**
 * 
 */
package mainPack;

import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Random;
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
    String input = "";
    String inputCaps = "";
    boolean goodSeed = false;

    
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
        o.println("Type in the seed to initialize Random() or \"r\" for a random seed: ");
        o.println();
        o.print("Seed = ");

        seed = i.nextLong();
        goodSeed = true;
      }
      catch (InputMismatchException iME)
      {
        input = i.next();
        inputCaps = input.toUpperCase();
        if (inputCaps.intern() == "R")
        {
          Random tempR = new Random();
          seed = tempR.nextLong();
          goodSeed = true;
          o.println("The Seed will be random.");
        }
        else
        {
          o.println();
          o.println("Try again.");
          goodSeed = false;
          continue;
        }
      }
    }
    
    o.println("The seed for this run will be " + seed);
    o.println();
    
    final int sizeAll = 1_000;
    
    b1 = new JTestBench(seed, JBloomType.Lovasoa, JGridSysType.GARS, JSkipListType.LP2, sizeAll, 75);
    b1.startup();
    
    
    b2 = new JTestBench(seed, JBloomType.Sangupta, JGridSysType.GARS, JSkipListType.LP2, sizeAll, 75);
    b2.startup();
    
    
    b3 = new JTestBench(seed, JBloomType.R_Tree, JGridSysType.GARS, JSkipListType.LP2, sizeAll, 75);
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
