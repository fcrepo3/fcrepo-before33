package fedora.client.bmech.xml;

import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import javax.xml.transform.dom.DOMResult;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import fedora.client.bmech.data.*;

/**
 *
 * <p><b>Title:</b> MethodMapGenerator.java</p>
 * <p><b>Description:</b> </p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version 1.0
 */
public class MethodMapGenerator
{
  private static final String FMM =
    "http://fedora.comm.nsdlib.org/service/methodmap";

  private Document document;

  public MethodMapGenerator(BMechTemplate newBMech)
  {
    createDOM();
    genMethodMap(newBMech);
  }

  private void createDOM()
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
    }
  }

  private void genMethodMap(BMechTemplate newBMech)
  {
    Method[] methods = newBMech.getBMechMethods();

    Element root = (Element)document.createElementNS(FMM, "fmm:MethodMap");
    String bmlabel = (newBMech.getbMechLabel() == null) ? "" : newBMech.getbMechLabel();
    root.setAttribute("name", ("MethodMap - " + bmlabel));
    String bDefPID = (newBMech.getbDefPID() == null) ? "" : newBMech.getbDefPID();
    root.setAttribute("bDefPID", bDefPID);
    document.appendChild(root);

    for (int i=0; i<methods.length; i++)
    {
      Element method = document.createElementNS(FMM, "fmm:Method");
      String mname = (methods[i].methodName == null) ? "" : methods[i].methodName;
      String mlabel = (methods[i].methodLabel == null) ? "" : methods[i].methodLabel;
      method.setAttribute("operationName", mname.trim());
      method.setAttribute("operationLabel", mlabel.trim());
      method.setAttribute("wsdlMsgName", (mname.trim() + "Request"));
      method.setAttribute("wsdlMsgOutput", "dissemResponse");
      root.appendChild(method);

      // Append Method Parm elements
      MethodParm[] parms = methods[i].methodProperties.methodParms;
      for (int j=0; j<parms.length; j++)
      {
        Element parm = null;
        if (parms[j].parmType.equalsIgnoreCase(MethodParm.DATASTREAM_INPUT))
        {
          parm = document.createElementNS(FMM, "fmm:DatastreamInputParm");
        }
        else if (parms[j].parmType.equalsIgnoreCase(MethodParm.USER_INPUT))
        {
          parm = document.createElementNS(FMM, "fmm:UserInputParm");
        }
        else if (parms[j].parmType.equalsIgnoreCase(MethodParm.DEFAULT_INPUT))
        {
          parm = document.createElementNS(FMM, "fmm:DefaultInputParm");
        }
        else
        {
          //FIXIT!  throw error on invalid parm type.
        }

        String name = (parms[j].parmName == null) ? "" : parms[j].parmName;
        parm.setAttribute("parmName", name);
        String passby = (parms[j].parmPassBy == null) ? "" : parms[j].parmPassBy;
        parm.setAttribute("passBy", passby);
        String req = (parms[j].parmRequired == null) ? "" : parms[j].parmRequired;
        parm.setAttribute("required", req);
        String def = (parms[j].parmDefaultValue == null) ? "" : parms[j].parmDefaultValue;
        parm.setAttribute("defaultValue", def);
        String label = (parms[j].parmLabel == null) ? "" : parms[j].parmLabel;
        parm.setAttribute("label", label);
        method.appendChild(parm);
      }
      // Append Method Return Type element
      String[] mimeTypes = methods[i].methodProperties.returnMIMETypes;
      StringBuffer sb = new StringBuffer();
      for (int k=0; k<mimeTypes.length; k++)
      {
        sb.append(mimeTypes[k].toString() + " ");
      }
      Element methodReturn = document.createElementNS(FMM, "fmm:MethodReturnType");
      methodReturn.setAttribute("wsdlMsgName", "dissemResponse");
      methodReturn.setAttribute("wsdlMsgTOMIME", sb.toString().trim());
      method.appendChild(methodReturn);
    }
  }

  public Element getRootElement()
  {
    return document.getDocumentElement();
  }

  public Document getDocument()
  {
    return document;
  }

  public void printMethodMap()
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