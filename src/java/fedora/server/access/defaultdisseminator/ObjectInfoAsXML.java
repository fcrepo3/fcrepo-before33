/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.access.defaultdisseminator;

import java.io.InputStream;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import fedora.common.Constants;

import fedora.server.access.ObjectProfile;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.utilities.DCFields;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.StreamUtility;

/**
 * Provide an XML encoding of various object components.
 * 
 * @author Sandy Payette
 */
public class ObjectInfoAsXML
        implements Constants {

    public ObjectInfoAsXML() {
    }

    public String getObjectProfile(String reposBaseURL,
                                   ObjectProfile objProfile,
                                   Date versDateTime) throws ServerException {
        StringBuffer out = new StringBuffer();
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.append("<objectProfile");
        out.append(" pid=\"" + objProfile.PID + "\"");
        if (versDateTime != null) {
            out.append(" dateTime=\"");
            out.append(DateUtility.convertDateToString(versDateTime));
            out.append("\"");
        }
        out.append(" xmlns:xsi=\"" + XSI.uri + "\"");
        out.append(" xsi:schemaLocation=\"" + ACCESS.uri + " ");
        out.append(OBJ_PROFILE1_0.xsdLocation + "\">");

        // PROFILE FIELDS SERIALIZATION
        out.append("<objLabel>" + StreamUtility.enc(objProfile.objectLabel)
                + "</objLabel>");

        String cDate =
                DateUtility.convertDateToString(objProfile.objectCreateDate);
        out.append("<objCreateDate>" + cDate + "</objCreateDate>");
        String mDate =
                DateUtility.convertDateToString(objProfile.objectLastModDate);
        out.append("<objLastModDate>" + mDate + "</objLastModDate>");
        out.append("<objDissIndexViewURL>"
                + StreamUtility.enc(objProfile.dissIndexViewURL)
                + "</objDissIndexViewURL>");
        out.append("<objItemIndexViewURL>"
                + StreamUtility.enc(objProfile.itemIndexViewURL)
                + "</objItemIndexViewURL>");
        out.append("</objectProfile>");
        return out.toString();
    }

    public String getItemIndex(String reposBaseURL,
                               DOReader reader,
                               Date versDateTime) throws ServerException {
        try {
            Datastream[] datastreams =
                    reader.GetDatastreams(versDateTime, null);
            StringBuffer out = new StringBuffer();

            out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            out.append("<objectItemIndex");
            out.append(" PID=\"" + reader.GetObjectPID() + "\"");
            if (versDateTime != null) {
                out.append(" dateTime=\"");
                out.append(DateUtility.convertDateToString(versDateTime));
                out.append("\"");
            }
            out.append(" xmlns:xsi=\"" + XSI.uri + "\"");
            out.append(" xsi:schemaLocation=\"" + ACCESS.uri + " ");
            out.append(OBJ_ITEMS1_0.xsdLocation + "\">");

            for (Datastream element : datastreams) {
                out.append("<item>\n");
                out.append("<itemId>" + StreamUtility.enc(element.DatastreamID)
                        + "</itemId>\n");
                String label = element.DSLabel;
                if (label == null) {
                    label = "";
                }
                out.append("<itemLabel>" + StreamUtility.enc(label)
                        + "</itemLabel>\n");
                String itemDissURL =
                        getItemDissURL(reposBaseURL,
                                       reader.GetObjectPID(),
                                       element.DatastreamID,
                                       versDateTime);
                out.append("<itemURL>" + StreamUtility.enc(itemDissURL)
                        + "</itemURL>\n");
                out.append("<itemMIMEType>" + StreamUtility.enc(element.DSMIME)
                        + "</itemMIMEType>\n");
                out.append("</item>\n");
            }
            out.append("</objectItemIndex>");
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ObjectIntegrityException(e.getMessage());
        }
    }

    public String getMethodIndex(String reposBaseURL,
                                 String PID,
                                 ObjectMethodsDef[] methods,
                                 Date versDateTime) throws ServerException {
        StringBuffer out = new StringBuffer();
        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.append("<objectMethods");
        out.append(" pid=\"" + PID + "\"");
        if (versDateTime != null) {
            out.append(" dateTime=\"");
            out.append(DateUtility.convertDateToString(versDateTime));
            out.append("\"");
        }
        out.append(" xmlns:xsi=\"" + XSI.uri + "\"");
        out.append(" xsi:schemaLocation=\"" + ACCESS.uri + " ");
        out.append(OBJ_METHODS1_0.xsdLocation + "\">");

        String nextSdef = "null";
        String currentSdef = "";
        for (int i = 0; i < methods.length; i++) {
            currentSdef = methods[i].sDefPID;
            if (!currentSdef.equalsIgnoreCase(nextSdef)) {
                if (i != 0) {
                    out.append("</sdef>");
                }
                out.append("<sdef pid=\""
                        + StreamUtility.enc(methods[i].sDefPID) + "\" >");
            }
            String versDate =
                    DateUtility.convertDateToString(methods[i].asOfDate);
            out.append("<method name=\""
                    + StreamUtility.enc(methods[i].methodName)
                    + "\" asOfDateTime=\"" + versDate + "\" >");
            MethodParmDef[] methodParms = methods[i].methodParmDefs;
            for (MethodParmDef element : methodParms) {
                out.append("<parm parmName=\""
                        + StreamUtility.enc(element.parmName)
                        + "\" parmDefaultValue=\""
                        + StreamUtility.enc(element.parmDefaultValue)
                        + "\" parmRequired=\"" + element.parmRequired
                        + "\" parmType=\""
                        + StreamUtility.enc(element.parmType)
                        + "\" parmLabel=\""
                        + StreamUtility.enc(element.parmLabel) + "\" >");
                if (element.parmDomainValues.length > 0) {
                    out.append("<parmDomainValues>");
                    for (String element2 : element.parmDomainValues) {
                        out.append("<value>" + StreamUtility.enc(element2)
                                + "</value>");
                    }
                    out.append("</parmDomainValues>");
                }
                out.append("</parm>");
            }

            out.append("</method>");
            nextSdef = currentSdef;
        }
        out.append("</sdef>");
        out.append("</objectMethods>");
        return out.toString();
    }

    public String getOAIDublinCore(DatastreamXMLMetadata dublinCore)
            throws ServerException {
        StringBuffer out = new StringBuffer();
        out.append("<oai_dc:dc xmlns:oai_dc=\"" + OAI_DC.uri + "\""
                + " xmlns:dc=\"" + DC.uri + "\"" + " xmlns:xsi=\"" + XSI.uri
                + "\"" + " xsi:schemaLocation=\"" + OAI_DC.uri + " "
                + OAI_DC2_0.xsdLocation + "\">\n");

        if (dublinCore != null) {
            InputStream in = dublinCore.getContentStream();
            DCFields dc = new DCFields(in);
            for (int i = 0; i < dc.titles().size(); i++) {
                out.append("<dc:title>");
                out.append(StreamUtility.enc((String) dc.titles().get(i)));
                out.append("</dc:title>\n");
            }
            for (int i = 0; i < dc.creators().size(); i++) {
                out.append("<dc:creator>");
                out.append(StreamUtility.enc((String) dc.creators().get(i)));
                out.append("</dc:creator>\n");
            }
            for (int i = 0; i < dc.subjects().size(); i++) {
                out.append("<dc:subject>");
                out.append(StreamUtility.enc((String) dc.subjects().get(i)));
                out.append("</dc:subject>\n");
            }
            for (int i = 0; i < dc.descriptions().size(); i++) {
                out.append("<dc:description>");
                out
                        .append(StreamUtility.enc((String) dc.descriptions()
                                .get(i)));
                out.append("</dc:description>\n");
            }
            for (int i = 0; i < dc.publishers().size(); i++) {
                out.append("<dc:publisher>");
                out.append(StreamUtility.enc((String) dc.publishers().get(i)));
                out.append("</dc:publisher>\n");
            }
            for (int i = 0; i < dc.contributors().size(); i++) {
                out.append("<dc:contributor>");
                out
                        .append(StreamUtility.enc((String) dc.contributors()
                                .get(i)));
                out.append("</dc:contributor>\n");
            }
            for (int i = 0; i < dc.dates().size(); i++) {
                String dateString =
                        StreamUtility.enc((String) dc.dates().get(i));
                out.append("<dc:date>");
                out.append(dateString);
                out.append("</dc:date>\n");
            }
            for (int i = 0; i < dc.types().size(); i++) {
                out.append("<dc:type>");
                out.append(StreamUtility.enc((String) dc.types().get(i)));
                out.append("</dc:type>\n");
            }
            for (int i = 0; i < dc.formats().size(); i++) {
                out.append("<dc:format>");
                out.append(StreamUtility.enc((String) dc.formats().get(i)));
                out.append("</dc:format>\n");
            }
            for (int i = 0; i < dc.identifiers().size(); i++) {
                out.append("<dc:identifier>");
                out.append(StreamUtility.enc((String) dc.identifiers().get(i)));
                out.append("</dc:identifier>\n");
            }
            for (int i = 0; i < dc.sources().size(); i++) {
                out.append("<dc:source>");
                out.append(StreamUtility.enc((String) dc.sources().get(i)));
                out.append("</dc:source>\n");
            }
            for (int i = 0; i < dc.languages().size(); i++) {
                out.append("<dc:language>");
                out.append(StreamUtility.enc((String) dc.languages().get(i)));
                out.append("</dc:language>\n");
            }
            for (int i = 0; i < dc.relations().size(); i++) {
                out.append("<dc:relation>");
                out.append(StreamUtility.enc((String) dc.relations().get(i)));
                out.append("</dc:relation>\n");
            }
            for (int i = 0; i < dc.coverages().size(); i++) {
                out.append("<dc:coverage>");
                out.append(StreamUtility.enc((String) dc.coverages().get(i)));
                out.append("</dc:coverage>\n");
            }
            for (int i = 0; i < dc.rights().size(); i++) {
                out.append("<dc:rights>");
                out.append(StreamUtility.enc((String) dc.rights().get(i)));
                out.append("</dc:rights>\n");
            }
        }
        out.append("</oai_dc:dc>\n");
        return out.toString();
    }

    public String getSearchFields(DOReader reader) throws ServerException {
        StringBuffer out = new StringBuffer();
        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        out.append("<fields>\n");
        out.append("<pid>" + StreamUtility.enc(reader.GetObjectPID())
                + "</pid>\n");
        String label = reader.GetObjectLabel();
        if (label == null) {
            label = "";
        }
        out.append("<label>" + StreamUtility.enc(label) + "</label>\n");

        /*
         * FIXME: gone is the <cModel> search field, since it's no longer a
         * single object property. Do we want to expose the HAS_MODEL
         * relationship in field search?
         */

        out.append("<state>" + StreamUtility.enc(reader.GetObjectState())
                + "</state>\n");
        String ownerId = reader.getOwnerId();
        if (ownerId == null) {
            ownerId = "";
        }
        out.append("<ownerId>" + StreamUtility.enc(ownerId) + "</ownerId>\n");
        out.append("<cDate>" + formatter.format(reader.getCreateDate())
                + "</cDate>\n");
        out.append("<cDateAsNum>" + reader.getCreateDate().getTime()
                + "</cDateAsNum>\n");
        out.append("<mDate>" + formatter.format(reader.getLastModDate())
                + "</mDate>\n");
        out.append("<mDateAsNum>" + reader.getLastModDate().getTime()
                + "</mDateAsNum>\n");
        String dc = getDublinCoreFields(reader);
        if (dc != null) {
            out.append(dc);
        }
        out.append("</fields>");
        return out.toString();
    }

    public String getDublinCoreFields(DOReader reader) throws ServerException {
        StringBuffer out = new StringBuffer();
        DatastreamXMLMetadata dcmd = null;
        try {
            dcmd = (DatastreamXMLMetadata) reader.GetDatastream("DC", null);
        } catch (ClassCastException cce) {
            throw new ObjectIntegrityException("Object "
                    + reader.GetObjectPID()
                    + " has a DC datastream, but it's not inline XML.");
        }
        if (dcmd != null) {
            InputStream in = dcmd.getContentStream();
            DCFields dc = new DCFields(in);
            for (int i = 0; i < dc.titles().size(); i++) {
                out.append("<title>");
                out.append(StreamUtility.enc((String) dc.titles().get(i)));
                out.append("</title>\n");
            }
            for (int i = 0; i < dc.creators().size(); i++) {
                out.append("<creator>");
                out.append(StreamUtility.enc((String) dc.creators().get(i)));
                out.append("</creator>\n");
            }
            for (int i = 0; i < dc.subjects().size(); i++) {
                out.append("<subject>");
                out.append(StreamUtility.enc((String) dc.subjects().get(i)));
                out.append("</subject>\n");
            }
            for (int i = 0; i < dc.descriptions().size(); i++) {
                out.append("<description>");
                out
                        .append(StreamUtility.enc((String) dc.descriptions()
                                .get(i)));
                out.append("</description>\n");
            }
            for (int i = 0; i < dc.publishers().size(); i++) {
                out.append("<publisher>");
                out.append(StreamUtility.enc((String) dc.publishers().get(i)));
                out.append("</publisher>\n");
            }
            for (int i = 0; i < dc.contributors().size(); i++) {
                out.append("<contributor>");
                out
                        .append(StreamUtility.enc((String) dc.contributors()
                                .get(i)));
                out.append("</contributor>\n");
            }
            for (int i = 0; i < dc.dates().size(); i++) {
                String dateString =
                        StreamUtility.enc((String) dc.dates().get(i));
                out.append("<date>");
                out.append(dateString);
                out.append("</date>\n");

                // FIXIT! this is not a valid DC element,
                // but is here to support searching.
                long dateNum = parseDateAsNum(dateString);
                if (dateNum != -1) {
                    out.append("<dateAsNum>");
                    out.append(dateNum);
                    out.append("</dateAsNum>");
                }
            }
            for (int i = 0; i < dc.types().size(); i++) {
                out.append("<type>");
                out.append(StreamUtility.enc((String) dc.types().get(i)));
                out.append("</type>\n");
            }
            for (int i = 0; i < dc.formats().size(); i++) {
                out.append("<format>");
                out.append(StreamUtility.enc((String) dc.formats().get(i)));
                out.append("</format>\n");
            }
            for (int i = 0; i < dc.identifiers().size(); i++) {
                out.append("<identifier>");
                out.append(StreamUtility.enc((String) dc.identifiers().get(i)));
                out.append("</identifier>\n");
            }
            for (int i = 0; i < dc.sources().size(); i++) {
                out.append("<source>");
                out.append(StreamUtility.enc((String) dc.sources().get(i)));
                out.append("</source>\n");
            }
            for (int i = 0; i < dc.languages().size(); i++) {
                out.append("<language>");
                out.append(StreamUtility.enc((String) dc.languages().get(i)));
                out.append("</language>\n");
            }
            for (int i = 0; i < dc.relations().size(); i++) {
                out.append("<relation>");
                out.append(StreamUtility.enc((String) dc.relations().get(i)));
                out.append("</relation>\n");
            }
            for (int i = 0; i < dc.coverages().size(); i++) {
                out.append("<coverage>");
                out.append(StreamUtility.enc((String) dc.coverages().get(i)));
                out.append("</coverage>\n");
            }
            for (int i = 0; i < dc.rights().size(); i++) {
                out.append("<rights>");
                out.append(StreamUtility.enc((String) dc.rights().get(i)));
                out.append("</rights>\n");
            }
            return out.toString();
        }
        // FIXIT!! What do we want to return if no DC records exists?
        return null;
    }

    private String getDissIndexURL(String reposBaseURL,
                                   String PID,
                                   Date versDateTime) {
        String dissIndexURL = null;

        if (versDateTime == null) {
            dissIndexURL = reposBaseURL + "/fedora/get/" + PID;
        } else {
            dissIndexURL =
                    reposBaseURL + "/fedora/get/" + PID + "/"
                            + DateUtility.convertDateToString(versDateTime);
        }
        return dissIndexURL;
    }

    private String getItemIndexURL(String reposBaseURL,
                                   String PID,
                                   Date versDateTime) {
        String itemIndexURL = null;

        if (versDateTime == null) {
            itemIndexURL =
                    reposBaseURL + "/fedora/get/" + PID
                            + "/fedora-system:3/viewItemIndex";
        } else {
            itemIndexURL =
                    reposBaseURL + "/fedora/get/" + PID
                            + "/fedora-system:3/viewItemIndex/"
                            + DateUtility.convertDateToString(versDateTime);
        }
        return itemIndexURL;
    }

    private String getItemDissURL(String reposBaseURL,
                                  String PID,
                                  String datastreamID,
                                  Date versDateTime) {
        String itemDissURL = null;

        if (versDateTime == null) {
            itemDissURL =
                    reposBaseURL + "/fedora/get/" + PID + "/" + datastreamID;
        } else {
            itemDissURL =
                    reposBaseURL + "/fedora/get/" + PID + "/" + datastreamID
                            + "/"
                            + DateUtility.convertDateToString(versDateTime);
        }
        return itemDissURL;
    }

    // returns -1 if can't parse as date
    private long parseDateAsNum(String str) {
        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date d = formatter.parse(str);
            return d.getTime();
        } catch (ParseException pe) {
            return -1;
        }
    }
}