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
import java.util.Stack;
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
   * JTestBench instance values
   */
  
  
  /**
   * Will become a Random object with seed mySeed, each time a Random needs to start fresh.
   */
  private Random  tbRng;
  
  /**
   * The size of the sets for a given run
   */
  private int     sizeSet;
  
  /**
   * The False Positvity Rate needed to instantiate a Sangupta Bloom Filter
   * 
   * @Note This is determined by (static final) SANGUPTA_BLOOM_FALSE_POSITIVE_RATE
   */
  double  fPosRate;
  
  /**
   * The Bits per Object needed to instantiate a Lovasoa Bloom Filter
   * 
   * @Note This is determined by (static final) LOVASOA_BLOOM_BITS_PER_OBJECT
   */
  int     bitsPerObj;
  
  /**
   * The actual constructed size of a Lovasoa Bloom Filter
   * 
   * @Note This is calulated as (bitsPerObj * sizeSet)
   */
  int     sizeLBloom;
  
  boolean doesItFail; // To determine whether a given object in The Data Set goes into Test Set, based on myFailRate
  
  /**
   * Keep count of number of Bad entries, i.e. number of objects in Test Set NOT IN The Data Set
   */
  long numBads;
  
  /**
   * Keep count of number of misses in the Skip List for objects not found
   */
  long numFails;
  
  /**
   * Limit constants
   */
  public static final int     MIN_POWER = 1;  // No less than 10**1 =          10 Objects in a set
  public static final int     MAX_POWER = 8;  // No more than 10**8 = 100_000_000 Objects in a set
  public static final double  SANGUPTA_BLOOM_FALSE_POSITIVE_RATE = 0.01; // Rate of false positives in Sangupta Bloom Filter
  public static final int     LOVASOA_BLOOM_BITS_PER_OBJECT = 8;    // Number of bits per object to allocate for the Lovasoa Bloom Filter
  
  /**
   * This is the conversion factor to make it easier to read
   * 
   * @Note ThreadMXBean returns times in nanoseconds.
   *         1_000 converts times to microseconds output
   *     1_000_000 converts times to  miliseconds output
   * 1_000_000_000 converts times to      seconds output
   */ 
//  public static final int     DURATION_TIMESCALE_QUOTIENT =             1;
  public static final int     DURATION_TIMESCALE_QUOTIENT =         1_000;
//  public static final int     DURATION_TIMESCALE_QUOTIENT =     1_000_000;
//  public static final int     DURATION_TIMESCALE_QUOTIENT = 1_000_000_000;
 
  // Is this a valid instatnce of JTestBench?
  private boolean isValid = false;  
  
  // The bean for counting...
  private ThreadMXBean myBenchBean = null; // ...but it's not ready for counting yet.
  
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
  
  // The data structure implementations to use, and their default values
  private JBloomType    myBloom     = JBloomType.Lovasoa;
  private JGridSysType  myGrid      = JGridSysType.GARS;
  private JSkipListType mySkip      = JSkipListType.LP2;
  private JBenchSetType myBenchSet  = JBenchSetType.LBloomGARSLP2;
  
  /* THE FULL SETS (separate from the actual storage data structure implementation object instantiations) */
  Stack<JTheDataSetObject>                TheDataSet; // The Data Set™
  Stack<JTheDataSetObject>                TestSet;    // Test Set for this Bench
  
  // Percentage of The Data Set objects that will NOT Test Set...
  private int           myFailRate; // ...as an int...
  private double        myFRate;    // ...as a double.
  
  LovaBloomFilter                         lovaBloom;        // Lovasoa Bloom Filter for this Bench
  InMemoryBloomFilter<JTheDataSetObject>  sangBloom;        // Sangupta Bloom Filter for this Bench
  SkipList<JTheDataSetObject>             LP2Skip;         // Skip List for this Bench
  
  PrintStream ot;     // Short alias for console output
  Scanner     n;      // Short alias for console input
  String      trash;  // Discard bad input

  
  /**
   *  The orders of magnitude of the size of The Data Set and Test Set. They will all be tried and times recorded.
   */
  private int powersOf10Start = 0;  // Where to start the series of runs for a given Bench
  private int powersOf10End   = 0;  // Where to   end the series of runs for a given Bench
  private int powersOf10Range = 0;  // The total number of runs in each series of runs for a given bench
                                    // so as to cover the entire set of Orders of Magnitude for the sizes of the sets on the Bench
  
  private int potStart        = 0;  // Expanded form of powersOf10Start, i.e. 10 ** powersOf10Start
  private int potEnd          = 0;  // Expanded form of powersOf10End  , i.e. 10 ** powersOf10End
  
  private int potCurr         = 0;  // Expanded form of current size of the sets. TODO: iterate this variable over powersOf10Range
  
  /**
   * Curated Random class object seed for repeatability
   */
  private long    mySeed  = 0;          // "Keep it secret, keep it safe...", but not really; it just needs to stay the same.
  
  /**
   * 
   * 
   * @param powersOf10End
   * @param powersOf10Start
   * @return (int) rangeOfPowers 
   *         This is the number of orders of magnitude over which to iterate to emirically determine time complexity of each implementation  
   */
  private int powersRange(int powersOf10End, int powersOf10Start)
  {
    int rangeOfPowers = 0;
    
    enforcePowerLimits();
    rangeOfPowers = powersOf10Start - powersOf10End;
    if (rangeOfPowers < 0) {rangeOfPowers = 0;}
    
    return rangeOfPowers;
  }
  
  /**
   * Keep powers within the declared limits
   */
  private void enforcePowerLimits()
  {
    if (this.powersOf10Start < MIN_POWER) {this.powersOf10Start = MIN_POWER;}
    else if (this.powersOf10Start > MAX_POWER) {this.powersOf10Start = MAX_POWER;}
    
    if (this.powersOf10End < MIN_POWER) {this.powersOf10End = MIN_POWER;}
    else if (this.powersOf10End > MAX_POWER) {this.powersOf10End = MAX_POWER;}
  }
  
  /*******
   * Default constructor for a JTestBench object instantiation. 
   * Makes an invalid instance. 
   * @Note DO. NOT. USE. EVER.
   * _..._
   * (I used it any way.)
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
      int inStartPower, int inEndPower, int inFailRate)
  {
    this.mySeed           = inSeed;
    this.myBenchBean      = ManagementFactory.getThreadMXBean(); // Get ready to do some "bean counting", i.e. get CPU times
    
    // Configuration of implementations
    this.myBloom          = inBloomType;
    this.myGrid           = inGridSysType;
    this.mySkip           = inSkipListType;
    this.myBenchSet       = makeOptionsSet();
    
    // Powers of 10 for the sizes of the sets
    this.powersOf10Start  = inStartPower;
    this.powersOf10End    = inEndPower;
    this.enforcePowerLimits();
    this.powersOf10Range  = powersRange(powersOf10End, powersOf10Start);
    
    // Expanded versions. I.e. evaluation of "10 to the power of ____"
    this.potStart         = expandPot(this.powersOf10Start);
    this.potEnd           = expandPot(this.powersOf10End);
    this.potCurr          = this.potStart; // First run starts with sets of this size
    
    // Number of times each Bloom Filter will reject Test Set objects, because an object is not in The Data Set
    this.myFailRate       = inFailRate;
    this.myFRate          = inFailRate / (double)100;
    
    // Initialize the stacks that will hold each set of objects.
    this.TheDataSet       = new Stack<JTheDataSetObject>();
    this.TestSet          = new Stack<JTheDataSetObject>();
    
    // Instantiate objects for console input/output
    this.ot     = new PrintStream(System.out);
    this.n      = new Scanner(System.in);
    this.trash = "";
    
    
    /**
     * All done constructing this particular instance/instantiation of a JTestBench object.
     * @Note It is valid.
     */
    this.isValid          = true;
  }
  
  /**
   * @param inPot: A Power of 10 to expand
   * @return (int) The numer 10 raised to inPot
   */
  private int expandPot(int inPot)
  {
    int outPot = 1;
    for (int j = 0; j < inPot; j++)
    {
      outPot *= 10;
    }
    return outPot;
  }

  /**
   * Compile the given options into an enum constant
   * @return (JBenchSetType) The exact configuration of this JTestBench instance.
   */
  private JBenchSetType makeOptionsSet()
  {
    JBenchSetType outType = null;
    
    if (myBloom == JBloomType.Lovasoa)
    {
      if (myGrid == JGridSysType.GARS) {outType = JBenchSetType.LBloomGARSLP2;}
      else {outType = JBenchSetType.LBloomMGRSLP2;}
    }
    else if (myBloom == JBloomType.Sangupta)
    {
      if (myGrid == JGridSysType.GARS) {outType = JBenchSetType.SBloomGARSLP2;}
      else {outType = JBenchSetType.SBloomMGRSLP2;}
    }
    
    return outType;
  }
  
  /**
   * Is this a valid construction of a JTestBench?
   * @return (boolean) Whether the construction is valid or not.
   */
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
    /**
     * Initialize the Random for this JTestBench instance
     */
    this.tbRng    = new Random(mySeed); // Initialize Random from the seed, for same data in ALL sets of a JTechBench object
    this.sizeSet  = potCurr; // Size of sets to make TODO: run once per power of 10 up to powerOf10End
    this.fPosRate    = SANGUPTA_BLOOM_FALSE_POSITIVE_RATE; // False positivity rate in a Sangupta Bloom Filter
    this.bitsPerObj  = LOVASOA_BLOOM_BITS_PER_OBJECT; // Number of bits per object in a Lovasoa Bloom Filter
    this.sizeLBloom  = sizeSet * bitsPerObj;         // Size passed into Lovasoa Bloom Filters
    
    boolean doesItFail; // To determine whether a given object in The Data Set goes into Test Set, based on myFailRate 
    
        
    /**
     * Time the Creation of the data sets.
     */
    this.timeCreateStart  = this.getBeanCount();
    this.doCreate();
    this.timeCreateEnd    = this.getBeanCount();
    
    /**
     * Time the Verification of the data sets.
     */
    this.timeVerifyStart  = this.getBeanCount();
    this.doVerify();
    this.timeVerifyEnd    = this.getBeanCount();
    
    /**
     * Time the Modification of the data sets.
     */
    this.timeModifyStart  = this.getBeanCount();
    this.doModify();
    this.timeModifyEnd    = this.getBeanCount();
    
    /**
     * Shut it down and output the results
     */
    this.shutdown();
  }
  
  /**
   * Converts (JGridSysType) this.myGrid to a JHashType
   * 
   * @return (JHashType) outType
   */
  private JHashType gridToHashType()
  {
    JHashType outType = null;
    switch (this.myGrid)
    {
      case GARS: {outType = JHashType.GARS; break;}
      case MGRS: {outType = JHashType.MGRS; break;}
    }
    return outType;
  }
  
  /**
   * Abstracted way to add object o into the correct Bloom.
   * 
   * @param (JTheDataSetObject) o
   */
  private void addToBloom (JTheDataSetObject o)
  {
    switch (myBloom)
    {
      case Lovasoa:   {lovaBloom.add(o);}
      case Sangupta:  {sangBloom.add(o);}
    }
  }
  
  /**
   * Create this JTestBench run's data sets.
   */
  private void doCreate()
  {
    numBads  = 0;
        
    JTheDataSetObject tmpObj      = null; // Temporary data set object for creating all the various sets
    JTheDataSetObject tmpObjFail  = null; // Temporary data set object that is outide of The Data Set, 
                                          // to be put into Test Set to fill the quota for a given myFailRate.
    
    /**
     * Create a Bloom Filter for this JTestBench object, to quickly eliminate objects NOT in The Data Set.
     * 
     * @Note Construct and initialize the correct Bloom Filter, based on (JBloomType) myBloom. 
     */
    if (this.myBloom == JBloomType.Sangupta)  {sangBloom = new InMemoryBloomFilter<JTheDataSetObject>(sizeSet, fPosRate);}
    else                                      {lovaBloom = new LovaBloomFilter(sizeSet, sizeLBloom);}
    
    /**
     * Create a Skip List to contain The Data Set
     * 
     * @Note For verification purposes, objects put into the Skip List (and the Bloom Filter)
     *       will ALSO BE PUT INTO a stack which I consider as the REAL The Data Set, of 
     *       which, this Skip List is merely a COPY.
     *
     * @Note Also, I have not made my OWN Skip List implementation, so Long-Project-2 Skip List will have to do...
     * TODO: Make my own implementation.
     */
    if (this.mySkip == JSkipListType.LP2) {LP2Skip = new SkipList<JTheDataSetObject>();}
    
    /**
     * Add the requisite number of objects into each data set.
     * 
     *  @Note Some proper objects in The Data Set (both of them) will have
     *        an invalid object complement in Test Set, based on stochastic
     *        function that makes a certain percentage of objects in the 
     *        Test Set outside of (not included in) The Data Set. This is
     *        where Bloom Filters really shine, compared with R-Trees, in
     *        theory anyway.
     */
    for (int k = 0; k < sizeSet; k++)
    {
      /* Make an object for the sets */
      tmpObj      = new JTheDataSetObject(gridToHashType(), tbRng);
      tmpObjFail  = new JTheDataSetObject(gridToHashType(), tbRng, true);
      doesItFail  = randFail(tbRng, myFailRate);
      
      /* Stick 'em where they belong */
      TheDataSet.push(tmpObj);
      addToBloom(tmpObj);
      LP2Skip.add(tmpObj);
      if (doesItFail)
      {
        TestSet.push(tmpObjFail);
        numBads++;
      }
      else
      {
        TestSet.push(tmpObj);
      }
      
      /* Clear out the temporaries */
      tmpObj      = null;
      tmpObjFail  = null;
      
      /* Diagnostic: Output the number of "Bad" objects */
      ot.println("Number of \"Bad\" entries in Test Set: " + numBads);
    }
  }

  /**
   * Verify which objects in Test Set are also in The Data Set
   * 
   * @Note The Data Set has two copies:
   *       The Bloom Filters and Skip List I am testing, and the
   *       Stack TheDataSet as a fool-proof backup for what 
   *       SHOULD be in the Skip List (and Bloom Filter).
   */
  private void doVerify()
  {
    numFails = 0; // 
    
//    boolean     inSet;
//    JTheDataSetObject tob = null;
//
//    switch (myBenchSet)
//    {
//      case LBloomGARSLP2:
//      {
//        long numSkipFails = 0;
//        long numBloomFails = 0;
//        ListIterator<JTheDataSetObject> iT = TestSet.listIterator();
//        while (iT.hasNext())
//        {
//          tob = null;
//          tob = iT.next();
//          ot.println(tob);
//          if (lBF.contains(tob)) // If in Bloom Filter
//          {
//            // Then find it in the Skip List
//            inSet = sL.contains(tob);
//            if (!inSet)
//            {
//              //ot.println(inSet);
//              numSkipFails++;
//            }
//          }
//          else
//          {
//            numBloomFails++;
//          }
//        }
//        ot.println("-------");
//        sL.printList();
//        ot.println("--------");
//        ot.println();
//        ot.println("Number of objects outside of (Lovasoa) The Data Set Skip List:    " + numSkipFails);
//        ot.println("Number of objects outside of (Lovasoa) The Data Set Bloom Filter: " + numBloomFails);
//        
//        break;  
//      }
//      case LBloomMGRSLP2:
//      {
//        break;
//      }
//      case SBloomGARSLP2:
//      {
//        long numSkipFails = 0;
//        long numBloomFails = 0;
//        ListIterator<JTheDataSetObject> iT = TestSet.listIterator();
//        while (iT.hasNext())
//        {
//          tob = null;
//          tob = iT.next();
//          ot.println(tob);
//          if (sBF.contains(tob)) // If in Bloom Filter
//          {
//            // Then find it in the Skip List
//            inSet = sL.contains(tob);
//            if (!inSet)
//            {
//              //ot.println(inSet);
//              numSkipFails++;
//            }
//          }
//          else
//          {
//            numBloomFails++;
//          }
//        }
//        ot.println("-------");
//        sL.printList();
//        ot.println("--------");
//        ot.println();
//        ot.println("Number of objects outside of (Sangupta) The Data Set Skip List:    " + numSkipFails);
//        ot.println("Number of objects outside of (Sangupta) The Data Set Bloom Filter: " + numBloomFails);
//        break;
//      }
//      case SBloomMGRSLP2:
//      {
//        break;
//      }
//    }
  }

  private void doModify()
  {
//    switch (myBenchSet)
//    {
//      case LBloomGARSLP2:
//      {
//        break;  
//      }
//      case LBloomMGRSLP2:
//      {
//        break;
//      }
//      case SBloomGARSLP2:
//      {
//        break;
//      }
//      case SBloomMGRSLP2:
//      {
//        break;
//      }
//    }
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

/* Archived code, for future reference: */
//switch (myBenchSet)
//{
//case LBloomGARSLP2:
//{
//  for (int i = 0; i < powersOf10Start; i++)
//  {
//    sizeSet *= 10;
//  }
//  sizeBloom *= sizeSet;
//  
//  lBF = new LovaBloomFilter(sizeSet, sizeBloom);
//  sL = new SkipList<JTheDataSetObject>();
//  
//  for (int i = 0; i < sizeSet; i++)
//  {
//    tmpP = new JTheDataSetObject(JHashType.GARS, rng);  // Make new point object
//    lBF.add(tmpP);                                      // Add it to the Bloom Filter
//    sangBloom.add(tmpObj);                                       // Add it to the Skip List
//    if (randFail(rng, myFRate)) // Add it to Test Set (unless randomly fails)
//    {
//      TestSet.add(JTheDataSetObject.makeBad(rng));
//      //ot.println("Random");
//      numRands++;
//    }
//    else
//    {
//      TestSet.add(tmpP);
//    }
//  }
//  for (int x = 0; x < TestSet.size(); x++)
//  {
//    ot.println(TestSet.at(x).toString());
//  }
//  ot.println("Number of Randoms in (Lovasoa) Test Set:             " + numRands);
//
//  
//  break;  
//}
//case LBloomMGRSLP2:
//{
//  break;
//}
//case SBloomGARSLP2:
//{
//  int sizeSet = 1;
//  int sizeBloom = 16;
//  long numRands = 0;
//  Random rng = new Random (this.mySeed);
//  JTheDataSetObject tmpP = null;
//  for (int i = 0; i < powersOf10Start; i++)
//  {
//    sizeSet *= 10;
//  }
//  sizeBloom *= sizeSet;
//  
//  sBF = new InMemoryBloomFilter<JTheDataSetObject>(sizeSet, 0.0005);
//  sL = new SkipList<JTheDataSetObject>();
//  
//  for (int i = 0; i < sizeSet; i++)
//  {
//    tmpP = new JTheDataSetObject(JHashType.GARS, rng);  // Make new point object
//    sBF.add(tmpP);                                      // Add it to the Bloom Filter
//    sL.add(tmpP);                                       // Add it to the Skip List
//    if (randFail(rng, myFRate)) // Add it to Test Set (unless randomly fails)
//    {
//      TestSet.add(JTheDataSetObject.makeBad());
//      //ot.println("Random");
//      numRands++;
//    }
//    else
//    {
//      TestSet.add(tmpP);
//    }
//    tmpP = null;
//  }
//  ot.println("Number of Randoms in (Sangupta) Test Set:             " + numRands);
//
//  
//  break;
//}
//case SBloomMGRSLP2:
//{
//  break;
//}
//}

/*
--------------------------------------------------------------------------------
----  The blank lines below are to aid me in keeping the last line of this  ----
----  file vertically centered and above the very bottom of my very large   ---- 
----  monitor.                                                              ----
--------------------------------------------------------------------------------
*/


















































































