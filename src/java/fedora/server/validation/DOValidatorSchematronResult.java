package fedora.server.validation;

/**
 * <p>Title: DOValidatorSchematronResult.java</p>
 * <p>Description: Schematron validation with FedoraRules schema as default.</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Sandy Payette, payette@cs.cornell.edu
 * @version 1.0
 */

import fedora.server.errors.ObjectValidityException;
import fedora.server.errors.ServerException;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Properties;

// DOM classes
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

// TrAX classes
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.*;

import javax.xml.parsers.ParserConfigurationException;

public class DOValidatorSchematronResult
{

  /**
   * Configuration variables for Schematron Validation
   * (These will ultimately be available via Server configuration)
   *
   */

  private StringBuffer string = new StringBuffer();
  private Element rootElement;

  public DOValidatorSchematronResult(DOMResult result)
  {
    rootElement = (Element)result.getNode().getFirstChild();
  }

    public String getXMLResult()
    throws TransformerException,
           TransformerConfigurationException,
           ParserConfigurationException
  {
    Writer w = new StringWriter();
    PrintWriter out = new PrintWriter(w);

    TransformerFactory tfactory = TransformerFactory.newInstance();
    Transformer transformer = tfactory.newTransformer();
    Properties transProps = new Properties();
    transProps.put("method", "xml");
    transProps.put("indent", "yes");
    transformer.setOutputProperties(transProps);
    transformer.transform(new DOMSource(rootElement), new StreamResult(out));
    out.close();
    return w.toString();
  }


  /**
   * Check if the object passes Schematron validation
   *
   * @return <code>true</code>, object is valid,
   *         <code>false</code> object had errors.
   */
  public boolean isValid()
  {
    if ((rootElement.getElementsByTagName("ASSERT").getLength() == 0) &&
        (rootElement.getElementsByTagName("REPORT").getLength() == 0) )
      return true;
    else
      return false;
  }

  /** Serializes the specified node, recursively, to a Writer
   *  and returns it as a String too.
   */
  public String serializeResult(Writer out)
  {
    return(serializeNode(rootElement, out));
  }

  private String serializeNode(Node node, Writer out)
  {
    try
    {
      if ( node == null )
      {
         return null;
      }

      int type = node.getNodeType();
      switch ( type )
      {
      case Node.DOCUMENT_NODE:
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        string.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n");
        serializeNode(((Document)node).getDocumentElement(), out);
        break;

      case Node.ELEMENT_NODE:
        string.append("<");
        string.append(node.getNodeName());

        out.write("<");
        out.write(node.getNodeName());

        // do attributes
        NamedNodeMap attrs = node.getAttributes();
        for ( int i = 0; i < attrs.getLength(); i++ )
        {
          string.append(" ");
          string.append(attrs.item(i).getNodeName());
          string.append("=\"");
          string.append(attrs.item(i).getNodeValue());
          string.append("\"");

          out.write(" ");
          out.write(attrs.item(i).getNodeName());
          out.write("=\"");
          out.write(attrs.item(i).getNodeValue());
          out.write("\"");
        }

        // close up the current element
        string.append(">");
        out.write(">");

        // recursive call to process this node's children
        NodeList children = node.getChildNodes();
        if ( children != null )
        {
          int len = children.getLength();
          for ( int i = 0; i < len; i++ )
          {
            serializeNode(children.item(i), out);
          }
        }
        break;

      case Node.TEXT_NODE:
        string.append(node.getNodeValue());
        out.write(node.getNodeValue());
        break;
      }

      if ( type == Node.ELEMENT_NODE )
      {
        string.append("</");
        string.append(node.getNodeName());
        string.append(">");
        out.write("</");
        out.write(node.getNodeName());
        out.write(">");
      }
      out.flush();
    }
    catch ( Exception e )
    {
      System.err.println( e.toString() );
    }
    return(string.toString());
  }

}