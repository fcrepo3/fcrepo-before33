package fedora.server.storage;

import fedora.server.storage.types.*;
import fedora.server.errors.*;
import java.util.Date;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.Set;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

public class DSBindSpecDeserializer 
        extends DefaultHandler {
        
    // FIXIT! How can we deal with prefix variability?  fbs.DSBinding vs. something.DSBinding?
    // I need to do something better here to deal with namespaces,
    // so the event handler is not fragile.
    
    protected BMechDSBindSpec dsBindSpec;

    private boolean inDSBindSpec = false;
    private boolean inBMechID = false;
    private boolean inBDefID = false;
    private boolean inDSBind = false;
    private boolean inDSBindLabel = false;
    private boolean inDSBindInstructions = false;
    private boolean inDSBindMIME = false;

    private BMechDSBindRule h_dsBindRule;
    private Vector h_vBindRules;
    private BMechDSBindSpec h_dsBindSpec;

    // constructor does the parsing
    public DSBindSpecDeserializer(InputStream in, String pid) 
            throws RepositoryConfigurationException, ObjectIntegrityException {
        XMLReader xmlReader=null;
        try {
            SAXParserFactory saxfactory=SAXParserFactory.newInstance();
            saxfactory.setValidating(false);
            SAXParser parser=saxfactory.newSAXParser();
            xmlReader=parser.getXMLReader();
            xmlReader.setContentHandler(this);
            xmlReader.setFeature("http://xml.org/sax/features/namespaces", false);
            xmlReader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
        } catch (Exception e) {
            throw new RepositoryConfigurationException("Internal SAX error while "
                    + "preparing for DSBIND datastream deserialization: "
                    + e.getMessage());
        }
        try {
            xmlReader.parse(new InputSource(in));
        } catch (Exception e) {
            throw new ObjectIntegrityException(
                    "Error parsing DSBIND datastream in '" + pid
                    + "': " + e.getClass().getName() + ": " + e.getMessage());
        }     
    }

    public BMechDSBindSpec getDSBindSpec() {
        return dsBindSpec;
    }

    public void startDocument() throws SAXException
    {
      //initialize the event handler variables

      h_vBindRules = new Vector();
      h_dsBindSpec = new BMechDSBindSpec();
    }

    public void endDocument() throws SAXException
    {
        h_dsBindSpec.dsBindRules = (BMechDSBindRule[]) h_vBindRules.toArray(new BMechDSBindRule[0]);
        h_vBindRules = null;
        dsBindSpec = h_dsBindSpec;
    }

    public void skippedEntity(String name) throws SAXException
    {
      StringBuffer sb = new StringBuffer();
      sb.append('&');
      sb.append(name);
      sb.append(';');
      char[] text = new char[sb.length()];
      sb.getChars(0, sb.length(), text, 0);
      this.characters(text, 0, text.length);
    }


    public void characters(char ch[], int start, int length)  throws SAXException
    {

      if (inBMechID)
      {
        h_dsBindSpec.bMechPID = new String(ch, start, length);
      }
      else if (inBDefID)
      {
        h_dsBindSpec.bDefPID = new String(ch, start, length);
      }
      else if (inDSBindLabel)
      {
        h_dsBindRule.bindingLabel = new String(ch, start, length);
      }
      else if (inDSBindInstructions)
      {
        h_dsBindRule.bindingInstruction = new String(ch, start, length);
      }
      else if (inDSBindMIME)
      {
        StringTokenizer st = new StringTokenizer(new String(ch, start, length), " ");
        String[] MIMETypes = new String[st.countTokens()];
        for (int i = 0; i < st.countTokens(); i++)
        {
          MIMETypes[i] = st.nextToken();
        }
        h_dsBindRule.bindingMIMETypes = MIMETypes;
      }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs)
      throws SAXException
    {
      if (qName.equalsIgnoreCase("fbs:DSBindingSpec"))
      {
        inDSBindSpec = true;
      }
      else if (qName.equalsIgnoreCase("fbs:mechanismID"))
      {
        inBMechID = true;
      }
      else if (qName.equalsIgnoreCase("fbs:behaviorDefID"))
      {
        inBDefID = true;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBinding"))
      {
        inDSBind = true;
        h_dsBindRule = new BMechDSBindRule();
        h_dsBindRule.bindingKeyName = attrs.getValue("BindingKeyName");
        h_dsBindRule.maxNumBindings = new Integer(attrs.getValue("DSMax")).intValue();
        h_dsBindRule.minNumBindings = new Integer(attrs.getValue("DSMin")).intValue();
        h_dsBindRule.ordinality = Boolean.getBoolean(attrs.getValue("DSOrdinality"));
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingLabel"))
      {
        inDSBindLabel = true;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingInstructions"))
      {
        inDSBindInstructions = true;
      }
      else if (qName.equalsIgnoreCase("fbs:DSMIME"))
      {
        inDSBindMIME = true;
      }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException
    {

      if (qName.equalsIgnoreCase("fbs:DSBindingSpec") && inDSBindSpec)
      {
        inDSBindSpec = false;
      }
      else if (qName.equalsIgnoreCase("fbs:mechanismID") && inBMechID)
      {
        inBMechID = false;
      }
      else if (qName.equalsIgnoreCase("fbs:behaviorDefID") && inBDefID)
      {
        inBDefID = false;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBinding") && inDSBind)
      {
        inDSBind = false;
        h_vBindRules.add(h_dsBindRule);
        h_dsBindRule = null;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingLabel") && inDSBindLabel)
      {
        inDSBindLabel = false;
      }
      else if (qName.equalsIgnoreCase("fbs:DSBindingInstructions") && inDSBindInstructions)
      {
        inDSBindInstructions = false;
      }
      else if (qName.equalsIgnoreCase("fbs:DSMIME") && inDSBindMIME)
      {
        inDSBindMIME = false;
      }
    }
    
}
