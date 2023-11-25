package mainPack.jCustomClasses;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

import mainPack.jCustomClasses.*;

public class JTestBench
{
  /*
   * Limits
   */
  public static final byte MIN_POWER = 2;
  public static final byte MAX_POWER = 8;
  
  // Is this a valid instatnce of JTestBench?
  private boolean isValid = false;  
  
  // The bean for counting
  private ThreadMXBean myBenchBean = null;
  
  /*
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
  
  /*
   * Configuration options for each created instance of JTestBench
   */
  
  // The data structure implementations to use
  JBloomType     myBloom = JBloomType.Lovasoa;
  JGridSysType   myGrid  = JGridSysType.GARS;
  JSkipListType  mySkip  = JSkipListType.LP2;
  
  // The orders of magnitude of the size of The Data Set and Test Set. They will all be tried and times recorded.
  private byte powersOf10Start  = 0;
  private byte powersOf10End    = 0;
  private byte powersOf10Range  = 0;
  
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
  
  /**
   * Default constructor. DO NOT USE. Makes an invalid instance.
   */
  public JTestBench()
  {
    this.isValid = false;
    this.myBenchBean = null;
  }
  
  public JTestBench (JBloomType bT, JGridSysType gS, JSkipListType sL, byte startP, byte endP)
  {
    this.myBenchBean      = ManagementFactory.getThreadMXBean();
    this.myBloom          = bT;
    this.myGrid           = gS;
    this.mySkip           = sL;
    this.powersOf10Start  = startP;
    this.powersOf10End    = endP;
    this.powersOf10Range  = rectifyPowers(powersOf10End, powersOf10Start);
    
    this.isValid          = true;
  }
  
  public boolean isValid()
  {
    return this.isValid;
  }
  
  private long calcCreateTime()
  {
    return this.timeCreateEnd - this.timeCreateStart;
  }
  
  private long calcVerifyTime()
  {
    return this.timeVerifyEnd - this.timeVerifyStart;
  }
  
  private long calcModifyTime()
  {
    return this.timeModifyEnd - this.timeModifyStart;
  }
  
  private void startUp()
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
  }
  
  private void doCreate()
  {
    // TODO Auto-generated method stub
    
  }

  private void doVerify()
  {
    // TODO Auto-generated method stub
    
  }

  private void doModify()
  {
    // TODO Auto-generated method stub
    
  }

  private void shutDown()
  {
    this.timeCreateTotal = calcCreateTime();
    this.timeVerifyTotal = calcVerifyTime();
    this.timeModifyTotal = calcModifyTime();    
  }
  
  private long getBeanCount()
  {
    return myBenchBean.getCurrentThreadCpuTime();
  }
}

/*
--------------------------------------------------------------------------------
----  The blank lines below are to aid me in keeping the last line of this  ----
----  file vertically centered and above the very bottom of my very large   ---- 
----  monitor.                                                              ----
--------------------------------------------------------------------------------
*/


















































































