package fedora.oai.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fedora.oai.BadResumptionTokenException;
import fedora.oai.DateGranularitySupport;
import fedora.oai.DeletedRecordSupport;
import fedora.oai.IDDoesNotExistException;
import fedora.oai.OAIProvider;
import fedora.oai.Record;
import fedora.oai.SimpleHeader;
import fedora.oai.SimpleMetadataFormat;
import fedora.oai.SimpleRecord;
import fedora.oai.SimpleSetInfo;

/**
 *
 * <p><b>Title:</b> SampleOAIProvider.java</p>
 * <p><b>Description:</b> A sample implementation of OAIProvider for testing and demonstration
 * purposes.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class SampleOAIProvider
        implements OAIProvider {

    private static String s_rec1_identifier="sample:1";

    private static String s_rec1_metadata="        <oai_dc:dc\n"
        + "           xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\"\n"
        + "           xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n"
        + "           xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
        + "           xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/\n"
        + "           http://www.openarchives.org/OAI/2.0/oai_dc.xsd\">\n"
        + "          <dc:title>Using Structural Metadata to Localize Experience of \n"
        + "                    Digital Content</dc:title>\n"
        + "          <dc:creator>Dushay, Naomi</dc:creator>\n"
        + "          <dc:subject>Digital Libraries</dc:subject>\n"
        + "          <dc:description>With the increasing technical sophistication of\n"
        + "              both information consumers and providers, there is\n"
        + "              increasing demand for more meaningful experiences of digital\n"
        + "              information. We present a framework that separates digital\n"
        + "              object experience, or rendering, from digital object storage\n"
        + "              and manipulation, so the rendering can be tailored to\n"
        + "              particular communities of users.\n"
        + "          </dc:description>\n"
        + "          <dc:description>Comment: 23 pages including 2 appendices,\n"
        + "              8 figures</dc:description>\n"
        + "          <dc:date>2001-12-14</dc:date>\n"
        + "        </oai_dc:dc>";

    private static String s_rec1_about="        <provenance\n"
        + "         xmlns=\"http://www.openarchives.org/OAI/2.0/provenance\"\n"
        + "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"
        + "         xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/provenance\n"
        + "         http://www.openarchives.org/OAI/2.0/provenance.xsd\">\n"
        + "         <originDescription harvestDate=\"2002-01-01T11:10:01Z\" altered=\"true\">\n"
        + "          <baseURL>http://some.oa.org</baseURL>\n"
        + "          <identifier>oai:r2.org:klik001</identifier>\n"
        + "          <datestamp>2001-01-01</datestamp>\n"
        + "          <metadataNamespace>http://www.openarchives.org/OAI/2.0/oai_dc/</metadataNamespace>\n"
        + "          </originDescription>\n"
        + "        </provenance>";

    private SimpleHeader m_head1;
    private SimpleRecord m_rec1;

    public SampleOAIProvider() {
        HashSet s=new HashSet();
        s.add("cs");
        s.add("cornell");
        m_head1=new SimpleHeader(s_rec1_identifier, new Date(), s, true);
        HashSet a=new HashSet();
        a.add(s_rec1_about);
        m_rec1=new SimpleRecord(m_head1, s_rec1_metadata, a);
    }

    public String getRepositoryName() {
        return "My Repository";
    }

    public String getBaseURL(String protocol, String port) {
        return protocol + "://localhost:" + port + "/path/to/servlet";
    }

    public String getProtocolVersion() {
        return "2.0";
    }

    public Date getEarliestDatestamp() {
        return new Date();
    }

    public DeletedRecordSupport getDeletedRecordSupport() {
        return DeletedRecordSupport.NO;
    }

    public DateGranularitySupport getDateGranularitySupport() {
        return DateGranularitySupport.SECONDS;
    }

    public Set getAdminEmails() {
        HashSet s=new HashSet();
        s.add("nobody@nowhere.com");
        return s;
    }

    public Set getSupportedCompressionEncodings() {
        return new HashSet();
    }

    public Set getDescriptions() {
        return new HashSet();
    }

    public Record getRecord(String identifier, String metadataPrefix)
            throws IDDoesNotExistException {
            // throws CannotDisseminateFormatException, IDDoesNotExistException;
        if (identifier.equals("sample:1")) {
            return m_rec1;
        } else {
            throw new IDDoesNotExistException("An item with that id was not found.");
        }
    }

    public List getRecords(Date from, Date until, String metadataPrefix,
            String set) {
            // throws CannotDisseminateFormatException,
            // NoRecordsMatchException, NoSetHierarchyException;
        ArrayList a=new ArrayList();
        a.add(m_rec1);
        return a;
    }

    public List getRecords(String resumptionToken)
            throws BadResumptionTokenException {
        throw new BadResumptionTokenException("Sample doesn't support resumptionTokens.");
    }

    public List getHeaders(Date from, Date until, String metadataPrefix,
            String set) {
        ArrayList a=new ArrayList();
        a.add(m_head1);
        return a;
    }

    public List getHeaders(String resumptionToken)
            throws BadResumptionTokenException {
        throw new BadResumptionTokenException("Sample doesn't support resumptionTokens.");
    }

    public List getSets() {
        ArrayList a=new ArrayList();
        a.add(new SimpleSetInfo("Computer Science", "cs", new HashSet()));
        a.add(new SimpleSetInfo("Cornell University", "cornell", new HashSet()));
        return a;
    }

    public List getSets(String resumptionToken)
            throws BadResumptionTokenException {
        throw new BadResumptionTokenException("Sample doesn't support resumptionTokens.");
    }

    public Set getMetadataFormats(String id) {
        HashSet s=new HashSet();
        s.add(new SimpleMetadataFormat("oai_dc",
                "http://www.openarchives.org/OAI/2.0/oai_dc.xsd",
                "http://www.openarchives.org/OAI/2.0/oai_dc/"));
        return s;
    }

    public long getMaxSets() {
        return 10;
    }

    public long getMaxRecords() {
        return 10;
    }

    public long getMaxHeaders() {
        return 10;
    }
}