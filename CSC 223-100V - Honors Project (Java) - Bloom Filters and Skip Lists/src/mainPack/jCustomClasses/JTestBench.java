/**
 * @Project: CSC 223-100V - Honors Project (Java) - Bloom Filters and Skip Lists
 * 
 * @FileName:                 JTestBench.java
 * @OriginalFileCreationDate: Nov 25, 2023
 * 
 * @Author:           Jonathan Wayne Edwards
 * @GitHubUserName:   SundayScour
 * @GutHubUserEmail:  sunday_scour@aol.com
 * 
 * @EnclosingPackage: mainPack.jCustomClasses
 * @ClassName:        JTestBench
 */

package mainPack.jCustomClasses;

import java.util.Iterator;
import java.util.ListIterator;
/**
 * Java library imports
 */
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * Imported Implementations
 */

// Global Area Reference System
import importedImplementations.GARS.LLtoGARS;
import importedImplementations.GARS.GARS;
import importedImplementations.GARS.GARStoLL;

// Military Grid Reference System
import importedImplementations.NGA_MGRS.mgrs.MGRS;

// Lovasoa Bloom Filter
import importedImplementations.lovasoaBloom.LovaBloomFilter;

// Long Project 2 Skip List
import importedImplementations.RahulLP2SkipList.SkipList;

// Sangupta Bloom Filter
import importedImplementations.SanguptaBloom.bloomfilter.BloomFilter;
import importedImplementations.SanguptaBloom.bloomfilter.impl.InMemoryBloomFilter;
import importedImplementations.SanguptaBloom.bloomfilter.AbstractBloomFilter;

// TinSpin - Indexes: R-Tree
import importedImplementations.TinSpinIndexes.index.rtree.RTree;

// My own work
import mainPack.jCustomClasses.*;

/**
 * 
 */
public class JTestBench
{
  /**
   * Limit constants
   */
  public static final byte MIN_POWER = 1;
  public static final byte MAX_POWER = 8;
  
  // Is this a valid instatnce of JTestBench?
  private boolean isValid = false;  
  
  // The bean for counting
  private ThreadMXBean myBenchBean = null;
  
  /**
   * Timers for each phase of testing
   */
  private long timeCreateStart  = 0;
  private long timeCreateEnd    = 0;
  private long timeCreateTotal  = 0;
  
  private long timeVerifyStart  = 0;
  private long timeVerifyEnd    = 0;
  private long timeVerifyTotal  = 0;
  
  private long timeModifyStart  = 0;
  private long timeModifyEnd    = 0;
  private long timeModifyTotal  = 0;
  
  /**
   * Configuration options for each created instance of JTestBench
   */
  
  // The data structure implementations to use
  private JBloomType    myBloom = JBloomType.Lovasoa;
  private JGridSysType  myGrid  = JGridSysType.GARS;
  private JSkipListType mySkip  = JSkipListType.LP2;
  private JBenchSetType myBenchSet = JBenchSetType.LBloomGARSLP2;
  
  private int           myFailRate; // Percentage of Test Set objects that are NOT in The Data Set
  private double        myFRate;
  
  LovaBloomFilter                         lBF;        // Lovasoa Bloom Filter for this Bench
  InMemoryBloomFilter<JTheDataSetObject>  sBF;        // Sangupta Bloom Filter for this Bench
  SkipList<JTheDataSetObject>             sL;         // Skip List for this Bench
  Vector<JTheDataSetObject>               TestSet;    // Test Set for this Bench
  
  PrintStream ot;
  Scanner     n;
  String      trash;

  
  // The orders of magnitude of the size of The Data Set and Test Set. They will all be tried and times recorded.
  private byte powersOf10Start  = 0;
  private byte powersOf10End    = 0;
  private byte powersOf10Range  = 0;
  
  // Curated Random class object seed for repeatability
  private long mySeed = 0;
  
  private byte rectifyPowers(byte startP, byte endP)
  {
    byte rangeOfPowers = 0;
    
    enforcePowerLimits();
    
    rangeOfPowers = (byte)((int)endP - (int)startP);
    
    if (rangeOfPowers < 0)
    {
      rangeOfPowers = 0;
    }
    
    return rangeOfPowers;
  }
  
  private void enforcePowerLimits()
  {
    if (this.powersOf10Start < MIN_POWER)
    {
      this.powersOf10Start = MIN_POWER;
    }
    else if (this.powersOf10Start > MAX_POWER)
    {
      this.powersOf10Start = MAX_POWER;
    }
    
    if (this.powersOf10End < MIN_POWER)
    {
      this.powersOf10End = MIN_POWER;
    }
    else if (this.powersOf10End > MAX_POWER)
    {
      this.powersOf10End = MAX_POWER;
    }

  }
  
  /*******
   * Default constructor for a JTestBench object instantiation. 
   * Makes an invalid instance. 
   * @note DO. NOT. USE. EVER.
   */
  public JTestBench()
  {
    this.isValid = false;
    this.myBenchBean = null;
  }
  
  /*******
   * A proper constructor for a JTestBench object instantiation.
   * 
   * @param inSeed (long) Seed for the Random
   * @param inBloomType (JBloomType) Which Bloom Filter implementation to use
   * @param inGridSysType (JGridSysType) Which Coordinate System to use: GARS or MGRS
   * @param inSkipListType (JSkipListType) Which Skip List implementation to use
   * @param inStartPowerOf10 (byte) Starting (i.e. smallest) size, in power of 10, of The Data Set and Test Set during the Test Bench run
   * @param inEndPowerOf10 (byte) Ending (i.e. the largest) size, in power of 10, of The Data Set and Test Set during the Test Bench run
   */
  public JTestBench (long inSeed, JBloomType inBloomType, JGridSysType inGridSysType, JSkipListType inSkipListType, 
      byte inStartPower, byte inEndPower, int inFailRate)
  {
    this.mySeed           = inSeed;
    this.myBenchBean      = ManagementFactory.getThreadMXBean();
    this.myBloom          = inBloomType;
    this.myGrid           = inGridSysType;
    this.mySkip           = inSkipListType;
    this.myBenchSet       = makeOptionsSet();
    this.powersOf10Start  = inStartPower;
    this.powersOf10End    = inEndPower;
    this.powersOf10Range  = rectifyPowers(powersOf10End, powersOf10Start);
    this.myFailRate       = inFailRate;
    this.myFRate          = inFailRate / (double)100;
    
    this.TestSet          = new Vector<JTheDataSetObject>();
    
    this.ot     = new PrintStream(System.out);
    this.n      = new Scanner(System.in);
    this.trash = "";
    
    this.isValid          = true;
  }
  
  /**
   * @return
   */
  private JBenchSetType makeOptionsSet()
  {
    JBenchSetType outType = null;
    
    if (myBloom == JBloomType.Lovasoa)
    {
      if (myGrid == JGridSysType.GARS)
      {
        outType = JBenchSetType.LBloomGARSLP2;
      }
      else
      {
        outType = JBenchSetType.LBloomMGRSLP2;
      }
    }
    else if (myBloom == JBloomType.Sangupta)
    {
      if (myGrid == JGridSysType.GARS)
      {
        outType = JBenchSetType.SBloomGARSLP2;
      }
      else
      {
        outType = JBenchSetType.SBloomMGRSLP2;
      }
    }
    return outType;
  }

  public boolean isValid()
  {
    return this.isValid;
  }
  
  private long calcCreateTime()
  {
    return ((this.timeCreateEnd - this.timeCreateStart) / 1_000);
  }
  
  private long calcVerifyTime()
  {
    return ((this.timeVerifyEnd - this.timeVerifyStart) / 1_000);
  }
  
  private long calcModifyTime()
  {
    return ((this.timeModifyEnd - this.timeModifyStart) / 1_000);
  }
  
  public void startup()
  {
    this.timeCreateStart  = this.getBeanCount();
    this.doCreate();
    this.timeCreateEnd    = this.getBeanCount();
    
    this.timeVerifyStart  = this.getBeanCount();
    this.doVerify();
    this.timeVerifyEnd    = this.getBeanCount();
    
    this.timeModifyStart  = this.getBeanCount();
    this.doModify();
    this.timeModifyEnd    = this.getBeanCount();
    
    this.shutdown();
  }
  
  private void doCreate()
  {
    switch (myBenchSet)
    {
      case LBloomGARSLP2:
      {
        int sizeSet = 1;
        int sizeBloom = 16;
        long numRands = 0;
        Random rng = new Random (this.mySeed);
        JTheDataSetObject tmpP = null;
        for (int i = 0; i < powersOf10Start; i++)
        {
          sizeSet *= 10;
        }
        sizeBloom *= sizeSet;
        
        lBF = new LovaBloomFilter(sizeSet, sizeBloom);
        sL = new SkipList<JTheDataSetObject>();
        
        for (int i = 0; i < sizeSet; i++)
        {
          tmpP = new JTheDataSetObject(JHashType.GARS, rng);  // Make new point object
          lBF.add(tmpP);                                      // Add it to the Bloom Filter
          sL.add(tmpP);                                       // Add it to the Skip List
          if (randFail(rng, myFRate)) // Add it to Test Set (unless randomly fails)
          {
            TestSet.add(JTheDataSetObject.makeBad());
            //ot.println("Random");
            numRands++;
          }
          else
          {
            TestSet.add(tmpP);
          }
        }
        ot.println("Number of Randoms in (Lovasoa) Test Set:             " + numRands);

        
        break;  
      }
      case LBloomMGRSLP2:
      {
        break;
      }
      case SBloomGARSLP2:
      {
        int sizeSet = 1;
        int sizeBloom = 16;
        long numRands = 0;
        Random rng = new Random (this.mySeed);
        JTheDataSetObject tmpP = null;
        for (int i = 0; i < powersOf10Start; i++)
        {
          sizeSet *= 10;
        }
        sizeBloom *= sizeSet;
        
        sBF = new InMemoryBloomFilter<JTheDataSetObject>(sizeSet, 0.0005);
        sL = new SkipList<JTheDataSetObject>();
        
        for (int i = 0; i < sizeSet; i++)
        {
          tmpP = new JTheDataSetObject(JHashType.GARS, rng);  // Make new point object
          sBF.add(tmpP);                                      // Add it to the Bloom Filter
          sL.add(tmpP);                                       // Add it to the Skip List
          if (randFail(rng, myFRate)) // Add it to Test Set (unless randomly fails)
          {
            TestSet.add(JTheDataSetObject.makeBad());
            //ot.println("Random");
            numRands++;
          }
          else
          {
            TestSet.add(tmpP);
          }
        }
        ot.println("Number of Randoms in (Sangupta) Test Set:             " + numRands);

        
        break;
      }
      case SBloomMGRSLP2:
      {
        break;
      }
    }
  }

  private void doVerify()
  {
    boolean     inSet;
    JTheDataSetObject tob = null;

    switch (myBenchSet)
    {
      case LBloomGARSLP2:
      {
        long numSkipFails = 0;
        long numBloomFails = 0;
        ListIterator<JTheDataSetObject> iT = TestSet.listIterator();
        while (iT.hasNext())
        {
          tob = null;
          tob = iT.next();
          ot.println(tob);
          if (lBF.contains(tob)) // If in Bloom Filter
          {
            // Then find it in the Skip List
            inSet = sL.contains(tob);
            if (!inSet)
            {
              //ot.println(inSet);
              numSkipFails++;
            }
          }
          else
          {
            numBloomFails++;
          }
        }
        ot.println("-------");
        sL.printList();
        ot.println("--------");
        ot.println();
        ot.println("Number of objects outside of (Lovasoa) The Data Set Skip List:    " + numSkipFails);
        ot.println("Number of objects outside of (Lovasoa) The Data Set Bloom Filter: " + numBloomFails);
        
        break;  
      }
      case LBloomMGRSLP2:
      {
        break;
      }
      case SBloomGARSLP2:
      {
        long numSkipFails = 0;
        long numBloomFails = 0;
        ListIterator<JTheDataSetObject> iT = TestSet.listIterator();
        while (iT.hasNext())
        {
          tob = null;
          tob = iT.next();
          ot.println(tob);
          if (sBF.contains(tob)) // If in Bloom Filter
          {
            // Then find it in the Skip List
            inSet = sL.contains(tob);
            if (!inSet)
            {
              //ot.println(inSet);
              numSkipFails++;
            }
          }
          else
          {
            numBloomFails++;
          }
        }
        ot.println("-------");
        sL.printList();
        ot.println("--------");
        ot.println();
        ot.println("Number of objects outside of (Sangupta) The Data Set Skip List:    " + numSkipFails);
        ot.println("Number of objects outside of (Sangupta) The Data Set Bloom Filter: " + numBloomFails);
        break;
      }
      case SBloomMGRSLP2:
      {
        break;
      }
    }
  }

  private void doModify()
  {
    switch (myBenchSet)
    {
      case LBloomGARSLP2:
      {
        break;  
      }
      case LBloomMGRSLP2:
      {
        break;
      }
      case SBloomGARSLP2:
      {
        break;
      }
      case SBloomMGRSLP2:
      {
        break;
      }
    }
  }

  private void shutdown()
  {
    this.timeCreateTotal = calcCreateTime();
    this.timeVerifyTotal = calcVerifyTime();
    this.timeModifyTotal = calcModifyTime();
    
    this.outResults();
  }
  
  /**
   * 
   */
  private void outResults()
  {
    
    ot.println("Creation time:     " + this.timeCreateTotal);
    ot.println("Verification time: " + this.timeVerifyTotal);
    ot.println("Modification time: " + this.timeModifyTotal);
    
    ot.println("*******************************************************************************************");
  }

  private long getBeanCount()
  {
    return myBenchBean.getCurrentThreadCpuTime();
  }
  
  public static boolean randFail(Random rng, double myFailRate)
  {
    boolean itFailed;
    double rate = myFailRate / 100.0;
    
    itFailed = rng.nextDouble() > rate;
    
    return itFailed;
  }
  
}

/*
--------------------------------------------------------------------------------
----  The blank lines below are to aid me in keeping the last line of this  ----
----  file vertically centered and above the very bottom of my very large   ---- 
----  monitor.                                                              ----
--------------------------------------------------------------------------------
*/


















































































