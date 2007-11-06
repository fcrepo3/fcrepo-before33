/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.bmech.xml;

import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fedora.client.bmech.data.*;
import fedora.client.bmech.BMechBuilderException;

import fedora.common.Constants;

/**
 * @author payette@cs.cornell.edu
 */
public class ServiceProfileSerializer
        implements Constants {

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

    Element root = (Element)document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceProfile");
    root.setAttributeNS(XMLNS.uri, "xmlns:fsvp", SERVICE_PROFILE.uri);
    String bDefPID = (newBMech.getbDefContractPID() == null) ? "" : newBMech.getbDefContractPID();
    root.setAttribute("bDefPID", bDefPID);
	String name = (profile.serviceName == null) ? "" : profile.serviceName;
	root.setAttribute("name", name);
    document.appendChild(root);
	
	Element serviceLabel = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceDescription");
	String label = (profile.serviceLabel == null) ? "" : profile.serviceLabel;
	serviceLabel.appendChild(document.createTextNode(label));
	
	Element serviceTestURL = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceLiveTestURL");
	String testURL = (profile.serviceTestURL == null) ? "" : profile.serviceTestURL;
	serviceTestURL.appendChild(document.createTextNode(testURL));
	
	Element serviceMsgProtocol = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceMessagingProtocol");
	String msgProtocol = (profile.msgProtocol == null) ? "" : profile.msgProtocol;
	serviceMsgProtocol.appendChild(document.createTextNode(msgProtocol));
	
	Element serviceInputs = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceInputFormats");
	for (int i=0; i<profile.inputMIMETypes.length; i++)
	{
		Element inMIME = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:MIMEType");
		String MIMEtype = (profile.inputMIMETypes[i] == null) ? "" : profile.inputMIMETypes[i];
		inMIME.appendChild(document.createTextNode(MIMEtype));
		serviceInputs.appendChild(inMIME);
	}
	
	Element serviceOutputs = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceOutputFormats");
	for (int i=0; i<profile.outputMIMETypes.length; i++)
	{
		Element outMIME = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:MIMEType");
		String MIMEtype = (profile.outputMIMETypes[i] == null) ? "" : profile.outputMIMETypes[i];
		outMIME.appendChild(document.createTextNode(MIMEtype));
		serviceOutputs.appendChild(outMIME);
	}

	Element dependencies = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceImplDependencies");
	for (int i=0; i<profile.software.length; i++)
	{
		Element software = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:software");
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
	
	Element serviceImpl = document.createElementNS(SERVICE_PROFILE.uri, "fsvp:serviceImplementation");		
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