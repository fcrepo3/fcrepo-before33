package fedora.server.access;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class ParmResultSet
{
  public String name = null;
  public String defaultValue = null;
  public String requiredFlag = null;
  public String label = null;
  private static final boolean debug = true;

  public ParmResultSet()
  {}

  /**
   * Constructor that initializes the class variables.
   *
   * @param sqlResults - array containing the results from a dissemination
   * query against the SQL database.
   */
  public ParmResultSet(String[] sqlResults)
  {
      this.name = sqlResults[0];
      this.defaultValue = sqlResults[1];
      this.requiredFlag = sqlResults[2];
      this.label = sqlResults[3];
      if (debug)
      {
        for (int i=0; i<sqlResults.length; i++)
        {
          System.out.println("parmResults["+i+"] = "+sqlResults[i]);
        }
      }
  }

  public static void main(String[] args)
  {

    // initialize array for testing
    String[] sqlResults = new String[4];
    sqlResults[0] = "get_image";
    sqlResults[1] = "default value";
    sqlResults[2] = "N";
    sqlResults[3] = "label for parameter";

    ParmResultSet result = new ParmResultSet(sqlResults);

    System.out.println("Digital Object PID = "+result.name);
    System.out.println("Disseminator Name = "+result.defaultValue);
    System.out.println("Method Name = "+result.requiredFlag);
    System.out.println("Address Location = "+result.label);
  }

}