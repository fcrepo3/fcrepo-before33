package fedora.server.access.internalservices;

import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Properties;
import java.util.Date;

import fedora.server.Context;
import fedora.server.access.Access;
import fedora.server.access.ObjectProfile;
import fedora.server.errors.ServerException;
import fedora.server.errors.DisseminationException;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.storage.DOReader;
import fedora.server.storage.DefinitiveDOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;
import fedora.server.storage.ExternalContentManager;
import fedora.server.utilities.DateUtility;
import fedora.server.Server;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import com.icl.saxon.expr.StringValue;

// FIXIT!  This is duplicate code from the search module.  Can we consolidate
// and have all parts of system use one version
import fedora.server.utilities.XMLConversions.ObjectInfoAsXML;
import fedora.server.utilities.XMLConversions.DatastreamsAsXML;

/**
 * <p>Title: DefaultBehaviorImpl.java</p>
 * <p>Description: Implements the methods defined in the DefaultBehavior interface.
 * This is the default behavior mechanism that implements the "contract" of
 * the default behavior definition that is dynamically associated with every
 * digital object in the repository.
 *
 * This class is considered an "internal service" that is built in to the
 * Fedora system.  Its purpose is to endow every digital object with a set of
 * generic behaviors.  It is an implementation of what is known as the
 * Default Disseminator.
 *
 * Unlike other behavior definitions and mechanisms, there is no Behavior
 * Definition Object or Behavior Mechanism Object stored in the repository. </p>
 *
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette
 * @version 1.0
 */
public class DefaultBehaviorImpl extends InternalService implements DefaultBehavior
{
  private Context context;
  private String PID;
  private Calendar asOfDateTime;
  private DOReader reader;
  private String reposBaseURL;
  private File reposHomeDir;
  private Access m_access;

  public DefaultBehaviorImpl(Context context, Calendar asOfDateTime,
    DOReader reader, Access access, String reposBaseURL, File reposHomeDir)
    throws ServerException
  {
    this.context = context;
    this.asOfDateTime = asOfDateTime;
    this.reader = reader;
    this.m_access = access;
    this.reposBaseURL = reposBaseURL;
    this.reposHomeDir = reposHomeDir;

  }

  /**
   * <p>Returns the key metadata from the object.  The data
   * is returned as XML encoded to the aaaa.xsd schema.</p>
   *
   */
  public MIMETypedStream getObjectProfile() throws ServerException
  {
    ObjectProfile profile = m_access.getObjectProfile(context, reader.GetObjectPID(), asOfDateTime);
    //return new MIMETypedStream("text/xml",
    //  new ObjectInfoAsXML().getObjectProfile(reposBaseURL, profile).getBytes());
    // RLW: change required by conversion fom byte[] to InputStream
    InputStream in = new ByteArrayInputStream(
      (new ObjectInfoAsXML().getObjectProfile(reposBaseURL, profile).getBytes()));
    return new MIMETypedStream("text/xml", in);
  }

  /**
   * <p>Returns the key metadata from the object.  the data
   * is returned as HTML in a presentation-oriented format.</p>
   *
   */
  public MIMETypedStream viewObjectProfile() throws ServerException
  {
    // DO XSLT transform on object info;
    try
    {
      // RLW: change required by conversion fom byte[] to InputStream
      //ByteArrayInputStream in = new ByteArrayInputStream(getObjectProfile().stream);
      InputStream in = getObjectProfile().getStream();
      // RLW: change required by conversion fom byte[] to InputStream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      File xslFile = new File(reposHomeDir, "access/viewObjectProfile.xslt");
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates template = factory.newTemplates(new StreamSource(xslFile));
      Transformer transformer = template.newTransformer();
      Properties details = template.getOutputProperties();
      transformer.transform(new StreamSource(in), new StreamResult(out));
      // RLW: change required by conversion fom byte[] to InputStream
      in = new ByteArrayInputStream(out.toByteArray());
      return new MIMETypedStream("text/html", in);
      //return new MIMETypedStream("text/html", out.toByteArray());
      // RLW: change required by conversion fom byte[] to InputStream
    } catch (TransformerException e)
    {
      throw new DisseminationException("[DefaultBehaviorImpl] had an error "
          + "in transforming xml for viewObjectProfile. "
          + "Underlying exception was: "
          + e.getMessage());
    }
  }

    /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as XML encoded to
   * the bbb.xsd schema.</p>
   */
  public MIMETypedStream getMethodIndex() throws ServerException
  {
    ObjectMethodsDef[] methods =
      m_access.getObjectMethods(context, reader.GetObjectPID(), asOfDateTime);
    // RLW: change required by conversion fom byte[] to InputStream
    InputStream in = new ByteArrayInputStream(
      (new ObjectInfoAsXML().getMethodIndex(
        reposBaseURL, reader.GetObjectPID(), methods).getBytes()));
    return new MIMETypedStream("text/xml", in);
    //return new MIMETypedStream("text/xml",
    //  new ObjectInfoAsXML().getMethodIndex(reposBaseURL, reader.GetObjectPID(), methods).getBytes());
  }

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as HTML in
   * a presentation-oriented format.</p>
   */
  public MIMETypedStream viewMethodIndex() throws ServerException
  {
    try
    {
      //ByteArrayInputStream in = new ByteArrayInputStream(getMethodIndex().stream);
      // RLW: change required by conversion fom byte[] to InputStream
      InputStream in = getMethodIndex().getStream();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      File xslFile = new File(reposHomeDir, "access/objectmethods.xslt");
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates template = factory.newTemplates(new StreamSource(xslFile));
      Transformer transformer = template.newTransformer();
      Properties details = template.getOutputProperties();
      transformer.transform(new StreamSource(in), new StreamResult(out));
      // RLW: change required by conversion fom byte[] to InputStream
      in = new ByteArrayInputStream(out.toByteArray());
      return new MIMETypedStream("text/html", in);
      //return new MIMETypedStream("text/html", out.toByteArray());
      // RLW: change required by conversion fom byte[] to InputStream
    } catch (TransformerException e)
    {
      throw new DisseminationException("[DefaultBehaviorImpl] had an error "
          + "in transforming xml for viewItemIndex. "
          + "Underlying exception was: "
          + e.getMessage());
    }
  }

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as XML encoded to
   * the bbb.xsd schema.</p>
   */
  public MIMETypedStream getItemIndex() throws ServerException
  {
    // FIXIT!! By using this default utility, we get exactly the information
    // that is in the digital object.  The DefaultBehaviorImpl will need to
    // transform this so that the dsLocation has a "public" URL that will
    // go at the datastream mediation servlet.  Also, may want to consider
    // whether we want to filter out inline XML metadata datastreams in this
    // behavior method.
    //return new MIMETypedStream("text/xml",
    //  new DatastreamsAsXML().getDatastreamList(reader, null).getBytes());

    // RLW: change required by conversion fom byte[] to InputStream
    InputStream is = new ByteArrayInputStream(new DatastreamsAsXML().getItemIndex(reposBaseURL, reader, null).getBytes());
    return new MIMETypedStream("text/xml", is);
    //return new MIMETypedStream("text/xml",
    //  new DatastreamsAsXML().getItemList(reposBaseURL, reader, null).getBytes());
    // RLW: change required by conversion fom byte[] to InputStream
  }

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as HTML in
   * a presentation-oriented format.</p>
   */
  public MIMETypedStream viewItemIndex() throws ServerException
  {
    //String temp = new String("<html><head><title>FedoraServlet</title></head>" +
    //  " <body><br></br>DefaultBehaviorImpl: HTML Presentation COMING SOON!</body></html>");
    //return new MIMETypedStream("text/html", temp.getBytes());
    /// Transform results into an html table
    try
    {
      // RLW: change required by conversion fom byte[] to InputStream
      //ByteArrayInputStream in = new ByteArrayInputStream(getItemList().stream);
      InputStream in = getItemIndex().getStream();
      // RLW: change required by conversion fom byte[] to InputStream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      File xslFile = new File(reposHomeDir, "access/viewItemIndex.xslt");
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates template = factory.newTemplates(new StreamSource(xslFile));
      Transformer transformer = template.newTransformer();
      Properties details = template.getOutputProperties();
      transformer.transform(new StreamSource(in), new StreamResult(out));
      // RLW: change required by conversion fom byte[] to InputStream
      in = new ByteArrayInputStream(out.toByteArray());
      return new MIMETypedStream("text/html", in);
      //return new MIMETypedStream("text/html", out.toByteArray());
      // RLW: change required by conversion fom byte[] to InputStream
    } catch (TransformerException e)
    {
      throw new DisseminationException("[DefaultBehaviorImpl] had an error "
          + "in transforming xml for viewItemIndex. "
          + "Underlying exception was: "
          + e.getMessage());
    }
  }

  /**
   * <p>Returns an particular item (datastream) in the object.
   * The item will by a mime-typed stream of bytes.</p>
   *
   * @param  itemID  : the unique identifier for the item in
   * the object in the form of DatastreamID+VersionID.  The
   * item identifer can be discovered via the results of the
   * getItemIndex or viewItemIndex methods.
   */
  public MIMETypedStream getItem(String itemID) throws ServerException
  {
  /*
    Datastream ds = reader.GetDatastream(itemID, null);
    ExternalContentManager externalContentManager = (ExternalContentManager)
    s_server.getModule("fedora.server.storage.ExternalContentManager");

    // FIXIT!! Deal with different kinds of datasteams
    MIMETypedStream dissemination =
      externalContentManager.getExternalContent(ds.DSLocation);
  */

    // FIXIT!! This is temporary.  Will look for redirect in
    // dsControlGrp similar to what's in DisseminationService.
    Datastream ds = reader.GetDatastream(itemID, null);
    ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    InputStream in = ds.getContentStream();
    int byteStream = 0;
    try
    {
    byte[] buffer = new byte[255];
    while ((byteStream = in.read(buffer)) >= 0)
    {
      out.write(buffer, 0, byteStream);
    }
    buffer = null;
    in.close();
    } catch (IOException ioe)
    {
      throw new DisseminationException("[DefaultBehaviorImpl] had an error "
          + "in reading or writing getItem bytestream. "
          + "Underlying exception was: "
          + ioe.getMessage());
    }
    // RLW: change required by conversion fom byte[] to InputStream
    InputStream is = new ByteArrayInputStream(out.toByteArray());
    return new MIMETypedStream(ds.DSMIME, is);
    //return new MIMETypedStream(ds.DSMIME, out.toByteArray());
    // RLW: change required by conversion fom byte[] to InputStream
  }

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as XML encoded to the oaidc.xsd schema.</p>
   *
   */
  public MIMETypedStream getDublinCore() throws ServerException
  {
    Date versionDate = DateUtility.convertCalendarToDate(asOfDateTime);
    DatastreamXMLMetadata dcmd = null;
    try
    {
        dcmd = (DatastreamXMLMetadata) reader.GetDatastream("DC", versionDate);
    }
    catch (ClassCastException cce) {
        throw new ObjectIntegrityException("Object " + reader.GetObjectPID()
                + " has a DC datastream, but it's not inline XML.");
    }

    // RLW: change required by conversion fom byte[] to InputStream
    InputStream is = new ByteArrayInputStream(
      new ObjectInfoAsXML().getOAIDublinCore(dcmd).getBytes());
    return new MIMETypedStream("text/xml", is);
    //return new MIMETypedStream("text/xml",
    //  new ObjectInfoAsXML().getOAIDublinCore(reader).getBytes());
    // RLW: change required by conversion fom byte[] to InputStream
  }

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as HTML in a presentation-oriented format.</p>
   *
   */
  public MIMETypedStream viewDublinCore() throws ServerException
  {
    try
    {
      //ByteArrayInputStream in = new ByteArrayInputStream(getDublinCore().stream);
      InputStream in = getDublinCore().getStream();
      // RLW: change required by conversion fom byte[] to InputStream
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      File xslFile = new File(reposHomeDir, "access/viewDublinCore.xslt");
      TransformerFactory factory = TransformerFactory.newInstance();
      Templates template = factory.newTemplates(new StreamSource(xslFile));
      Transformer transformer = template.newTransformer();
      Properties details = template.getOutputProperties();
      transformer.transform(new StreamSource(in), new StreamResult(out));
      // RLW: change required by conversion fom byte[] to InputStream
      in = new ByteArrayInputStream(out.toByteArray());
      return new MIMETypedStream("text/html", in);
      //return new MIMETypedStream("text/html", out.toByteArray());
      // RLW: change required by conversion fom byte[] to InputStream
    } catch (TransformerException e)
    {
      throw new DisseminationException("[DefaultBehaviorImpl] had an error "
          + "in transforming xml for viewDublinCore. "
          + "Underlying exception was: "
          + e.getMessage());
    }
  }

  public static MethodDef[] reflectMethods()
  {
    ArrayList methodList = new ArrayList();
    // Read in method definitions for Default Disseminator from
    // a source file configurable with the repository.

    // FIXIT!! For now, I am just HACKING in a few method defs for some
    // quick testing.
    //
    // To properly get the default behavior defintion, we can either do java
    // reflection on the interface DefaultBehavior (see technique in original
    // Fedora class cornell.SigGen.Lecture.java) or else we can configure a
    // Method Map xml file with the DynamicAccess module.  The later solution
    // is better since java reflection can't provide all information we want
    // about parameters.

    MethodDef method = null;

    method = new MethodDef();
    method.methodName = "getObjectProfile";
    method.methodLabel = "Get XML-encoded description of the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "viewObjectProfile";
    method.methodLabel = "View description of the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "getMethodIndex";
    method.methodLabel = "Get an XML-encoded list of dissemination methods for the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "viewMethodIndex";
    method.methodLabel = "View a list of dissemination methods in the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "getItemIndex";
    method.methodLabel = "Get an XML-encoded list of items in the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "viewItemIndex";
    method.methodLabel = "View a list of items in the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "getItem";
    method.methodLabel = "Get an item from the object";
    method.methodParms = new MethodParmDef[1];
    method.methodParms[0] = new MethodParmDef();
    method.methodParms[0].parmName = "itemID";
    method.methodParms[0].parmType = MethodParmDef.USER_INPUT;
    method.methodParms[0].parmLabel = "Item identifier in format of datastream ID";
    method.methodParms[0].parmRequired = true;
    method.methodParms[0].parmPassBy = MethodParmDef.PASS_BY_VALUE;
    method.methodParms[0].parmDefaultValue = null;
    method.methodParms[0].parmDomainValues = new String[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "getDublinCore";
    method.methodLabel = "Get an XML-encoded Dublin Core record for the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "viewDublinCore";
    method.methodLabel = "View the Dublin Core record for the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);
    return (MethodDef[])methodList.toArray(new MethodDef[0]);
  }
}
