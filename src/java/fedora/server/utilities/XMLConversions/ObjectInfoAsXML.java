package fedora.server.utilities.XMLConversions;

import java.io.InputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fedora.server.errors.ObjectIntegrityException;
import fedora.server.errors.ServerException;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.Disseminator;

// FIXIT!! This class was copied from fedora.server.search into
// fedora.server.utilties for more general purpose use.  Decide on one official copy
// in one of these two packages, and use throughout the system.
import fedora.server.utilities.DCFields;

// FIXIT!  Note that the method implementation of this class was taken
// from fedora.search.FieldSearchExistingImpl.java.  The method can be of
// general use in the system, thus it is in this class.  Looks towards
// eliminating the method from FieldSearchExistingImpl.java and calling
// this one from the utilities package.

/**
 * Use an object reader to get descriptive metadata out of the object
 * and encode the metadata in XML in accordance with objectinfo.xsd.
 * <p></p>
 *
 * @author cwilper@cs.cornell.edu
 */
public class ObjectInfoAsXML
{

    public ObjectInfoAsXML()
    {
    }

    public String getObjectProfile(DOReader reader)
            throws ServerException {
        StringBuffer out=new StringBuffer();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

        out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.append("<objectProfile"
              + " targetNamespace=\"http://www.fedora.info/definitions/1/0/access/\""
              + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
              + " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\""
              + " xsi:schemaLocation=\"http://www.fedora.info/definitions/1/0/access/"
              + " http://www.fedora.info/definitions/1/0/access/objectProfile.xsd\""
              + " PID=\"" + reader.GetObjectPID() + "\">\n");

        String label=reader.GetObjectLabel();
        if (label==null) label="";
        out.append("<label>" + label + "</label>\n");
        String objType = reader.getFedoraObjectType();
        out.append("<objectType>");
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
        out.append("</objectType>\n");
        String cModel=reader.getContentModelId();
        if (cModel==null) cModel="";
        out.append("<contentModel>" + cModel + "</contentModel>\n");
        out.append("<createDate>" + formatter.format(reader.getCreateDate()) + "</createDate>\n");
        out.append("<modDate>" + formatter.format(reader.getLastModDate()) + "</modDate>\n");
        Disseminator[] dissSet = reader.GetDisseminators(null);
        for (int i=0; i<dissSet.length; i++)
        {
          out.append("<disseminator>\n");
          out.append("<bDefPID>" + dissSet[i].bDefID + "</bDefPID>\n");
          out.append("<bDefLabel>" +dissSet[i].bDefLabel + "</bDefLabel>\n");
          out.append("<dissLabel>" +dissSet[i].dissLabel + "</dissLabel>\n");
          out.append("<dissCreateDate>" +dissSet[i].dissCreateDT + "</dissCreateDate>\n");
          out.append("</disseminator>\n");
        }
        out.append("</objectProfile>\n");
        return out.toString();
    }

    public String getOAIDublinCore(DOReader reader) throws ServerException
    {
      StringBuffer out = new StringBuffer();
      DatastreamXMLMetadata dcmd = null;
      try {
          dcmd = (DatastreamXMLMetadata) reader.GetDatastream("DC", null);
      } catch (ClassCastException cce) {
          throw new ObjectIntegrityException("Object " + reader.GetObjectPID()
                  + " has a DC datastream, but it's not inline XML.");
      }
      out.append("<oai_dc:dc xmlns:oai_dc=\""
        + "http://www.openarchives.org/OAI/2.0/oai_dc/\""
        + " xmlns:dc=\"http://purl.org/dc/elements/1.1/\""
        + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
        + " xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/"
        + " http://www.openarchives.org/OAI/2.0/oai_dc.xsd\"" + ">\n");

        if (dcmd!=null) {
          InputStream in=dcmd.getContentStream();
          DCFields dc=new DCFields(in);
          for (int i=0; i<dc.titles().size(); i++) {
              out.append("<dc:title>");
              out.append((String) dc.titles().get(i));
              out.append("</dc:title>\n");
          }
          for (int i=0; i<dc.creators().size(); i++) {
              out.append("<dc:creator>");
              out.append((String) dc.creators().get(i));
              out.append("</dc:creator>\n");
          }
          for (int i=0; i<dc.subjects().size(); i++) {
              out.append("<dc:subject>");
              out.append((String) dc.subjects().get(i));
              out.append("</dc:subject>\n");
          }
          for (int i=0; i<dc.descriptions().size(); i++) {
              out.append("<dc:description>");
              out.append((String) dc.descriptions().get(i));
              out.append("</dc:description>\n");
          }
          for (int i=0; i<dc.publishers().size(); i++) {
              out.append("<dc:publisher>");
              out.append((String) dc.publishers().get(i));
              out.append("</dc:publisher>\n");
          }
          for (int i=0; i<dc.contributors().size(); i++) {
              out.append("<dc:contributor>");
              out.append((String) dc.contributors().get(i));
              out.append("</dc:contributor>\n");
          }
          for (int i=0; i<dc.dates().size(); i++) {
              String dateString=(String) dc.dates().get(i);
              out.append("<dc:date>");
              out.append(dateString);
              out.append("</dc:date>\n");
          }
          for (int i=0; i<dc.types().size(); i++) {
              out.append("<dc:type>");
              out.append((String) dc.types().get(i));
              out.append("</dc:type>\n");
          }
          for (int i=0; i<dc.formats().size(); i++) {
              out.append("<dc:format>");
              out.append((String) dc.formats().get(i));
              out.append("</dc:format>\n");
          }
          for (int i=0; i<dc.identifiers().size(); i++) {
              out.append("<dc:identifier>");
              out.append((String) dc.identifiers().get(i));
              out.append("</dc:identifier>\n");
          }
          for (int i=0; i<dc.sources().size(); i++) {
              out.append("<dc:source>");
              out.append((String) dc.sources().get(i));
              out.append("</dc:source>\n");
          }
          for (int i=0; i<dc.languages().size(); i++) {
              out.append("<dc:language>");
              out.append((String) dc.languages().get(i));
              out.append("</dc:language>\n");
          }
          for (int i=0; i<dc.relations().size(); i++) {
              out.append("<dc:relation>");
              out.append((String) dc.relations().get(i));
              out.append("</dc:relation>\n");
          }
          for (int i=0; i<dc.coverages().size(); i++) {
              out.append("<dc:coverage>");
              out.append((String) dc.coverages().get(i));
              out.append("</dc:coverage>\n");
          }
          for (int i=0; i<dc.rights().size(); i++) {
              out.append("<dc:rights>");
              out.append((String) dc.rights().get(i));
              out.append("</dc:rights>\n");
          }
        }
        out.append("</oai_dc:dc>\n");
        return out.toString();
    }

    public String getSearchFields(DOReader reader)
            throws ServerException {
        StringBuffer out=new StringBuffer();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        out.append("<fields>\n");
        out.append("<pid>" + reader.GetObjectPID() + "</pid>\n");
        String label=reader.GetObjectLabel();
        if (label==null) label="";
        out.append("<label>" + label + "</label>\n");
        out.append("<fType>" + reader.getFedoraObjectType() + "</fType>\n");
        String cModel=reader.getContentModelId();
        if (cModel==null) cModel="";
        out.append("<cModel>" + cModel + "</cModel>\n");
        out.append("<state>" + reader.GetObjectState() + "</state>\n");
        String locker=reader.getLockingUser();
        if (locker==null) locker="";
        out.append("<locker>" + locker + "</locker>\n");
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
              out.append((String) dc.titles().get(i));
              out.append("</title>\n");
          }
          for (int i=0; i<dc.creators().size(); i++) {
              out.append("<creator>");
              out.append((String) dc.creators().get(i));
              out.append("</creator>\n");
          }
          for (int i=0; i<dc.subjects().size(); i++) {
              out.append("<subject>");
              out.append((String) dc.subjects().get(i));
              out.append("</subject>\n");
          }
          for (int i=0; i<dc.descriptions().size(); i++) {
              out.append("<description>");
              out.append((String) dc.descriptions().get(i));
              out.append("</description>\n");
          }
          for (int i=0; i<dc.publishers().size(); i++) {
              out.append("<publisher>");
              out.append((String) dc.publishers().get(i));
              out.append("</publisher>\n");
          }
          for (int i=0; i<dc.contributors().size(); i++) {
              out.append("<contributor>");
              out.append((String) dc.contributors().get(i));
              out.append("</contributor>\n");
          }
          for (int i=0; i<dc.dates().size(); i++) {
              String dateString=(String) dc.dates().get(i);
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
              out.append((String) dc.types().get(i));
              out.append("</type>\n");
          }
          for (int i=0; i<dc.formats().size(); i++) {
              out.append("<format>");
              out.append((String) dc.formats().get(i));
              out.append("</format>\n");
          }
          for (int i=0; i<dc.identifiers().size(); i++) {
              out.append("<identifier>");
              out.append((String) dc.identifiers().get(i));
              out.append("</identifier>\n");
          }
          for (int i=0; i<dc.sources().size(); i++) {
              out.append("<source>");
              out.append((String) dc.sources().get(i));
              out.append("</source>\n");
          }
          for (int i=0; i<dc.languages().size(); i++) {
              out.append("<language>");
              out.append((String) dc.languages().get(i));
              out.append("</language>\n");
          }
          for (int i=0; i<dc.relations().size(); i++) {
              out.append("<relation>");
              out.append((String) dc.relations().get(i));
              out.append("</relation>\n");
          }
          for (int i=0; i<dc.coverages().size(); i++) {
              out.append("<coverage>");
              out.append((String) dc.coverages().get(i));
              out.append("</coverage>\n");
          }
          for (int i=0; i<dc.rights().size(); i++) {
              out.append("<rights>");
              out.append((String) dc.rights().get(i));
              out.append("</rights>\n");
          }
          return out.toString();
        }
        // FIXIT!! What do we want to return if no DC records exists?
        return null;
    }

    // returns -1 if can't parse as date
    private long parseDateAsNum(String str) {
        SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        try {
            Date d=formatter.parse(str);
            return d.getTime();
        } catch (ParseException pe) {
            return -1;
        }
    }
}