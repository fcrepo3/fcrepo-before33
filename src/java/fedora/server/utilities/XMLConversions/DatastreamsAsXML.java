package fedora.server.utilities.XMLConversions;

import java.io.IOException;
import java.util.Date;

import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.utilities.DateUtility;

/**
 *
 * <p><b>Title:</b> DatastreamAsXML.java</p>
 * <p><b>Description:</b> Use an object reader to get datastream information
 * out of the object and encode the in XML.</p>
 *
 * <p>XML Schemas used for output:
 * http://www.fedora.info/definitions/1/0/access/objectItemIndex.xsd.
 * (To be created at this URL asap!) </p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class DatastreamsAsXML
{

    public DatastreamsAsXML()
    {
    }

    // FIXIT!! Make the output of this valid XML with namespaces for fedora
    public String getItemIndex(String reposBaseURL, DOReader reader, Date versDateTime)
            throws ServerException {
        Datastream[] datastreams = reader.GetDatastreams(versDateTime);
        StringBuffer out = new StringBuffer();

        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.append("<objectItemIndex"
              + " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
              + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
              + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/"
              + " http://www.fedora.info/definitions/1/0/access/objectItemList.xsd\""
              + " PID=\"" + reader.GetObjectPID() + "\">\n");

        for (int i=0; i<datastreams.length; i++)
        {
          out.append("<item>\n");
          out.append("<itemId>" + datastreams[i].DatastreamID + "</itemId>\n");
          String label = datastreams[i].DSLabel;
          if (label==null) label="";
          out.append("<itemLabel>" + label + "</itemLabel>\n");
          String itemDissURL = getItemDissURL(reposBaseURL, reader.GetObjectPID(),
              datastreams[i].DatastreamID, versDateTime);
          out.append("<itemURL>" + itemDissURL + "</itemURL>\n");
          out.append("<itemMIMEType>" + datastreams[i].DSMIME + "</itemMIMEType>\n");
          out.append("</item>\n");
        }
        out.append("</objectItemIndex>");
        return out.toString();
    }

    private String getItemDissURL(String reposBaseURL, String PID,
        String datastreamID, Date versDateTime)
    {
        String itemDissURL = null;

        if (versDateTime == null)
        {
          itemDissURL = reposBaseURL + "/fedora/get/"
            + PID + "/fedora-system:3/getItem?itemID=" + datastreamID;
        }
        else
        {
            itemDissURL = reposBaseURL + "/fedora/get/"
              + PID + "/fedora-system:3/getItem/"
              + DateUtility.convertDateToString(versDateTime)
              + "/?itemID=" + datastreamID;
        }
        return itemDissURL;
    }

    public String getDatastreamList(DOReader reader, Date versDateTime)
            throws ServerException {
        Datastream[] datastreams = reader.GetDatastreams(versDateTime);
        StringBuffer out = new StringBuffer();
        out.append("<datastreamSet>\n");
        out.append("<pid>" + reader.GetObjectPID() + "</pid>\n");
        for (int i=0; i<datastreams.length; i++)
        {
          out.append("<datastream>\n");
          out.append("<dsId>" + datastreams[i].DatastreamID + "</dsId>\n");
          out.append("<dsVersionId>" + datastreams[i].DSVersionID + "</dsVersionId>\n");
          String label = datastreams[i].DSLabel;
          if (label==null) label="";
          out.append("<dsCreateDT>" + datastreams[i].DSCreateDT + "</dsCreateDT>\n");
          out.append("<dsLabel>" + label + "</dsLabel>\n");
          out.append("<dsLocationURL>" + datastreams[i].DSLocation + "</dsLocationURL>\n");
          out.append("<dsMIMEType>" + datastreams[i].DSMIME + "<dsMIMEType>\n");
          out.append("<dsControlGrp>" + datastreams[i].DSControlGrp + "<dsControl>\n");
          out.append("</datastream>\n");
        }
        out.append("</datastreamSet>");
        return out.toString();
    }
}