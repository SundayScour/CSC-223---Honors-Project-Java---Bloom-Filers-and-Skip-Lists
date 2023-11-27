/**
 * @Project: CSC 223-100V - Honors Project (Java) - Bloom Filters and Skip Lists
 * 
 * @FileName:                 JTheDataSetObject.java
 * @OriginalFileCreationDate: Nov 25, 2023
 * 
 * @Author:           Jonathan Wayne Edwards
 * @GitHubUserName:   SundayScour
 * @GutHubUserEmail:  sunday_scour@aol.com
 * 
 * @EnclosingPackage: mainPack.jCustomClasses
 * @ClassName:        JTheDataSetObject
 */

package mainPack.jCustomClasses;

import java.util.Random;
import java.util.Formatter;
import importedImplementations.GARS.LLtoGARS;
import importedImplementations.GARS.GARStoLL;
import importedImplementations.NGA_MGRS.mgrs.MGRS;
import importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point;
import importedImplementations.TinSpinIndexes.index.PointMap;
import importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry;
import importedImplementations.TinSpinIndexes.index.util.PointMapWrapper;
import mainPack.jCustomClasses.*;

/**
 * 
 */
public class JTheDataSetObject implements Comparable<JTheDataSetObject>
{
  // Min., max. for Latitude (in decimal degrees)
  public static final double  MIN_LAT =    -90.0;
  public static final double  MAX_LAT =     90.0;
  public static final double  RNG_LAT = MAX_LAT - MIN_LAT;
  
  // Min., max. for Longitude (in decimal degrees)
  public static final double  MIN_LON =   -180.0;
  public static final double  MAX_LON =    180.0;
  public static final double  RNG_LON = MAX_LON - MIN_LON;
  
  // Min., max. for Altitude (in meters)
  public static final int     MIN_ALT =        0;
  public static final int     MAX_ALT =  999_999; // 0 to 999 Kilometers
  public static final int     RNG_ALT = MAX_ALT - MIN_ALT;
  
  
  
  /**
   * The object's Latitude, in decimal degrees.
   * @range -90.0º <= myLat <= 90.0º
   */
  private double myLat;
  
  /**
   * The object's Longitude, in decimal degrees.
   * @range -180.0º <= myLon < 180.0º
   */
  private double myLon;
  
  /**
   * The object's Altitude, measured in meters.
   * @range 0 meters <= myAlt <= 999999 meters.
   */
  private int myAlt;
  
  /**
   *  The object's Latitude and Longitude encoded as a GARS code String.
   */
  private String myGARS;
  
  /***
   * The object's Latitude and Longitude encoded as an MGRS code String.
   */
  private String myMGRS;
  
  /***
   * The object's Payload: Three random names concatenated into a single String, representing identifing object data.
   */
  private String myPay;
  
  /**
   * Which of the object's Strings to hash into the Bloom Filters and into the Skip Lists 
   */
  private JHashType myHashType;
  
  /**
   * Whether the generated instance of JTheDataSetObject is valid or not.
   */
  public boolean isValid;
  
  /**
   * Whether to make a 
   */
  private boolean failMe;
  
  /**
   * Default constructor. Does NOT MAKE a valid JTheDataSetObject object.
   */
  public JTheDataSetObject()
  {
    myLat   = -0.0;
    myLon   = -0.0;
    myAlt   = -1;
    myGARS  = "_INVALID_";
    myMGRS  = "_INVALID_";
    myPay   = "_INVALID_";
    isValid = false;
  }
  
  public JTheDataSetObject(JHashType inType, Random rng)
  {
    theRandomizer(inType, rng, false);
    isValid = true;
  }
  
  public JTheDataSetObject(JHashType inType, Random rng, boolean failMe)
  {
    if (!failMe) {failMe=true;} // There's really only one way to fail... ;)
    theRandomizer(inType, rng, failMe);
    isValid = true;    
  }
  
  /**
   * A proper constructor that makes a valid JTheDataSetObject object.
   * 
   * @param inLat
   *        Latitude of created object
   * @param inLon
   *        Longitude of created object
   */
  public JTheDataSetObject(double inLat, double inLon, int inAlt, JHashType inType, Random rng)
  {
    this.myLat  = inLat;
    this.myLon  = inLon;
    this.myAlt  = inAlt;
    this.enforceRanges();
    
    this.myGARS = LLtoGARS.getGARS(myLat, myLon);
    
    // Temporary objects used only to construct the MGRS object and get its code as a String.
    importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point tmpPoint = 
        new importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point(myLat, myLon);
    MGRS tmpGARS = MGRS.from(tmpPoint);
    
    this.myMGRS = tmpGARS.toString();
    
    this.myPay  = makeRandomName(rng, false) + makeRandomName(rng, false);
    this.myHashType = inType;
    this.isValid = true;
  }
  
  /** 
   * Enforce ranges using constants
   */
  private void enforceRanges()
  {
    if (myLat < MIN_LAT)
    {
      myLat = MIN_LAT;
    }
    else if (myLat > MAX_LAT)
    {
      myLat = MAX_LAT;
    }
    
    if (myLon < MIN_LON)
    {
      myLon = MIN_LON;
    }
    else if (myLon > MAX_LON)
    {
      myLon = MAX_LON;
    }
    
    if (myAlt < MIN_ALT)
    {
      myAlt = MIN_ALT;
    }
    else if (myAlt > MAX_ALT)
    {
      myAlt = MAX_ALT;
    }
  }

  @Override
  public int compareTo(JTheDataSetObject o)
  {
    int retVal;
    
    if (this.myLon < o.myLon)
    {
      retVal = -1;
    }
    else if (this.myLon > o.myLon)
    {
      retVal = 1;
    }
    else
    {
      if (this.myLat < o.myLat)
      {
        retVal = -1;
      }
      else if (this.myLat > o.myLat)
      {
        retVal = 1;
      }
      else
      {
        retVal = 0;
      }
    }
    
    return retVal;
  }

  
  public void setHashType(JHashType newType)
  {
    this.myHashType = newType;
  }
    
  /**
   * 
   * @return The Hash String, unique to the object, used as key in Bloom Filters and Skip Lists
   */
  public String toHashString()
  {
    String strOut = "";
    String strMyAlt = String.format("%06d", this.getMyAlt());
    
    switch (myHashType)
    {
      case GARS:
      {
        strOut = this.getMyGARS() + strMyAlt;
        break;
      }
      case MGRS:
      {
        strOut = this.getMyMGRS() + strMyAlt;
        break;
      }
      case LATLON:
      {
        strOut = "" + this.getMyLat() + this.getMyLon() + strMyAlt;
        break;
      }
      default:
      {
        break;
      }
    }
    return strOut;
  }
  
//  @ Override
//  public String toString()
//  {
//    String strOut = "";
//    
//    return strOut;
//  }
  

  // Eclipse generated getters and setters:
  /**
   * @return the myLat field
   */
  public double getMyLat()
  {
    return myLat;
  }

  /**
   * @return the myLon field
   */
  public double getMyLon()
  {
    return myLon;
  }

  /**
   * @return the myAlt field
   */
  public int getMyAlt()
  {
    return myAlt;
  }

  /**
   * @return the myGARS field
   */
  public String getMyGARS()
  {
    return myGARS;
  }

  /**
   * @return the myMGRS field
   */
  public String getMyMGRS()
  {
    return myMGRS;
  }

  /**
   * @return the myPay field
   */
  public String getMyPay()
  {
    return myPay;
  }

  /**
   * @return the isValid field
   */
  public boolean isValid()
  {
    return isValid;
  }
  
  /**
   * Make a random JTheDataSetObject, based on a calibrated Random object parameter: rng 
   * 
   * @param inType -  What the hash code should be generated from: GARS, MGRS, Lat/Lon
   * @param rng  - The calibrated Random object from which to generate random values
   * @param failMe  - Whether this is an object for tmpObjFail or just plain tmpObj
   */
  public void theRandomizer(JHashType inType, Random rng, boolean failMe)
  {
    this.myLat  = MIN_LAT + RNG_LAT * rng.nextDouble();
    this.myLon  = MIN_LON + RNG_LON * rng.nextDouble();
    this.myAlt  = MIN_ALT + RNG_ALT * rng.nextInt();
        
    this.myGARS = LLtoGARS.getGARS(myLat, myLon);
    
    // Temporary objects used only to construct the MGRS object and get its code as a String.
    importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point tmpPoint = 
        new importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point(myLat, myLon);
    MGRS tmpGARS = MGRS.from(tmpPoint);
    
    this.myMGRS = tmpGARS.toString();
    
    this.myPay  = "" + makeRandomName(rng, failMe) + " " + makeRandomName(rng, failMe);
    this.myHashType = inType;
    this.isValid = failMe;
  }
  
  
  @Override
  public String toString()
  {
//    return ("" + "Pay: " + myPay + " --- " + "Valid: " + isValid);
    

    return "JTheDataSetObject [myLat=" + myLat + ", myLon=" + myLon + ", myAlt="
        + myAlt + ", myGARS=" + myGARS + ", myMGRS=" + myMGRS + ", myPay="
        + myPay + ", myHashType=" + myHashType + ", isValid=" + isValid + "]";
        
    
  }
  
  public RTreeEntry<String> toRTreePoint()
  {
    double alt = (double)this.myAlt;
    double myEntry[] = {this.myLat, this.myLon, alt};
//    return importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry.createPoint(myEntry, null);
    return importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry.createPoint(myEntry, myPay);
    
    
//    return importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry.createPoint(myEntry, <double[]>);
//    return importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry.createPoint(myEntry, <double[]>);
//    return importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry.createPoint(myEntry, <double[]>);
//    return importedImplementations.TinSpinIndexes.index.rtree.RTreeEntry.createPoint(myEntry, <double[]>);
  }


  /**
   * Helper function copied from previous semester's Final Project:
   * "Generalized RPS Game"
   * 
   * (See https://github.com/SundayScour/Generalized-RPS-Game for full source and JavaDocs.)
   * 
   * @return String
   *         One random name from the hardcoded set of 52 possible names: 26 female, 26 male, both A to Z. 
   */
  private String makeRandomName(Random rng, boolean failMe)
  {
    String outStr = "";
    if (failMe)
    {
      int x = -1;
      String[] names = {"FAilice  ", "FAilmanda",
                        "ZenoFail ", "Jupiter  "}; 
      x = rng.nextInt(4);
      outStr = names[x];      
    }
    else
    {
      int x = -1;
      String[] names = {"Alice    ", "Amanda  ",
                        "Andrew   ", "Adaman  ",
                        "Barbara  ", "Beatris ",
                        "Bob      ", "Brandon ",
                        "Clara    ", "Connie  ",
                        "Cody     ", "Charles ",
                        "Denise   ", "Dana    ",
                        "Daniel   ", "Douglas ",
                        "Erica    ", "Evelyn  ",
                        "Eren     ", "Edward  ",
                        "Frauline ", "Feorie  ",
                        "Franklin ", "Fred    ",
                        "Gwen     ", "Ginny   ",
                        "Gaige    ", "Graber  ",
                        "Hazel    ", "Hanna   ",
                        "Henry    ", "Hank    ",
                        "Ingrid   ", "Ia      ",
                        "Ichiro   ", "Ian     ",
                        "Jasmine  ", "Jody    ",
                        "Jon      ", "Jeremy  ",
                        "Karen    ", "Kat     ",
                        "Keith    ", "Kenny   ",
                        "Lauren   ", "Lisa    ",
                        "Langston ", "Larold  ",
                        "Melody   ", "MeiMei  ",
                        "Maximan  ", "Mathew  ",
                        "Nadia    ", "Naani   ",
                        "Nathan   ", "Nerd    ",
                        "Ophalia  ", "Oppai   ",
                        "Oberon   ", "Othello ",
                        "Pixie    ", "Posh    ",
                        "Paul     ", "Potter  ",
                        "Quinn    ", "Quiana  ",
                        "Quill    ", "Quang   ",
                        "Robin    ", "Rachael ",
                        "Roger    ", "Richard ",
                        "Svetlana ", "Sally   ",
                        "Slade    ", "Samwise ",
                        "Toya     ", "Theresa ",
                        "Tim      ", "Tony    ",
                        "Usha     ", "Uma     ",
                        "Uriel    ", "Ulrich  ",
                        "Veronica ", "Valerie ",
                        "Viggo    ", "Victor  ",
                        "Winona   ", "Wanda   ",
                        "Wies     ", "Westley ",
                        "Xochitl  ", "Xi      ",
                        "Xander   ", "Xriss   ",
                        "Ylva     ", "Yaaya   ",
                        "Yoshiro  ", "Yyuman  ",
                        "Zelda    ", "Zeyphr  ",
                        "Zenon    ", "Zeus    "}; 
      x = rng.nextInt(102);
      outStr = names[x];
    }
    return outStr;
  }
    
}
