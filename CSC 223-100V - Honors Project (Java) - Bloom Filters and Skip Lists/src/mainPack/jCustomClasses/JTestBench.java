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

/* ****************************************************************************************************************************************/
/* **** Imports ***************************************************************************************************************************/
/* ****************************************************************************************************************************************/


import java.util.Iterator;
import java.util.ListIterator;
/*
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

/*
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
 * A class whose instatiations can be used for "Benchmark Testing", hence the name.
 * 
 * @Note This class is where nearly all of this Java program's execution time is spent
 */
public class JTestBench
{  
/* ****************************************************************************************************************************************/
/* **** Declarations and Variables ********************************************************************************************************/
/* ****************************************************************************************************************************************/
  /**
   * Curated Random class object seed for repeatability
   */
  private long    mySeed  = 0;          // "Keep it secret, keep it safe...", but not really; it just needs to stay the same.
  /**
   * The Random object to pass around where needed.
   * 
   * @Note Will become a Random object with seed mySeed, each
   *       time a Random needs to start fresh.
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
  /**
   * To determine whether a given object in The Data Set goes into Test Set, based on myFailRate
   * 
   * @Note Used only in doCreate() method
   */
  boolean doesItFail; 
  /**
   * Used to keep count of number of Bad entries, i.e. number of objects in Test Set NOT IN The Data Set
   * 
   * @Note Used onlt in Creation phase
   */
  long numBads;
  /**
   * Used to keep count of number of misses in the Skip List for objects not found
   * 
   * @Note Used only in Verification phase
   */
  long numFails;
  /*
   * Limit constants
   */
  public static final int     MIN_POWER = 1;  // No less than 10**1 =          10 Objects in a set
  public static final int     MAX_POWER = 8;  // No more than 10**8 = 100_000_000 Objects in a set
  public static final double  SANGUPTA_BLOOM_FALSE_POSITIVE_RATE = 0.01; // Rate of false positives in Sangupta Bloom Filter
  public static final int     LOVASOA_BLOOM_BITS_PER_OBJECT = 8;    // Number of bits per object to allocate for the Lovasoa Bloom Filter
  public static final int     MOD_SET_PERCENT_OF_TEST_SET = 15;
  /**
   * This is the conversion factor to make it easier to read
   * 
   * @Note ThreadMXBean returns times in nanoseconds.
   *         1_000 converts times to microseconds output
   *     1_000_000 converts times to  miliseconds output
   * 1_000_000_000 converts times to      seconds output
   */ 
//  public static final int     DURATION_TIMESCALE_QUOTIENT =             1;
//  public static final int     DURATION_TIMESCALE_QUOTIENT =         1_000;
  public static final int     DURATION_TIMESCALE_QUOTIENT =     1_000_000;
//  public static final int     DURATION_TIMESCALE_QUOTIENT = 1_000_000_000;
  /**
   * Is this a valid instance of JTestBench?
   * 
   * @Note Yes, probably.
   */
  private boolean isValid = false;  
  /**
   * The bean for counting...
   * 
   * @Note Instantiated within JTestBench constructors
   */
  private ThreadMXBean myBenchBean; 
  /* Timers for each phase of testing */
  /**
   * CPU Timestamp for start of Creation phase. 
   * 
   * @Note A Creation timestamp.
   */
  private long timeCreateStart  = 0;
  /**
   * CPU Timestamp for end of Creation phase.
   * 
   * @Note A Creation timestamp.
   */
  private long timeCreateEnd    = 0;
  /**
   * Total CPU time for Creation phase
   * 
   * @Note A Creation timestamp.
   */
  private long timeCreateTotal  = 0;
  /**
   * CPU Timestamp for start of Verification phase.
   * 
   * @Note A Verification timestamp.
   */
  private long timeVerifyStart  = 0;
  /**
   * CPU Timestamp for end of Verification phase.
   * 
   * @Note A Verification timestamp.
   */
  private long timeVerifyEnd    = 0;
  /**
   * Total CPU time for Verification phase
   * 
   * @Note A Verification timestamp.
   */
  private long timeVerifyTotal  = 0;
  /**
   * CPU Timestamp for start of Modification phase.
   * 
   * @Note A Modification timestamp.
   */
  private long timeModifyStart  = 0;
  /**
   * CPU Timestamp for end of Modification phase.
   * 
   * @Note A Modification timestamp.
   */
  private long timeModifyEnd    = 0;
  /**
   * Total CPU time for Modification phase.
   * 
   * @Note A Modification timestamp.
   */
  private long timeModifyTotal  = 0;  
  /**
   * Which type (i.e. which imported implementation) of
   * Bloom Filter to use
   * 
   * @Note JBloomTypes are:
   *       .Lovasoa
   *       and
   *       .Sangupta
   */
  private JBloomType    myBloom     = JBloomType.Lovasoa;
  /**
   * Which type (i.e. which imported implementation) of
   * Bloom Filter to use
   * 
   * @Note JBloomTypes are:
   *       .Lovasoa
   *       and
   *       .Sangupta
   */  
  private JGridSysType  myGrid      = JGridSysType.GARS;
  /**
   * Which type (i.e. which imported implementation) of
   * Bloom Filter to use
   * 
   * @Note JBloomTypes are:
   *       .Lovasoa
   *       and
   *       .Sangupta
   */
  private JSkipListType mySkip      = JSkipListType.LP2;
  /**
   * THE FULL SET: The Data Set™
   * 
   * @Note Separate from the actual storage data structure
   *       implementation object instantiations to facilitate
   *       double-checking the imported implementations as to
   *       whether I am using them properly.
   * @Note Implemented as a Java Collection Vector
   */
  Vector<JTheDataSetObject>                TheDataSet;
  /**
   * Test Set for this Bench
   * 
   * @Note Implemented as a Java Collection Stack
   */
  Vector<JTheDataSetObject>                TestSet;
  /**
   * Modifications set
   */
  Vector<JTheDataSetObject>                 ModSet;
  /**
   * Temporary data set object for creation and verification in all the various sets
   */
  JTheDataSetObject tmpObj; 
  /**
   * Temporary data set object that is outide of The Data Set,
   * to be put into Test Set to fill the quota for a given myFailRate.
   */
  JTheDataSetObject tmpObjFail;  
  /**
   * Percentage of The Data Set objects that will NOT Test Set...
   * 
   * @Note ...as an int...
   */
  private int           myFailRate; // ...as an int...
  /**
   * Percentage of The Data Set objects that will NOT Test Set...
   * 
   * @Note ...as a double.
   */
  private double        myFRate;    // ...as a double.
  /**
   * Lovasoa Bloom Filter for this Bench.
   */
  LovaBloomFilter                         lovaBloom;
  /**
   * Sangupta Bloom Filter for this Bench.
   */
  InMemoryBloomFilter<JTheDataSetObject>  sangBloom;
  /**
   * Skip List for this Bench.
   */
  SkipList<JTheDataSetObject>             LP2Skip;
  /**
   * R-Tree for this Bench.
   */
//  RTree<JTheDataSetObject>                RTree;
  RTree<String>                RTree;
  /**
   * A short alias for console output.
   */
  PrintStream ot;
  /**
   * A short alias for console input.
   */
  Scanner     n;
  /**
   * A temporary String used for processing input.
   * 
   * @Note Used only by (PrintStream) n to discard bad input
   */
  String      trash;
  /**
   * Lower end of sizeTest for this series of runs.
   * @Note A power of 10 order of magnitude
   * @Note Expanded by expandPot() and stored in potStart
   */
  private int powersOf10Start = 0;
  /**
   * Upper end of sizeTest for this series of runs.
   * @Note A power of 10 order of magnitude
   * @Note Expanded by expandPot() and stored in potEnd
   */
  private int powersOf10End   = 0;
  /**
   * Total range over which sizeTest varies for this Bench's series of runs.
   * 
   * @Note The total number of runs in each series of runs for a given bench
   *        so as to cover the entire set of Orders of Magnitude for the sizes of the sets on the Bench
   */
  private int powersOf10Range = 0;
  /**
   * Lower end of sizeTest for this series of runs.
   * @Note This is the expanded form of powersOf10Start
   */
  private int potStart        = 0;
  /**
   * Upper end of sizeTest for this series of runs.
   * @Note This is the expanded form of powersOf10End
   */
  private int potEnd          = 0;
  /**
   * Expanded form of current size of the sets.
   * 
   * @Note TODO: iterate this variable over powersOf10Range
   */
  private int potCurr         = 0;
    
/* ****************************************************************************************************************************************/
/* **** Methods ***************************************************************************************************************************/
/* ****************************************************************************************************************************************/
  
  /**
   * Get the range over which to iterate to empirically
   * determine time comlexities
   * 
   * @param powersOf10End
   * @param powersOf10Start
   * @return (int) rangeOfPowers 
   *         This is the number of orders of magnitude over which to iterate to emirically determine time complexity of each implementation  
   */
  private int powersRange(int powersOf10End, int powersOf10Start)
  {
    int rangeOfPowers = 0;
    rangeOfPowers = powersOf10Start - powersOf10End;
    if (rangeOfPowers < 0) {rangeOfPowers = 0;}
    return rangeOfPowers;
  }
  /**
   * Keep powers within the declared limits
   * 
   * @Note Called in constructor
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
   * 
   * (...I used it any way.)
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
   * @param inStartPowerOf10 (int) Starting (i.e. smallest) size, in power of 10, of The Data Set and Test Set during the Test Bench run
   * @param inEndPowerOf10 (int) Ending (i.e. the largest) size, in power of 10, of The Data Set and Test Set during the Test Bench run
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
    this.TheDataSet       = new Vector<JTheDataSetObject>();
    this.TestSet          = new Vector<JTheDataSetObject>();
    this.ModSet           = new Vector<JTheDataSetObject>();
    
    // Instantiate objects for console input/output
    this.ot     = new PrintStream(System.out);
    this.n      = new Scanner(System.in);
    this.trash = "";
    
    /*
     * All done constructing this particular instance/instantiation of a JTestBench object.
     * It is valid.
     */
    this.isValid          = true;
  }
  /**
   * Expands fields given as powers of 10
   * 
   * @param inPot The Power of 10 to expand
   * @return (int) The numer 10 raised to inPot
   * @Note Called by JTestBench constructor
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
   * Is this a valid construction of a JTestBench?
   * @return (boolean) Whether the construction is valid or not.
   */
  public boolean isValid()
  {
    return this.isValid;
  }
  /**
   * Calculates total CPU time of Creation phase.
   * 
   * @return This Java program thread's CPU time spent on Creation phase
   * 
   * @Note Timescale varies by using (staic final) DURATION_TIMESCALE_QUOTIENT factor
   */
  private long calcCreateTime()
  {
    return ((this.timeCreateEnd - this.timeCreateStart) / DURATION_TIMESCALE_QUOTIENT);
  }
  /**
   * Calculates total CPU time of Verification phase.
   * 
   * @return This Java program thread's CPU time spent on Verification phase
   * 
   * @Note Timescale varies by using (staic final) DURATION_TIMESCALE_QUOTIENT factor
   */
  private long calcVerifyTime()
  {
    return ((this.timeVerifyEnd - this.timeVerifyStart) / DURATION_TIMESCALE_QUOTIENT);
  }
  /**
   * Calculates total CPU time of Modification phase.
   * 
   * @return This Java program thread's CPU time spent on Modification phase
   * 
   * @Note Timescale varies by using (staic final) DURATION_TIMESCALE_QUOTIENT factor
   */
  private long calcModifyTime()
  {
    return ((this.timeModifyEnd - this.timeModifyStart) / DURATION_TIMESCALE_QUOTIENT);
  }
  
  /**
   * The main logic control structure of the JTestBench class.
   * 
   * @Note Let's get this (Bench)party started! Wooo!
   */
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
    
    /**
     * Creation of the data sets.
     */
    this.doCreate();
    /**
     * Verification of the data sets.
     */
    if (myBloom == JBloomType.R_Tree)
    {
      this.doRTreeVerify();
    }
    else
    {
      this.doVerify();
    }
    /**
     * Modification of the data sets.
     */
    this.doModify();
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
    try
    {
      switch (myBloom)
      {
        case Lovasoa:   {this.lovaBloom.add(o); break;}
        case Sangupta:  {this.sangBloom.add(o); break;}
        case R_Tree:
        {
          RTree.insert(o.toRTreePoint());
        }
      }
    } catch (Exception e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  /**
   * Abstracted way to check if object o is in the Bloom used by this bench.
   * 
   * @param o - The object whose inclusion in the filter is to be checked 
   * @return (boolean) outBool - Whether object o is in the Bloom
   */
  private boolean checkInBloom (JTheDataSetObject o)
  {
    boolean outBool = false;
    try
    {
      switch (myBloom)
      {
        case Lovasoa:   {outBool = lovaBloom.contains(o); break;}
        case Sangupta:  {outBool = sangBloom.contains(o); break;}
        case R_Tree:
        {
          double[] min = {-90.0, -180.0, 0.0};
          double[] max = { 90.0,  180.0, 999_999.0};
          outBool = RTree.contains(min, max, o.getMyPay());
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
    return outBool;
  }
  private String getBloomType()
  {
    String outStr = "";
    switch (myBloom)
    {
      case Lovasoa:   {outStr = " Lovasoa"; break;}
      case Sangupta:  {outStr = "Sangupta"; break;}
      case R_Tree:    {outStr = "  R-Tree"; break;}
    }
    return outStr;
  }
  /**
   * Create this JTestBench run's data sets.
   */
  private void doCreate()
  {
    ot.println("*******************************************************************************************");
    ot.println();
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println("        CREATION       ");
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println();
    ot.println("Bloom type: " + getBloomType());
    
    numBads  = 0;
    int numGoods = 0;
    
    /**
     * Create a Bloom Filter for this JTestBench object, to quickly eliminate objects NOT in The Data Set.
     * 
     * @Note Construct and initialize the correct Bloom Filter, based on (JBloomType) myBloom. 
     */
    if      (this.myBloom == JBloomType.Sangupta) {this.sangBloom = new InMemoryBloomFilter<JTheDataSetObject>(sizeSet, fPosRate);}
    else if (this.myBloom == JBloomType.Lovasoa)  {this.lovaBloom = new LovaBloomFilter(sizeSet, sizeLBloom);}
    else if (this.myBloom == JBloomType.R_Tree)   {this.RTree     = importedImplementations.TinSpinIndexes.index.rtree.RTree.createRStar(3);}
    
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
    this.timeCreateStart  = this.getBeanCount();
    for (int k = 0; k < sizeSet; k++)
    {
      /* Make an object for the sets */
      tmpObj      = new JTheDataSetObject(gridToHashType(), tbRng);
      tmpObjFail  = new JTheDataSetObject(gridToHashType(), tbRng, true);
      doesItFail  = randFail(tbRng, myFailRate);
      
      /* Stick 'em where they belong */
      TheDataSet.add(tmpObj);
      addToBloom(tmpObj);
      LP2Skip.add(tmpObj);
      if (!doesItFail)
      {
        TestSet.add(tmpObjFail);
        numBads++;
      }
      else
      {
        TestSet.add(tmpObj);
        numGoods++;
      }
      
      /* Clear out the temporaries */
      tmpObj      = null;
      tmpObjFail  = null;
    }
    this.timeCreateEnd    = this.getBeanCount();
    /* Diagnostic: Output the number of "Bad" objects */
    ot.println(String.format("Total number of objects in Test Set: % 6d", sizeSet));
    ot.println(String.format("Number of \"Bad\" entries in Test Set:  % 6d", numBads));
    ot.println(String.format("Number of \"Good\" entries in Test Set: % 6d", numGoods)); 

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
    ot.println();
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println("      VERIFICATION     ");
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");

    int numBloomFails = 0;
    int numBloomPositives = 0;
    int numSkipFails = 0;
    int numSkipPositives = 0;
    this.timeVerifyStart  = this.getBeanCount();
    for (int k = 0; k < TestSet.size(); k++)
    {
      tmpObj = TestSet.elementAt(k);
      if (checkInBloom(tmpObj))
      {
        numBloomPositives++;
        if (LP2Skip.contains(tmpObj))
        {
          numSkipPositives++;
        }
        else
        {
          numSkipFails++;
        }
      }
      else
      {
        numBloomFails++;
      }
    }
    this.timeVerifyEnd    = this.getBeanCount();
    ot.println();
    ot.println(String.format("Bloom Fails: %1$ 6d, Bloom Hits: %2$ 6d", numBloomFails, numBloomPositives));
    ot.println(String.format("Skip  Fails: %1$ 6d, Skip  Hits: %2$ 6d", numSkipFails, numSkipPositives));
  }
  /**
   * Verify which objects in Test Set are also in The Data Set
   * 
   * @Note The Data Set has two copies:
   *       The Bloom Filters and Skip List I am testing, and the
   *       Stack TheDataSet as a fool-proof backup for what 
   *       SHOULD be in the Skip List (and Bloom Filter).
   */
  private void doRTreeVerify()
  {
    ot.println();
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println("      VERIFICATION     ");
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");

    int numRTreeFails = 0;
    int numRTreeHits = 0;
    this.timeVerifyStart  = this.getBeanCount();
    for (int k = 0; k < TestSet.size(); k++)
    {
      tmpObj = TestSet.elementAt(k);
      if (checkInBloom(tmpObj))
      {
        numRTreeHits++;
      }
      else
      {
        numRTreeFails++;
      }
    }
    this.timeVerifyEnd    = this.getBeanCount();
    ot.println();
    ot.println(String.format("Bloom Fails: %1$ 6d, Bloom Hits: %2$ 6d", numRTreeFails, numRTreeHits));
  }

  
  private void doModify()
  {
    ot.println();
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println("     Modification      ");
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println();

    double modRate = (double)MOD_SET_PERCENT_OF_TEST_SET;
    
    // Populate the ModSet
    for (int k = 0; k < TestSet.size(); k++)
    {
      if (!randFail(tbRng, modRate))
      {
        ModSet.add(TestSet.elementAt(k));
      }
    }
    ot.println("Size of ModSet: " + ModSet.size());
    
    // Modify
    this.timeModifyStart  = this.getBeanCount();
    
    int numAdded    = 0;
    int numRemoved  = 0;
    for (int k = 0; k < ModSet.size(); k++)
    {
      if (!LP2Skip.contains(ModSet.elementAt(k)))
      {
        numAdded++;
        LP2Skip.add(ModSet.elementAt(k));
      }
      else
      {
        numRemoved++;
        LP2Skip.remove(ModSet.elementAt(k));
      }
    }
    ot.println();
    ot.println("----*----*----");
    ot.println(String.format("Number objects added:   % 6d", numAdded));
    ot.println(String.format("Number objects removed: % 6d", numRemoved));
    ot.println("----*----*----");
    ot.println();
    
    this.timeModifyEnd    = this.getBeanCount();    
  }

  /**
   * Performs the final functions of a given Bench run.
   * 
   * @Note Calls outResults to report on all the metrics calculated, 
   *        which is the point of this ENTIRE PROJECT!
   * @Note *Cops are here* Time to shut this (Bench)party down! Boo!
   */
  private void shutdown()
  {
    ot.println();
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println("        Results        ");
    ot.println("*-*-*-*-*-*-*-*-*-*-*-*");
    ot.println();
    
    this.timeCreateTotal = calcCreateTime();
    this.timeVerifyTotal = calcVerifyTime();
    this.timeModifyTotal = calcModifyTime();
    
    this.outResults();
  }
  /**
   * Outputs the empirically calculated metrics for the objects used in this JTestBench
   * 
   * @Note This is literally the ENTIRE POINT of this Honors Project!
   */
  private void outResults()
  {
    String timeString = "";
    
    int x = DURATION_TIMESCALE_QUOTIENT;
    if      (x ==             1) {timeString = " (nanoseconds)";}
    else if (x ==         1_000) {timeString = " (microseconds)";}
    else if (x ==     1_000_000) {timeString = " (miliseconds)";}
    else if (x == 1_000_000_000) {timeString = " (seconds)";}

    ot.println(String.format("Creation time:     %1$ 8d", this.timeCreateTotal) + timeString);
    ot.println(String.format("Verification time: %1$ 8d", this.timeVerifyTotal) + timeString);
    ot.println(String.format("Modification time: %1$ 8d", this.timeModifyTotal) + timeString);
    ot.println();
    ot.println("*******************************************************************************************");
    ot.println();
    ot.println();
    ot.println();
    ot.println();
    ot.println();
    ot.println();
  }
  /**
   * A simple method to timestamp various points of this 
   * JTestBench object's execution, so as to enable metrics to be calculated empirically.
   * 
   * @return (long) The total CPU time for the current thread if CPU timemeasurement is enabled; -1 otherwise.
   * @Note The @return desription was lifted from the ThreadMXBean Javadocs
   */
  private long getBeanCount()
  {
    return myBenchBean.getCurrentThreadCpuTime();
  }
  /**
   * A CRUCIAL part of the .doCreate() method, enabling a specific fraction of The Data Set to be EXCLUDED from the Test Set. 
   * 
   * @param rng (Random) The initialized Random object passed around 
   *        to allow a carefully curated set of random values, enabling repitiion of data sets
   * @param myFailRate (double) The probibalistic rate at which objects in The Data Set are excluded from the Test Set
   * @return (boolean) A boolean vaule used to stochasticaly exclude data in The Data Set from the Test Set
   * 
   * @Note Trivia: I am very deeply ashamed that I had to look this algorithm up on a Stack Exchange website:
   * URI: https://stackoverflow.com/questions/17359834/random-boolean-with-weight-or-bias   * 
   */
  public static boolean randFail(Random rng, double myFailRate)
  {
    boolean itFailed;
    double rate = myFailRate / 100.0;
    
    itFailed = rng.nextDouble() > rate;
    
    return itFailed;
  }
}

/* ****************************************************************************************************************************************/
/* **** Archived code, for future reference: **********************************************************************************************/
/* ****************************************************************************************************************************************/

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
//
//private void doVerify()
//{
//  numFails = 0; // 
//  
//  boolean     inSet;
//  JTheDataSetObject tob = null;
//
//  switch (myBenchSet)
//  {
//    case LBloomGARSLP2:
//    {
//      long numSkipFails = 0;
//      long numBloomFails = 0;
//      ListIterator<JTheDataSetObject> iT = TestSet.listIterator();
//      while (iT.hasNext())
//      {
//        tob = null;
//        tob = iT.next();
//        ot.println(tob);
//        if (lBF.contains(tob)) // If in Bloom Filter
//        {
//          // Then find it in the Skip List
//          inSet = sL.contains(tob);
//          if (!inSet)
//          {
//            //ot.println(inSet);
//            numSkipFails++;
//          }
//        }
//        else
//        {
//          numBloomFails++;
//        }
//      }
//      ot.println("-------");
//      sL.printList();
//      ot.println("--------");
//      ot.println();
//      ot.println("Number of objects outside of (Lovasoa) The Data Set Skip List:    " + numSkipFails);
//      ot.println("Number of objects outside of (Lovasoa) The Data Set Bloom Filter: " + numBloomFails);
//      
//      break;  
//    }
//    case LBloomMGRSLP2:
//    {
//      break;
//    }
//    case SBloomGARSLP2:
//    {
//      long numSkipFails = 0;
//      long numBloomFails = 0;
//      ListIterator<JTheDataSetObject> iT = TestSet.listIterator();
//      while (iT.hasNext())
//      {
//        tob = null;
//        tob = iT.next();
//        ot.println(tob);
//        if (sBF.contains(tob)) // If in Bloom Filter
//        {
//          // Then find it in the Skip List
//          inSet = sL.contains(tob);
//          if (!inSet)
//          {
//            //ot.println(inSet);
//            numSkipFails++;
//          }
//        }
//        else
//        {
//          numBloomFails++;
//        }
//      }
//      ot.println("-------");
//      sL.printList();
//      ot.println("--------");
//      ot.println();
//      ot.println("Number of objects outside of (Sangupta) The Data Set Skip List:    " + numSkipFails);
//      ot.println("Number of objects outside of (Sangupta) The Data Set Bloom Filter: " + numBloomFails);
//      break;
//    }
//    case SBloomMGRSLP2:
//    {
//      break;
//    }
//  }
//}

/*
--------------------------------------------------------------------------------
----  The blank lines below are to aid me in keeping the last line of this  ----
----  file vertically centered and above the very bottom of my very large   ---- 
----  monitor.                                                              ----
--------------------------------------------------------------------------------
*/






























































































