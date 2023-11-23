package mainPack.jCustomClasses;

import java.util.Random;
import java.util.Formatter;
import importedImplementations.GARS.LLtoGARS;
import importedImplementations.GARS.GARStoLL;
import importedImplementations.NGA_MGRS.mgrs.MGRS;
import importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point;


public class JTheDataSetObject
{
  /**
   * The two types of Strings an object can pass into the hashing functions 
   */
  public enum JHashType
  {
    GARS,
    MGRS,
    LATLON
  }
  
  /**
   * The object's Latitude, in decimal degrees.
   */
  private double myLat;
  
  /**
   * The object's Longitude, in decimal degrees.
   */
  private double myLon;
  
  /**
   * The object's Altitude, measured in meters.
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
  
  /**
   * A proper constructor that makes a valid JTheDataSetObject object.
   * 
   * @param inLat
   *        Latitude of created object
   * @param inLon
   *        Longitude of created object
   */
  public JTheDataSetObject(double inLat, double inLon, int inAlt, JHashType inType)
  {
    this.myLat  = inLat;
    this.myLon  = inLon;
    this.myAlt  = inAlt;
    this.myGARS = LLtoGARS.getGARS(myLat, myLon);
    
    // Temporary objects used only to construct the MGRS object and get its code as a String.
    importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point tmpPoint = 
        new importedImplementations.NGA_MGRS.mgrs.mil.nga.grid.features.Point(myLat, myLon);
    MGRS tmpGARS = MGRS.from(tmpPoint);
    
    this.myMGRS = tmpGARS.toString();
    
    this.myPay  = "" + makeRandomName() + " " + makeRandomName() + " " + makeRandomName();
    this.myHashType = inType;
    this.isValid = true;
  }
  
  /**
   * 
   * @return 
   */
  public String toHashString()
  {
    String strOut = "";
    String strMyAlt = String.format("%07d", this.getMyAlt());
    
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
   * Helper function copied from previous semester's Final Project:
   * "Generalized RPS Game"
   * 
   * (See https://github.com/SundayScour/Generalized-RPS-Game for full source and JavaDocs.)
   * 
   * @return String
   *         One random name from the hardcoded set of 52 possible names: 26 female, 26 male, both A to Z. 
   */
  private String makeRandomName()
  {
    Random rnd = new Random();
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
    x = rnd.nextInt(51);
    return names[x];    
  }
}
