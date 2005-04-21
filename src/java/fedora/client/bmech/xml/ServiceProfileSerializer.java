package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

/**
 *
 * <p><b>Title:</b> ServiceProfileSerializer.java</p>
 * <p><b>Description:</b> </p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ServiceProfileSerializer
{
  private static final String FSVP =
    "http://fedora.comm.nsdlib.org/service/profile";

  private static final String XMLNS = "http://www.w3.org/2000/xmlns/";

  private Document document;

  public ServiceProfileSerializer(BMechTemplate newBMech) throws BMechBuilderException
  {
    createDOM();
    genServiceProfile(newBMech);
  }

  private void createDOM() throws BMechBuilderException
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try
    {
        DocumentBuilder builder =   factory.newDocumentBuilder();
        document = builder.newDocument();

    }
    catch (ParserConfigurationException pce)
    {
      // Parser with specified options can't be built
      pce.printStackTrace();
      throw new BMechBuilderException("ServiceProfileGenerator: error configuring parser."
        + "Underlying exception: " + pce.getMessage());
    }
  }

  private void genServiceProfile(BMechTemplate newBMech)
  {
    ServiceProfile profile = newBMech.getServiceProfile();

    Element root = (Element)document.createElementNS(FSVP, "fsvp:serviceProfile");
    root.setAttributeNS(XMLNS, "xmlns:fsvp", FSVP);
    String bDefPID = (newBMech.getbDefContractPID() == null) ? "" : newBMech.getbDefContractPID();
    root.setAttribute("bDefPID", bDefPID);
	String name = (profile.serviceName == null) ? "" : profile.serviceName;
	root.setAttribute("name", name);
    document.appendChild(root);
	
	Element serviceLabel = document.createElementNS(FSVP, "fsvp:serviceDescription");
	String label = (profile.serviceLabel == null) ? "" : profile.serviceLabel;
	serviceLabel.appendChild(document.createTextNode(label));
	
	Element serviceTestURL = document.createElementNS(FSVP, "fsvp:serviceLiveTestURL");
	String testURL = (profile.serviceTestURL == null) ? "" : profile.serviceTestURL;
	serviceTestURL.appendChild(document.createTextNode(testURL));
	
	Element serviceMsgProtocol = document.createElementNS(FSVP, "fsvp:serviceMessagingProtocol");
	String msgProtocol = (profile.msgProtocol == null) ? "" : profile.msgProtocol;
	serviceMsgProtocol.appendChild(document.createTextNode(msgProtocol));
	
	Element serviceInputs = document.createElementNS(FSVP, "fsvp:serviceInputFormats");
	for (int i=0; i<profile.inputMIMETypes.length; i++)
	{
		Element inMIME = document.createElementNS(FSVP, "fsvp:MIMEType");
		String MIMEtype = (profile.inputMIMETypes[i] == null) ? "" : profile.inputMIMETypes[i];
		inMIME.appendChild(document.createTextNode(MIMEtype));
		serviceInputs.appendChild(inMIME);
	}
	
	Element serviceOutputs = document.createElementNS(FSVP, "fsvp:serviceOutputFormats");
	for (int i=0; i<profile.outputMIMETypes.length; i++)
	{
		Element outMIME = document.createElementNS(FSVP, "fsvp:MIMEType");
		String MIMEtype = (profile.outputMIMETypes[i] == null) ? "" : profile.outputMIMETypes[i];
		outMIME.appendChild(document.createTextNode(MIMEtype));
		serviceOutputs.appendChild(outMIME);
	}

	Element dependencies = document.createElementNS(FSVP, "fsvp:serviceImplDependencies");
	for (int i=0; i<profile.software.length; i++)
	{
		Element software = document.createElementNS(FSVP, "fsvp:software");
		String swName = (profile.software[i].swName == null) ? "" : profile.software[i].swName;
		software.setAttribute("name", swName);
		String swVersion = (profile.software[i].swVersion == null) ? "" : profile.software[i].swVersion;
		software.setAttribute("version", swVersion);
		String swType = (profile.software[i].swType == null) ? "" : profile.software[i].swType;
		software.setAttribute("type", swType);
		String swLicenceType = (profile.software[i].swLicenceType == null) ? "" : profile.software[i].swLicenceType;
		software.setAttribute("license", swLicenceType);
		String isOpenSrc = Boolean.toString(profile.software[i].isOpenSource);
		software.setAttribute("opensource", isOpenSrc);		
		dependencies.appendChild(software);
	}	
	
	Element serviceImpl = document.createElementNS(FSVP, "fsvp:serviceImplementation");		
	serviceImpl.appendChild(serviceTestURL);	
	serviceImpl.appendChild(serviceMsgProtocol);
	serviceImpl.appendChild(serviceInputs);
	serviceImpl.appendChild(serviceOutputs);
	serviceImpl.appendChild(dependencies);
	
	root.appendChild(serviceLabel);
	root.appendChild(serviceImpl);
  }

  public Element getRootElement()
  {
    return document.getDocumentElement();
  }

  public Document getDocument()
  {
    return document;
  }

  public void printDSInputSpec()
  {
    try
    {
      String str = new XMLWriter(new DOMResult(document)).getXMLAsString();
      System.out.println(str);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}