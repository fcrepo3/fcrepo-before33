package fedora.server.access.defaultdisseminator;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fedora.server.access.ObjectProfile;
import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.ObjectMethodsDef;
import fedora.server.storage.types.MethodParmDef;
import fedora.server.utilities.DateUtility;
import fedora.server.utilities.StreamUtility;

// FIXIT!! This DCFields was copied from fedora.server.search into
// fedora.server.utilties for more general purpose use.  Decide on one official copy
// in one of these two packages, and use throughout the system.
// FIXIT!  Note that the some of the methods of this class were taken
// from fedora.search.FieldSearchExistingImpl.java.  The method can be of
// general use in the system, thus it is in this class.  Looks towards
// eliminating the method from FieldSearchExistingImpl.java and calling
// this one from the utilities package.
import fedora.server.utilities.DCFields;

/**
 *
 * <p><b>Title:</b> ObjectInfoAsXML.java</p>
 * <p><b>Description:</b> Provide an XML encoding of various object components.</p>
 *
 * @author payette@cs.cornell.edu
 * @version $Id$
 */
public class ObjectInfoAsXML
{

    public ObjectInfoAsXML()
    {
    }

    public String getObjectProfile(String reposBaseURL, ObjectProfile objProfile, Date versDateTime)
            throws ServerException {
        StringBuffer out = new StringBuffer();
        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        if (versDateTime == null || DateUtility.
            convertDateToString(versDateTime).equalsIgnoreCase(""))
        {
            out.append("<objectProfile "
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                + StreamUtility.enc(reposBaseURL)
                + "/objectProfile.xsd\"" + " pid=\"" + StreamUtility.enc(objProfile.PID) + "\" >");
        } else
        {
            out.append("<objectProfile "
                + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                + StreamUtility.enc(reposBaseURL)
                + "/objectProfile.xsd\"" + " pid=\"" + StreamUtility.enc(objProfile.PID) + "\""
                + " dateTime=\"" + DateUtility.convertDateToString(versDateTime) + "\" >");
        }

        // PROFILE FIELDS SERIALIZATION
        out.append("<objLabel>" + StreamUtility.enc(objProfile.objectLabel) + "</objLabel>");
        out.append("<objContentModel>" + StreamUtility.enc(objProfile.objectContentModel) + "</objContentModel>");
        String cDate = DateUtility.convertDateToString(objProfile.objectCreateDate);
        out.append("<objCreateDate>" + cDate + "</objCreateDate>");
        String mDate = DateUtility.convertDateToString(objProfile.objectLastModDate);
        out.append("<objLastModDate>" + mDate + "</objLastModDate>");
        String objType = objProfile.objectType;
        out.append("<objType>");
        if (objType.equalsIgnoreCase("O"))
        {
          out.append("Fedora Data Object");
        }
        else if (objType.equalsIgnoreCase("D"))
        {
          out.append("Fedora Behavior Definition Object");
        }
        else if (objType.equalsIgnoreCase("M"))
        {
          out.append("Fedora Behavior Mechanism Object");
        }
        out.append("</objType>");
        out.append("<objDissIndexViewURL>" + StreamUtility.enc(objProfile.dissIndexViewURL) + "</objDissIndexViewURL>");
        out.append("<objItemIndexViewURL>" + StreamUtility.enc(objProfile.itemIndexViewURL) + "</objItemIndexViewURL>");
        out.append("</objectProfile>");
        return out.toString();
    }

	public String getItemIndex(String reposBaseURL, DOReader reader, Date versDateTime)
			throws ServerException {
	    try {
		Datastream[] datastreams = reader.GetDatastreams(versDateTime, null);
		StringBuffer out = new StringBuffer();

		out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		if (versDateTime == null || DateUtility.
			convertDateToString(versDateTime).equalsIgnoreCase(""))
		{
			out.append("<objectItemIndex"
				+ " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
				+ StreamUtility.enc(reposBaseURL)
				+ "/objectItemIndex.xsd\""
				+ " PID=\"" + StreamUtility.enc(reader.GetObjectPID()) + "\">\n");
		} else
		{
			out.append("<objectItemIndex"
				+ " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
				+ " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
				+ " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
				+ StreamUtility.enc(reposBaseURL)
				+ "/objectItemIndex.xsd\""
				+ " PID=\"" + StreamUtility.enc(reader.GetObjectPID()) + "\""
				+ " dateTime=\"" + DateUtility.convertDateToString(versDateTime) + "\">\n");
		}


		for (int i=0; i<datastreams.length; i++)
		{
		  out.append("<item>\n");
		  out.append("<itemId>" + StreamUtility.enc(datastreams[i].DatastreamID) + "</itemId>\n");
		  String label = datastreams[i].DSLabel;
		  if (label==null) label="";
		  out.append("<itemLabel>" + StreamUtility.enc(label) + "</itemLabel>\n");
		  String itemDissURL = getItemDissURL(reposBaseURL, reader.GetObjectPID(),
			  datastreams[i].DatastreamID, versDateTime);
		  out.append("<itemURL>" + StreamUtility.enc(itemDissURL) + "</itemURL>\n");
		  out.append("<itemMIMEType>" + StreamUtility.enc(datastreams[i].DSMIME) + "</itemMIMEType>\n");
		  out.append("</item>\n");
		}
		out.append("</objectItemIndex>");
		return out.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new ObjectIntegrityException(e.getMessage());
	    }
	}

    public String getMethodIndex(String reposBaseURL, String PID, ObjectMethodsDef[] methods,
                                 Date versDateTime)
            throws ServerException {
        StringBuffer out=new StringBuffer();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        if (versDateTime == null || DateUtility.
            convertDateToString(versDateTime).equalsIgnoreCase(""))
        {
            out.append("<objectMethods "
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                + StreamUtility.enc(reposBaseURL)
                + "/objectMethods.xsd\"" + " pid=\"" + StreamUtility.enc(PID) + "\">");
        } else {
            out.append("<objectMethods "
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/ "
                + StreamUtility.enc(reposBaseURL)
                + "/objectMethods.xsd\"" + " pid=\"" + StreamUtility.enc(PID) + "\""
                + " dateTime=\"" + DateUtility.convertDateToString(versDateTime) + "\">");
        }

        String nextBdef = "null";
        String currentBdef = "";
        for (int i=0; i<methods.length; i++)
        {
          currentBdef = methods[i].bDefPID;
          if (!currentBdef.equalsIgnoreCase(nextBdef))
          {
            if (i != 0) out.append("</bdef>");
            out.append("<bdef pid=\"" + StreamUtility.enc(methods[i].bDefPID) + "\" >");
          }
		  String versDate = DateUtility.convertDateToString(methods[i].asOfDate);
          out.append("<method name=\"" + StreamUtility.enc(methods[i].methodName) + "\" asOfDateTime=\"" + versDate + "\" >");
          MethodParmDef[] methodParms = methods[i].methodParmDefs;
          for (int j=0; j<methodParms.length; j++)
          {
            out.append("<parm parmName=\"" + StreamUtility.enc(methodParms[j].parmName)
                + "\" parmDefaultValue=\"" + StreamUtility.enc(methodParms[j].parmDefaultValue)
                + "\" parmRequired=\"" + methodParms[j].parmRequired
                + "\" parmType=\"" + StreamUtility.enc(methodParms[j].parmType)
                + "\" parmLabel=\"" + StreamUtility.enc(methodParms[j].parmLabel) + "\" >");
            if (methodParms[j].parmDomainValues.length > 0 )
            {
              out.append("<parmDomainValues>");
              for (int k=0; k<methodParms[j].parmDomainValues.length; k++)
              {
                out.append("<value>" + StreamUtility.enc(methodParms[j].parmDomainValues[k])
                    + "</value>");
              }
              out.append("</parmDomainValues>");
            }
            out.append("</parm>");
          }

          out.append("</method>");
          nextBdef = currentBdef;
        }
        out.append("</bdef>");
        out.append("</objectMethods>");
        return out.toString();
    }

    public String getOAIDublinCore(DatastreamXMLMetadata dublinCore) throws ServerException
    {
      StringBuffer out = new StringBuffer();
      out.append("<oai_dc:dc xmlns:oai_dc=\""
        + "http://www.openarchives.org/OAI/2.0/oai_dc/\""
        + " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""
        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
        + " xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/"
        + " http://www.openarchives.org/OAI/2.0/oai_dc.xsd\"" + ">\n");

        if (dublinCore!=null) {
          InputStream in=dublinCore.getContentStream();
          DCFields dc=new DCFields(in);
          for (int i=0; i<dc.titles().size(); i++) {
              out.append("<dc:title>");
              out.append(StreamUtility.enc((String) dc.titles().get(i)));
              out.append("</dc:title>\n");
          }
          for (int i=0; i<dc.creators().size(); i++) {
              out.append("<dc:creator>");
              out.append(StreamUtility.enc((String) dc.creators().get(i)));
              out.append("</dc:creator>\n");
          }
          for (int i=0; i<dc.subjects().size(); i++) {
              out.append("<dc:subject>");
              out.append(StreamUtility.enc((String) dc.subjects().get(i)));
              out.append("</dc:subject>\n");
          }
          for (int i=0; i<dc.descriptions().size(); i++) {
              out.append("<dc:description>");
              out.append(StreamUtility.enc((String) dc.descriptions().get(i)));
              out.append("</dc:description>\n");
          }
          for (int i=0; i<dc.publishers().size(); i++) {
              out.append("<dc:publisher>");
              out.append(StreamUtility.enc((String) dc.publishers().get(i)));
              out.append("</dc:publisher>\n");
          }
          for (int i=0; i<dc.contributors().size(); i++) {
              out.append("<dc:contributor>");
              out.append(StreamUtility.enc((String) dc.contributors().get(i)));
              out.append("</dc:contributor>\n");
          }
          for (int i=0; i<dc.dates().size(); i++) {
              String dateString=StreamUtility.enc((String) dc.dates().get(i));
              out.append("<dc:date>");
              out.append(dateString);
              out.append("</dc:date>\n");
          }
          for (int i=0; i<dc.types().size(); i++) {
              out.append("<dc:type>");
              out.append(StreamUtility.enc((String) dc.types().get(i)));
              out.append("</dc:type>\n");
          }
          for (int i=0; i<dc.formats().size(); i++) {
              out.append("<dc:format>");
              out.append(StreamUtility.enc((String) dc.formats().get(i)));
              out.append("</dc:format>\n");
          }
          for (int i=0; i<dc.identifiers().size(); i++) {
              out.append("<dc:identifier>");
              out.append(StreamUtility.enc((String) dc.identifiers().get(i)));
              out.append("</dc:identifier>\n");
          }
          for (int i=0; i<dc.sources().size(); i++) {
              out.append("<dc:source>");
              out.append(StreamUtility.enc((String) dc.sources().get(i)));
              out.append("</dc:source>\n");
          }
          for (int i=0; i<dc.languages().size(); i++) {
              out.append("<dc:language>");
              out.append(StreamUtility.enc((String) dc.languages().get(i)));
              out.append("</dc:language>\n");
          }
          for (int i=0; i<dc.relations().size(); i++) {
              out.append("<dc:relation>");
              out.append(StreamUtility.enc((String) dc.relations().get(i)));
              out.append("</dc:relation>\n");
          }
          for (int i=0; i<dc.coverages().size(); i++) {
              out.append("<dc:coverage>");
              out.append(StreamUtility.enc((String) dc.coverages().get(i)));
              out.append("</dc:coverage>\n");
          }
          for (int i=0; i<dc.rights().size(); i++) {
              out.append("<dc:rights>");
              out.append(StreamUtility.enc((String) dc.rights().get(i)));
              out.append("</dc:rights>\n");
          }
        }
        out.append("</oai_dc:dc>\n");
        return out.toString();
    }

    public String getSearchFields(DOReader reader)
            throws ServerException {
        StringBuffer out=new StringBuffer();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        out.append("<fields>\n");
        out.append("<pid>" + StreamUtility.enc(reader.GetObjectPID()) + "</pid>\n");
        String label=reader.GetObjectLabel();
        if (label==null) label="";
        out.append("<label>" + StreamUtility.enc(label) + "</label>\n");
        out.append("<fType>" + StreamUtility.enc(reader.getFedoraObjectType()) + "</fType>\n");
        String cModel=reader.getContentModelId();
        if (cModel==null) cModel="";
        out.append("<cModel>" + StreamUtility.enc(cModel) + "</cModel>\n");
        out.append("<state>" + StreamUtility.enc(reader.GetObjectState()) + "</state>\n");
        String ownerId=reader.getOwnerId();
        if (ownerId==null) ownerId="";
        out.append("<ownerId>" + StreamUtility.enc(ownerId) + "</ownerId>\n");
        out.append("<cDate>" + formatter.format(reader.getCreateDate()) + "</cDate>\n");
        out.append("<cDateAsNum>" + reader.getCreateDate().getTime() + "</cDateAsNum>\n");
        out.append("<mDate>" + formatter.format(reader.getLastModDate()) + "</mDate>\n");
        out.append("<mDateAsNum>" + reader.getLastModDate().getTime() + "</mDateAsNum>\n");
        String dc = getDublinCoreFields(reader);
        if (dc != null)
        {
          out.append(dc);
        }
        out.append("</fields>");
        return out.toString();
    }

    public String getDublinCoreFields(DOReader reader) throws ServerException
    {
      StringBuffer out=new StringBuffer();
      DatastreamXMLMetadata dcmd=null;
      try {
          dcmd=(DatastreamXMLMetadata) reader.GetDatastream("DC", null);
      } catch (ClassCastException cce) {
          throw new ObjectIntegrityException("Object " + reader.GetObjectPID()
                  + " has a DC datastream, but it's not inline XML.");
      }
      if (dcmd!=null) {
          InputStream in=dcmd.getContentStream();
          DCFields dc=new DCFields(in);
          for (int i=0; i<dc.titles().size(); i++) {
              out.append("<title>");
              out.append(StreamUtility.enc((String) dc.titles().get(i)));
              out.append("</title>\n");
          }
          for (int i=0; i<dc.creators().size(); i++) {
              out.append("<creator>");
              out.append(StreamUtility.enc((String) dc.creators().get(i)));
              out.append("</creator>\n");
          }
          for (int i=0; i<dc.subjects().size(); i++) {
              out.append("<subject>");
              out.append(StreamUtility.enc((String) dc.subjects().get(i)));
              out.append("</subject>\n");
          }
          for (int i=0; i<dc.descriptions().size(); i++) {
              out.append("<description>");
              out.append(StreamUtility.enc((String) dc.descriptions().get(i)));
              out.append("</description>\n");
          }
          for (int i=0; i<dc.publishers().size(); i++) {
              out.append("<publisher>");
              out.append(StreamUtility.enc((String) dc.publishers().get(i)));
              out.append("</publisher>\n");
          }
          for (int i=0; i<dc.contributors().size(); i++) {
              out.append("<contributor>");
              out.append(StreamUtility.enc((String) dc.contributors().get(i)));
              out.append("</contributor>\n");
          }
          for (int i=0; i<dc.dates().size(); i++) {
              String dateString=StreamUtility.enc((String) dc.dates().get(i));
              out.append("<date>");
              out.append(dateString);
              out.append("</date>\n");

              // FIXIT! this is not a valid DC element,
              // but is here to support searching.
              long dateNum=parseDateAsNum(dateString);
              if (dateNum!=-1) {
                  out.append("<dateAsNum>");
                  out.append(dateNum);
                  out.append("</dateAsNum>");
              }
          }
          for (int i=0; i<dc.types().size(); i++) {
              out.append("<type>");
              out.append(StreamUtility.enc((String) dc.types().get(i)));
              out.append("</type>\n");
          }
          for (int i=0; i<dc.formats().size(); i++) {
              out.append("<format>");
              out.append(StreamUtility.enc((String) dc.formats().get(i)));
              out.append("</format>\n");
          }
          for (int i=0; i<dc.identifiers().size(); i++) {
              out.append("<identifier>");
              out.append(StreamUtility.enc((String) dc.identifiers().get(i)));
              out.append("</identifier>\n");
          }
          for (int i=0; i<dc.sources().size(); i++) {
              out.append("<source>");
              out.append(StreamUtility.enc((String) dc.sources().get(i)));
              out.append("</source>\n");
          }
          for (int i=0; i<dc.languages().size(); i++) {
              out.append("<language>");
              out.append(StreamUtility.enc((String) dc.languages().get(i)));
              out.append("</language>\n");
          }
          for (int i=0; i<dc.relations().size(); i++) {
              out.append("<relation>");
              out.append(StreamUtility.enc((String) dc.relations().get(i)));
              out.append("</relation>\n");
          }
          for (int i=0; i<dc.coverages().size(); i++) {
              out.append("<coverage>");
              out.append(StreamUtility.enc((String) dc.coverages().get(i)));
              out.append("</coverage>\n");
          }
          for (int i=0; i<dc.rights().size(); i++) {
              out.append("<rights>");
              out.append(StreamUtility.enc((String) dc.rights().get(i)));
              out.append("</rights>\n");
          }
          return out.toString();
        }
        // FIXIT!! What do we want to return if no DC records exists?
        return null;
    }

    private String getDissIndexURL(String reposBaseURL, String PID, Date versDateTime)
    {
        String dissIndexURL = null;

        if (versDateTime == null)
        {
          dissIndexURL = reposBaseURL + "/fedora/get/" + PID;
        }
        else
        {
            dissIndexURL = reposBaseURL + "/fedora/get/"
              + PID + "/" + DateUtility.convertDateToString(versDateTime);
        }
        return dissIndexURL;
    }

    private String getItemIndexURL(String reposBaseURL, String PID, Date versDateTime)
    {
        String itemIndexURL = null;

        if (versDateTime == null)
        {
          itemIndexURL = reposBaseURL + "/fedora/get/" + PID +
                         "/fedora-system:3/viewItemIndex";
        }
        else
        {
            itemIndexURL = reposBaseURL + "/fedora/get/"
              + PID + "/fedora-system:3/viewItemIndex/"
              + DateUtility.convertDateToString(versDateTime);
        }
        return itemIndexURL;
    }

	private String getItemDissURL(String reposBaseURL, String PID,
		String datastreamID, Date versDateTime)
	{
		String itemDissURL = null;

		if (versDateTime == null)
		{
		  itemDissURL = reposBaseURL + "/fedora/get/"
			+ PID
                        + "/"
                        + datastreamID;
		}
		else
		{
			itemDissURL = reposBaseURL + "/fedora/get/"
			  + PID
                          + "/"
                          + datastreamID
                          + "/"
			  + DateUtility.convertDateToString(versDateTime);
		}
		return itemDissURL;
	}

    // returns -1 if can't parse as date
    private long parseDateAsNum(String str) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date d=formatter.parse(str);
            return d.getTime();
        } catch (ParseException pe) {
            return -1;
        }
    }
}