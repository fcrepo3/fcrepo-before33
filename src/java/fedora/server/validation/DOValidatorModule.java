package fedora.server.validation;

// Fedora imports
import fedora.server.errors.ServerException;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.Module;
import fedora.server.Server;

// Java imports
import java.util.Map;
import java.io.InputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * <p><b>Title:</b> DOValidatorModule.java</p>
 * <p><b>Description:</b> Module Wrapper for DOValidatorImpl.java.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DOValidatorModule extends Module implements DOValidator
{

  /**
   * An instance of the core implementation class for DOValidator.
   * The DOValidatorModule acts as a wrapper to this class.
   */
  private DOValidatorImpl dov = null;

  /**
   * <p>Constructs a new DOValidatorModule</p>
   *
   * @param moduleParameters The name/value pair map of module parameters.
   * @param server The server instance.
   * @param role The module role name.
   * @throws ModuleInitializationException If initialization values are
   *         invalid or initialization fails for some other reason.
   * @throws ServerException
   */
    public DOValidatorModule(Map moduleParameters, Server server, String role)
          throws ModuleInitializationException, ServerException
    {
        super(moduleParameters, server, role);
    }

  public void postInitModule() throws ModuleInitializationException
  {
    try
    {	
		HashMap xmlSchemaMap=new HashMap();
		HashMap ruleSchemaMap=new HashMap();
		String tempDir=null;
		String schematronPreprocessorPath=null;
		Iterator nameIter=parameterNames();
		while (nameIter.hasNext()) {
			String paramName=(String) nameIter.next();
			if (paramName.startsWith("xsd_")) {
				String xmlSchemaName=paramName.substring(4);
				try {
					String xmlSchemaPath = new File(getServer().getHomeDir(),
						getParameter(paramName)).getPath();
					xmlSchemaMap.put(xmlSchemaName, xmlSchemaPath);
					logFiner("[DOValidatorModule] initialized XML Schema "
							+ "location: " + xmlSchemaPath);
				} catch (Exception e) {
					throw new ModuleInitializationException(
							"Problem configuring XML Schema for format="
							+ xmlSchemaName + " : " +
							e.getClass().getName() + ": " + e.getMessage(),
							getRole());
				}
			} else if (paramName.startsWith("rules_")) {
				String ruleSchemaName=paramName.substring(6);
				try {
					String ruleSchemaPath = new File(getServer().getHomeDir(),
						getParameter(paramName)).getPath();
					ruleSchemaMap.put(ruleSchemaName, ruleSchemaPath);
					logFiner("[DOValidatorModule] initialized Schematron schema "
							+ "location: "  + ruleSchemaPath);
				} catch (Exception e) {
					throw new ModuleInitializationException(
							"Problem configuring Schematron Schema for format="
							+ ruleSchemaName + " : " +
							e.getClass().getName() + ": " + e.getMessage(),
							getRole());
				}
			} else if (paramName.equals("tempDir")){
				tempDir = new File(getServer().getHomeDir(),
					getParameter(paramName)).getPath();
				logFiner("[DOValidatorModule] tempDir set to: "
						+ tempDir);
			} else if (paramName.equals("schtron_preprocessor")){
				schematronPreprocessorPath = new File(getServer().getHomeDir(),
					getParameter(paramName)).getPath();
				logFiner("[DOValidatorModule] initialized Schematron "
						+ "preprocessor location: " + schematronPreprocessorPath);
			}
		} 
			        
      	// FINALLY, instantiate the validation module implementation class
      	dov = new DOValidatorImpl(tempDir, xmlSchemaMap, 
      			schematronPreprocessorPath,
				ruleSchemaMap, this);
    }
    catch(Exception e)
    {
      throw new ModuleInitializationException(
          e.getMessage(),"fedora.server.validation.DOValidatorModule");
    }
  }

  /**
   * <p>Validates a digital object.</p>
   *
   * @param objectAsStream The digital object provided as a bytestream.
   * @param validationType The level of validation to perform on the digital
   *        object. This is an integer from 0-2 with the following meanings:
   *        0 = VALIDATE_ALL (do all validation levels)
   *        1 = VALIDATE_XML_SCHEMA (perform only XML Schema validation)
   *        2 = VALIDATE_SCHEMATRON (perform only Schematron Rules validation)
   * @param phase The stage in the work flow for which the
   *        validation should be contextualized.
   *        "ingest" = the object is in the submission format for the
   *                   ingest stage phase
   *        "store" = the object is in the authoritative format for the
   *                  final storage phase
   * @throws ServerException If validation fails for any reason.
   */
  public void validate(InputStream objectAsStream, String format, 
  	int validationType, String phase)
    throws ServerException
  {
    dov.validate(objectAsStream, format, validationType, phase);
  }

  /**
   * <p>Validates a digital object.</p>
   *
   * @param objectAsFile The digital object provided as a file.
   * @param validationType The level of validation to perform on the digital
   *        object. This is an integer from 0-2 with the following meanings:
   *        0 = VALIDATE_ALL (do all validation levels)
   *        1 = VALIDATE_XML_SCHEMA (perform only XML Schema validation)
   *        2 = VALIDATE_SCHEMATRON (perform only Schematron Rules validation)
   * @param phase The stage in the work flow for which the validation
   *        should be contextualized.
   *        "ingest" = the object is in the submission format for the
   *                   ingest stage phase
   *        "store" = the object is in the authoritative format for the
   *                  final storage phase
   * @throws ServerException If validation fails for any reason.
   */
  public void validate(File objectAsFile, String format, 
  	int validationType, String phase)
    throws ServerException
  {
      dov.validate(objectAsFile, format, validationType, phase);
  }
}
