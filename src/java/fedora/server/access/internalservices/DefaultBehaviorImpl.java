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

import fedora.server.Context;
import fedora.server.errors.ServerException;
import fedora.server.errors.DisseminationException;
import fedora.server.errors.InitializationException;
import fedora.server.errors.ServerInitializationException;
import fedora.server.storage.DOReader;
import fedora.server.storage.DefinitiveDOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.MethodDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.MIMETypedStream;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.Property;
import fedora.server.storage.ExternalContentManager;
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
  private DOReader reader;
  private String reposBaseURL;

  public DefaultBehaviorImpl(DOReader objectReader, String repositoryBaseURL)
    throws ServerException
  {
    reader = objectReader;
    reposBaseURL = repositoryBaseURL;
  }

  /**
   * <p>Returns the key metadata from the object.  The data
   * is returned as XML encoded to the aaaa.xsd schema.</p>
   *
   */
  public MIMETypedStream getObjectProfile() throws ServerException
  {
    return new MIMETypedStream("text/xml",
      new ObjectInfoAsXML().getObjectProfile(reader).getBytes());
  }

  /**
   * <p>Returns the key metadata from the object.  the data
   * is returned as HTML in a presentation-oriented format.</p>
   *
   */
  public MIMETypedStream viewObjectInfo() throws ServerException
  {
    // DO XSLT transform on infoXML;
/*
    String infoXML = new ObjectInfoAsXML().getObjectProfile(reader);
    ByteArrayInputStream in = new ByteArrayInputStream(infoXML.getBytes());
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    File xslFile = new File(s_server.getHomeDir(), "access/objectinfo.xslt");
    TransformerFactory factory = TransformerFactory.newInstance();
    Templates template = factory.newTemplates(new StreamSource(xslFile));
    Transformer transformer = template.newTransformer();
    Properties details = template.getOutputProperties();
    transformer.setParameter("serverURI", new StringValue(serverURI));
    transformer.setParameter("dummy", new StringValue("dummy"));
    transformer.transform(new StreamSource(in), new StreamResult(out));
    return new MIMETypedStream("text/html", out.toByteArray());
    */
    String temp = new String("<html><head><title>FedoraServlet</title></head>" +
      " <body><br></br>DefaultBehaviorImpl: HTML Presentation COMING SOON!</body></html>");
    return new MIMETypedStream("text/html", temp.getBytes());
  }

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as XML encoded to
   * the bbb.xsd schema.</p>
   */
  public MIMETypedStream getItemList() throws ServerException
  {
    // FIXIT!! By using this default utility, we get exactly the information
    // that is in the digital object.  The DefaultBehaviorImpl will need to
    // transform this so that the dsLocation has a "public" URL that will
    // go at the datastream mediation servlet.  Also, may want to consider
    // whether we want to filter out inline XML metadata datastreams in this
    // behavior method.
    //return new MIMETypedStream("text/xml",
    //  new DatastreamsAsXML().getDatastreamList(reader, null).getBytes());

    return new MIMETypedStream("text/xml",
      new DatastreamsAsXML().getItemList(reposBaseURL, reader, null).getBytes());
  }

  /**
   * <p>Returns the list of items (datastreams) in the object.
   * The item list information is returned as HTML in
   * a presentation-oriented format.</p>
   */
  public MIMETypedStream viewItemList() throws ServerException
  {
    String temp = new String("<html><head><title>FedoraServlet</title></head>" +
      " <body><br></br>DefaultBehaviorImpl: HTML Presentation COMING SOON!</body></html>");
    return new MIMETypedStream("text/html", temp.getBytes());
  }

  /**
   * <p>Returns an particular item (datastream) in the object.
   * The item will by a mime-typed stream of bytes.</p>
   *
   * @param  itemID  : the unique identifier for the item in
   * the object in the form of DatastreamID+VersionID.  The
   * item identifer can be discovered via the results of the
   * getItemList or viewItemList methods.
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
    while ((byteStream = in.read()) >= 0)
    {
      out.write(byteStream);
    }
    in.close();
    } catch (IOException ioe)
    {
      throw new DisseminationException("[DefaultBehaviorImpl] had an error "
          + "in reading or writing getItem bytestream. "
          + "Underlying exception was: "
          + ioe.getMessage());
    }
    return new MIMETypedStream(ds.DSMIME, out.toByteArray());
  }

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as XML encoded to the oaidc.xsd schema.</p>
   *
   */
  public MIMETypedStream getDublinCore() throws ServerException
  {
    return new MIMETypedStream("text/xml",
      new ObjectInfoAsXML().getOAIDublinCore(reader).getBytes());
  }

  /**
   * <p>Returns the Dublin Core record for the object, if one exists.
   * The record is returned as HTML in a presentation-oriented format.</p>
   *
   */
  public MIMETypedStream viewDublinCore() throws ServerException
  {
    String temp = new String("<html><head><title>FedoraServlet</title></head>" +
      " <body><br></br>DefaultBehaviorImpl: HTML Presentation COMING SOON!</body></html>");
    return new MIMETypedStream("text/html", temp.getBytes());
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
    method.methodName = "viewObjectInfo";
    method.methodLabel = "View description of the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "getItemList";
    method.methodLabel = "Get an XML-encoded list of items in the object";
    method.methodParms = new MethodParmDef[0];
    methodList.add(method);

    method = new MethodDef();
    method.methodName = "viewItemList";
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