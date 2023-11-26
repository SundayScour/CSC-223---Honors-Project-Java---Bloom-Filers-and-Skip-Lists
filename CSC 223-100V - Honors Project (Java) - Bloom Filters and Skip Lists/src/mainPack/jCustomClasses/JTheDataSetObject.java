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
    theRandomizer(inType, rng);
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
    
    this.myPay  = "" + makeRandomName(rng) + " " + makeRandomName(rng) + " " + makeRandomName(rng);
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
  
  public void theRandomizer(JHashType inType, Random rng)
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
    
    this.myPay  = "" + makeRandomName(rng) + " " + makeRandomName(rng) + " " + makeRandomName(rng);
    this.myHashType = inType;
    this.isValid = true;
  }
  
  public static JTheDataSetObject makeBad()
  {
    JTheDataSetObject BadOne = new JTheDataSetObject();
    return BadOne;
  }
  
  @Override
  public String toString()
  {
    return ("" + "Pay: " + myPay + " --- " + "Valid: " + isValid);
    /*

    return "JTheDataSetObject [myLat=" + myLat + ", myLon=" + myLon + ", myAlt="
        + myAlt + ", myGARS=" + myGARS + ", myMGRS=" + myMGRS + ", myPay="
        + myPay + ", myHashType=" + myHashType + ", isValid=" + isValid + "]";
        
    */
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
  private String makeRandomName(Random rng)
  {
    //Random rnd = new Random();
    int x = -1;
    String[] names = {"Alice",
                      "Andrew",
                      "Barbara",
                      "Bob",
                      "Clara",
                      "Cody",
                      "Denise",
                      "Daniel",
                      "Erica",
                      "Eren",
                      "Frauline",
                      "Franklin",
                      "Gwen",
                      "Gaige",
                      "Hazel",
                      "Henry",
                      "Ingrid",
                      "Ichiro",
                      "Jasmine",
                      "Jon",
                      "Karen",
                      "Keith",
                      "Lauren",
                      "Langston",
                      "Melody",
                      "Maximillion",
                      "Nadia",
                      "Nathan",
                      "Ophalia",
                      "Oberon",
                      "Pixie",
                      "Paul",
                      "Quinn",
                      "Quill",
                      "Robin",
                      "Roger",
                      "Svetlana",
                      "Slade",
                      "Toya",
                      "Tim",
                      "Usha",
                      "Uriel",
                      "Veronica",
                      "Viggo",
                      "Winona",
                      "Wies",
                      "Xochitl",
                      "Xander",
                      "Ylva",
                      "Yoshihiro",
                      "Zelda",
                      "Zenon"};
    x = rng.nextInt(51);
    return names[x];    
  }
}
