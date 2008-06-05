/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also
 * available online at http://www.fedora.info/license/).
 */
package fedora.client.utility.ingest;

import java.rmi.RemoteException;

import fedora.client.Administrator;

import fedora.common.Constants;

import fedora.server.management.FedoraAPIM;
import fedora.server.utilities.StreamUtility;

/**
 * Creates basic object xml for ingest.
 *
 * @author Bill Branan
 */
public class XMLBuilder {

    private FedoraAPIM apim = null;

    public static enum OBJECT_TYPE {
        dataObject, contentModel, serviceDefinition, serviceDeployment
    };

    public XMLBuilder(FedoraAPIM fedoraAPIM) {
        apim = fedoraAPIM;
    }

    public String createObjectXML(OBJECT_TYPE objectType, String pid, String label) throws RemoteException {
        StringBuffer xml = new StringBuffer();
        pid = encodePid(pid);

        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<foxml:digitalObject xmlns:xsi=\"" + Constants.XSI.uri + "\"\n");
        xml.append("       xmlns:foxml=\"" + Constants.FOXML.uri + "\"\n");
        xml.append("       xsi:schemaLocation=\"" + Constants.FOXML.uri  + " " + Constants.FOXML1_1.xsdLocation + "\"");
        xml.append("       VERSION=\"1.1\" PID=\"" + pid + "\">\n");
        xml.append("  <foxml:objectProperties>\n");
        xml.append("    <foxml:property NAME=\"" + Constants.MODEL.LABEL.uri + "\" VALUE=\"" + StreamUtility.enc(label) + "\"/>\n");
        xml.append("    <foxml:property NAME=\"" + Constants.MODEL.OWNER.uri + "\" VALUE=\"" + Administrator.getUser() + "\"/>");
        xml.append("  </foxml:objectProperties>\n");

        if(OBJECT_TYPE.contentModel.equals(objectType)) {
            xml.append("  <foxml:datastream ID=\"DS-COMPOSITE-MODEL\" STATE=\"A\" CONTROL_GROUP=\"X\" VERSIONABLE=\"true\">\n");
            xml.append("    <foxml:datastreamVersion ID=\"DS-COMPOSITE-MODEL1.0\" LABEL=\"DS Composite Model\" MIMETYPE=\"text/xml\">\n");
            xml.append("      <foxml:xmlContent>\n");
            xml.append("        <dsCompositeModel xmlns=\"info:fedora/fedora-system:def/dsCompositeModel#\">\n");
            xml.append("          <dsTypeModel ID=\"DC\">\n");
            xml.append("            <form MIME=\"text/xml\"/>\n");
            xml.append("          </dsTypeModel>\n");
            xml.append("        </dsCompositeModel>\n");
            xml.append("      </foxml:xmlContent>\n");
            xml.append("    </foxml:datastreamVersion>\n");
            xml.append("  </foxml:datastream>\n");
            xml.append("  <foxml:datastream CONTROL_GROUP=\"X\" ID=\"RELS-EXT\" STATE=\"A\" VERSIONABLE=\"false\">\n");
            xml.append("    <foxml:datastreamVersion ID=\"RELS-EXT1.0\" LABEL=\"Relationships\" MIMETYPE=\"application/rdf+xml\">\n");
            xml.append("      <foxml:xmlContent>\n");
            xml.append("        <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:fedora-model=\"info:fedora/fedora-system:def/model#\">\n");
            xml.append("          <rdf:Description rdf:about=\"info:fedora/" + pid + "\">\n");
            xml.append("            <fedora-model:hasModel rdf:resource=\"info:fedora/fedora-system:ContentModel-3.0\" />\n");
            xml.append("          </rdf:Description>\n");
            xml.append("        </rdf:RDF>\n");
            xml.append("      </foxml:xmlContent>\n");
            xml.append("    </foxml:datastreamVersion>\n");
            xml.append("  </foxml:datastream>\n");
        }
        else if(OBJECT_TYPE.serviceDefinition.equals(objectType)) {
            xml.append("  <foxml:datastream ID=\"METHODMAP\" CONTROL_GROUP=\"X\" STATE=\"A\">");
            xml.append("    <foxml:datastreamVersion ID=\"METHODMAP1.0\" MIMETYPE=\"text/xml\" LABEL=\"Method Map\">");
            xml.append("      <foxml:xmlContent>");
            xml.append("        <fmm:MethodMap name=\"Fedora MethodMap for SDef\" xmlns:fmm=\"http://fedora.comm.nsdlib.org/service/methodmap\">");
            xml.append("          <fmm:Method operationName=\"changeme\"/>");
            xml.append("        </fmm:MethodMap>");
            xml.append("      </foxml:xmlContent>");
            xml.append("    </foxml:datastreamVersion>");
            xml.append("  </foxml:datastream>");
            xml.append("  <foxml:datastream CONTROL_GROUP=\"X\" ID=\"RELS-EXT\" STATE=\"A\" VERSIONABLE=\"false\">");
            xml.append("    <foxml:datastreamVersion ID=\"RELS-EXT1.0\" LABEL=\"Relationships\" MIMETYPE=\"application/rdf+xml\">");
            xml.append("      <foxml:xmlContent>");
            xml.append("        <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:fedora-model=\"info:fedora/fedora-system:def/model#\">");
            xml.append("          <rdf:Description rdf:about=\"info:fedora/" + pid + "\">");
            xml.append("            <fedora-model:hasModel rdf:resource=\"info:fedora/fedora-system:ServiceDefinition-3.0\"/>");
            xml.append("          </rdf:Description>");
            xml.append("        </rdf:RDF>");
            xml.append("      </foxml:xmlContent>");
            xml.append("    </foxml:datastreamVersion>");
            xml.append("  </foxml:datastream>");
        }
        else if(OBJECT_TYPE.serviceDeployment.equals(objectType)) {
            xml.append("  <foxml:datastream ID=\"WSDL\" CONTROL_GROUP=\"X\" STATE=\"A\">");
            xml.append("      <foxml:datastreamVersion ID=\"WSDL1.0\" MIMETYPE=\"text/xml\" LABEL=\"WSDL definition\">");
            xml.append("          <foxml:xmlContent>");
            xml.append("              <changeme>Update this section to include a wsdl:definitions tag which specifies the services provided by this Service Definition via WSDL</changeme>");
            xml.append("          </foxml:xmlContent>");
            xml.append("      </foxml:datastreamVersion>");
            xml.append("  </foxml:datastream>");
            xml.append("  <foxml:datastream ID=\"DSINPUTSPEC\" CONTROL_GROUP=\"X\" STATE=\"A\">");
            xml.append("      <foxml:datastreamVersion ID=\"DSINPUTSPEC1.0\" MIMETYPE=\"text/xml\" LABEL=\"Datastream Input Specification\">");
            xml.append("          <foxml:xmlContent>");
            xml.append("              <changeme>Update this section to include a fbs:DSInputSpec tag which specifies the datastreams to be used by WSDL-defined methods</changeme>");
            xml.append("          </foxml:xmlContent>");
            xml.append("      </foxml:datastreamVersion>");
            xml.append("  </foxml:datastream>");
            xml.append("  <foxml:datastream ID=\"METHODMAP\" CONTROL_GROUP=\"X\" STATE=\"A\">");
            xml.append("      <foxml:datastreamVersion ID=\"METHODMAP1.0\" MIMETYPE=\"text/xml\" LABEL=\"Mapping of WSDL to Fedora notion of Method Definitions\">");
            xml.append("          <foxml:xmlContent>");
            xml.append("              <changeme>Update this section to include a fmm:MethodMap tag which specifies the mapping of the WSDL to Fedora object methods</changeme>");
            xml.append("          </foxml:xmlContent>");
            xml.append("      </foxml:datastreamVersion>");
            xml.append("  </foxml:datastream>");
            xml.append("  <foxml:datastream CONTROL_GROUP=\"X\" ID=\"RELS-EXT\">");
            xml.append("      <foxml:datastreamVersion ID=\"RELS-EXT1.0\" MIMETYPE=\"application/rdf+xml\" LABEL=\"Relationships to other objects\">");
            xml.append("      <foxml:xmlContent>");
            xml.append("          <rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:fedora-model=\"info:fedora/fedora-system:def/model#\">");
            xml.append("              <rdf:Description rdf:about=\"info:fedora/" + pid + "\">");
            xml.append("                  <fedora-model:hasModel rdf:resource=\"info:fedora/fedora-system:ServiceDeployment-3.0\"/>");
            xml.append("                  <fedora-model:isDeploymentOf rdf:resource=\"info:fedora/changeme-to-sDefPid\"/>");
            xml.append("                  <fedora-model:isContractorOf rdf:resource=\"info:fedora/changeme-to-cModelPid\"/>");
            xml.append("              </rdf:Description>");
            xml.append("          </rdf:RDF>");
            xml.append("      </foxml:xmlContent>");
            xml.append("      </foxml:datastreamVersion>");
            xml.append("  </foxml:datastream>");
        }

        xml.append("</foxml:digitalObject>");

        return xml.toString();
    }

    private String encodePid(String pid) throws RemoteException {
        if(pid == null || pid.equals("")) {
            pid = apim.getNextPID(null, null)[0];
        }
        return StreamUtility.enc(pid);
    }
}