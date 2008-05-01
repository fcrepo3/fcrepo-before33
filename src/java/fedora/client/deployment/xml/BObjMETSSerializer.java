/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.deployment.xml;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Date;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fedora.client.deployment.DeploymentBuilderException;
import fedora.client.deployment.data.BObjTemplate;
import fedora.client.deployment.data.Datastream;

import fedora.common.Constants;

import fedora.server.utilities.DateUtility;

/**
 * @author Sandy Payette
 */
public abstract class BObjMETSSerializer
        implements Constants {

    protected PrintWriter out;

    protected Document document;

    protected Element root;

    protected Element bObjFileSec;

    protected Vector<String> docDSIDs = new Vector<String>();

    protected BObjTemplate bObjData;

    protected String now;

    public BObjMETSSerializer(BObjTemplate bObjData)
            throws DeploymentBuilderException {
        this.bObjData = bObjData;
        now = DateUtility.convertDateToString(new Date());
    }

    /*
     * The ServiceDefinitionMETSSerializer and ServiceDeploymentMETSSerializer
     * will implement this to deal with the specific metadata formats required
     * in each.
     */
    protected abstract Element[] getInlineMD()
            throws DeploymentBuilderException;

    /*
     * The serviceDefinitionMETSSerializer and serviceDeploymentMETSSerializer
     * will implement this to deal with the VERSION attribute.
     */
    protected abstract Attr[] getVariableRootAttrs()
            throws DeploymentBuilderException;

    protected void serialize() throws DeploymentBuilderException {
        initializeTree();
        genBaseMETS(bObjData);
        finalizeTree();
    }

    protected void initializeTree() throws DeploymentBuilderException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.newDocument();
        } catch (ParserConfigurationException pce) {
            // Parser with specified options can't be built
            pce.printStackTrace();
            throw new DeploymentBuilderException("BObjMETSSerializer: "
                    + " Parser configuration exception initializing document builder.");
        }
        root = document.createElementNS(METS.uri, "METS:mets");
        bObjFileSec = document.createElementNS(METS.uri, "METS:fileSec");
    }

    protected void finalizeTree() throws DeploymentBuilderException {
        // put the tree together
        document.appendChild(root);
        //root.appendChild(header);
        Element[] mdSecs = getInlineMD();
        for (Element element : mdSecs) {
            root.appendChild(element);
        }
        root.appendChild(bObjFileSec);
    }

    protected void genBaseMETS(BObjTemplate bObjData)
            throws DeploymentBuilderException {
        // METS root element
        setMETSRoot(bObjData);
        //setMETSHeader();
        setBObjFileSec(bObjData);
    }

    protected void setMETSRoot(BObjTemplate bObjData)
            throws DeploymentBuilderException {
        // METS root element
        root.setAttributeNS(XMLNS.uri, "xmlns:METS", METS.uri);
        root.setAttributeNS(XMLNS.uri, "xmlns:xlink", XLINK.uri);
        root.setAttributeNS(XSI.uri, "xsi:schemaLocation", METS.uri + " "
                + METS_EXT1_1.xsdLocation);
        String pid = bObjData.getbObjPID() == null ? "" : bObjData.getbObjPID();
        if (!pid.equals("")) {
            root.setAttribute("OBJID", pid);
        }
        String label =
                bObjData.getbObjLabel() == null ? "" : bObjData.getbObjLabel();
        root.setAttribute("LABEL", label);
        Attr[] attrSet = getVariableRootAttrs();
        for (Attr element : attrSet) {
            root.setAttributeNodeNS(element);
        }
    }

    protected void setBObjFileSec(BObjTemplate bObjData) {
        // METS fileSec element for the service object
        Element datastreamsFileGrp =
                document.createElementNS(METS.uri, "METS:fileGrp");
        datastreamsFileGrp.setAttribute("ID", "DATASTREAMS");
        Datastream[] docs = bObjData.getDocDatastreams();
        for (int i = 0; i < docs.length; i++) {
            Element dsFileGrp =
                    document.createElementNS(METS.uri, "METS:fileGrp");
            String dsid = "DS" + (i + 1);
            dsFileGrp.setAttribute("ID", dsid);
            dsFileGrp.setAttribute("STATUS", "A");
            Element dsFile = document.createElementNS(METS.uri, "METS:file");
            dsFile.setAttribute("ID", (dsid + ".0"));
            dsFile.setAttribute("CREATED", now);
            dsFile.setAttribute("SEQ", "0");
            dsFile.setAttribute("MIMETYPE", docs[i].dsMIMEType);
            dsFile.setAttribute("OWNERID", docs[i].dsControlGrpType);
            dsFile.setAttribute("STATUS", "A");
            Element dsFileLoc =
                    document.createElementNS(METS.uri, "METS:FLocat");
            dsFileLoc.setAttribute("LOCTYPE", "URL");
            dsFileLoc.setAttributeNS(XLINK.uri, "xlink:href", docs[i].dsURL);
            dsFileLoc.setAttributeNS(XLINK.uri, "xlink:title", docs[i].dsLabel);
            dsFile.appendChild(dsFileLoc);
            dsFileGrp.appendChild(dsFile);
            datastreamsFileGrp.appendChild(dsFileGrp);
            docDSIDs.add(dsid);
        }
        bObjFileSec.appendChild(datastreamsFileGrp);
    }

    protected Element setDC(Element dc) throws DeploymentBuilderException {
        Element dcNode =
                document.createElementNS(METS.uri, "METS:dmdSecFedora");
        dcNode.setAttribute("ID", "DC");
        Element descMD = document.createElementNS(METS.uri, "METS:descMD");
        descMD.setAttribute("ID", "DC1.0");
        descMD.setAttribute("CREATED", now);
        descMD.setAttribute("STATUS", "A");
        Element mdWrap = document.createElementNS(METS.uri, "METS:mdWrap");
        mdWrap.setAttribute("MIMETYPE", "text/xml");
        mdWrap.setAttribute("MDTYPE", "OTHER");
        mdWrap.setAttribute("LABEL", "Dublin Core Metadata for Service");
        Element xmlData = document.createElementNS(METS.uri, "METS:xmlData");
        Node importDC = document.importNode(dc, true);
        xmlData.appendChild(importDC);
        mdWrap.appendChild(xmlData);
        descMD.appendChild(mdWrap);
        dcNode.appendChild(descMD);
        return dcNode;
    }

    public void printMETS() {
        try {
            String str =
                    new XMLWriter(new DOMResult(document)).getXMLAsString();
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeMETSFile(File file) throws DeploymentBuilderException {
        try {
            XMLWriter w = new XMLWriter(document);
            w.writeXMLToFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeploymentBuilderException("BObjMETSSerializer: "
                    + " IO or parser exception writing METS file."
                    + " Underlying exception was: " + e.getMessage());
        }
    }

    public InputStream writeMETSStream() throws DeploymentBuilderException {
        InputStream in = null;
        try {
            XMLWriter w = new XMLWriter(document);
            in = w.writeXMLToStream();
        } catch (Exception e) {
            e.printStackTrace();
            throw new DeploymentBuilderException("BObjMETSSerializer: "
                    + " IO or parser exception writing METS to stream."
                    + " Underlying exception was: " + e.getMessage());
        }
        return in;
    }
}