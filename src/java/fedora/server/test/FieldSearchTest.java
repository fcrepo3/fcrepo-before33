package fedora.server.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

import fedora.oai.sample.RandomDCMetadataFactory;
import fedora.server.StdoutLogging;
import fedora.server.search.Condition;
//import fedora.server.search.FieldSearchExistImpl;
import fedora.server.search.FieldSearchQuery;
import fedora.server.search.FieldSearchSQLImpl;
import fedora.server.search.ObjectFields;
import fedora.server.storage.ConnectionPool;
import fedora.server.storage.DirectoryBasedRepositoryReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.SimpleDOReader;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSLikeDODeserializer;
import fedora.server.storage.translation.METSLikeDOSerializer;
import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DatastreamXMLMetadata;

/**
 *
 * <p><b>Title:</b> FieldSearchTest.java</p>
 * <p><b>Description:</b> Tests the implementation of the FieldSearch interface,
 * FieldSearchSQLImpl.</p>
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
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class FieldSearchTest
        extends TestCase {

    private File m_repoDir;
    private File m_existDir;
    private DirectoryBasedRepositoryReader m_repoReader;
    //private FieldSearchExistImpl m_fieldSearch;
    private FieldSearchSQLImpl m_fieldSearch;
    private SimpleDateFormat m_formatter=
            new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
    private ConnectionPool m_cPool;

    public FieldSearchTest(String fedoraHome, String label) {
        super(label);
        m_repoDir=new File(new File(fedoraHome), "server/demo");
        m_existDir=new File(new File(fedoraHome), "server/exist09");
    }

    public void setUp() {
        try {
            String mets="mets11fedora1";
            HashMap sers=new HashMap();
            sers.put(mets, new METSLikeDOSerializer());
            HashMap desers=new HashMap();
            desers.put(mets, new METSLikeDODeserializer());
            DOTranslatorImpl translator=new DOTranslatorImpl(sers, desers, null);
            translator.setLogLevel(0);
            m_repoReader=new DirectoryBasedRepositoryReader(m_repoDir, translator,
                    mets, mets, mets, "UTF-8", null);
            m_repoReader.setLogLevel(0);
            //m_fieldSearch=new FieldSearchExistImpl(m_existDir.toString(), null);
            m_cPool=new ConnectionPool( "com.mckoi.JDBCDriver",
                    "jdbc:mckoi://localhost/", "fedoraAdmin", "fedoraAdmin", 5,
                    10, true);
            m_fieldSearch=new FieldSearchSQLImpl(m_cPool, m_repoReader, 50, 50, null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testDeleteAll() {
        try {
            String[] pids=m_repoReader.listObjectPIDs(null);
            for (int i=0; i<pids.length; i++) {
                m_fieldSearch.delete(pids[i]);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testUpdateAll() {
        try {
            String[] pids=m_repoReader.listObjectPIDs(null);
            for (int i=0; i<pids.length; i++) {
                DOReader r=m_repoReader.getReader(null, pids[i]);
                m_fieldSearch.update(r);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testSimpleSearch() {
        try {
            List results;
            //FIXME: limit # of returned results by...what?
            results=m_fieldSearch.findObjects(new String[] {"pid", "cDate"}, 100,
                    new FieldSearchQuery("*test*")).objectFieldsList();
            System.out.println("search('pid', 'test') got " + results.size() + " results.");
            for (int i=0; i<results.size(); i++) {
                ObjectFields f=(ObjectFields) results.get(i);
                System.out.println(f.getPid() + "'s cDate is " + m_formatter.format(f.getCDate()));
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void testPerformance(File dictionaryFile, int repeatMax, int wordMax,
            int numObjects, File indexingOutput, File regexOutput,
            File wordOutput, File fieldOutput) {
        StringBuffer out=new StringBuffer();
        StringBuffer outTwo=new StringBuffer();
        StringBuffer outThree=new StringBuffer();
        StringBuffer outFour=new StringBuffer();
        StringBuffer mem=new StringBuffer();
        File memInfo=new File("memory.dat");
        int i=0;
        Date now=new Date();
        Runtime runTime=Runtime.getRuntime();
        long kbTotal=runTime.totalMemory()/1024;
        try {
            RandomDCMetadataFactory dcFactory=new RandomDCMetadataFactory(dictionaryFile);
            int n=0;
            for (i=0; i<numObjects; i++) {
                BasicDigitalObject obj=new BasicDigitalObject();
                obj.setPid("perftest:" + i);
                obj.setCreateDate(now);
                obj.setLastModDate(now);
                obj.setPid("perftest:" + i);
                DatastreamXMLMetadata ds=new DatastreamXMLMetadata();
                ds.xmlContent=dcFactory.get(repeatMax, wordMax).getBytes();
                ds.DSCreateDT=now;
                ds.DSVersionID="DC";
                ds.DSState="A";
                ds.DatastreamID="DC1.0";
                obj.datastreams("DC").add(ds);
                SimpleDOReader doReader=new SimpleDOReader(null, null, null,
                        null, null, "UTF-8", obj, null);
                long st=new Date().getTime();
                m_fieldSearch.update(doReader);
                long et=new Date().getTime();
                long tt=et-st;
                n++;
                out.append(i + " " + tt + "\n");
                if (n==50) {
                    System.err.println(i + " " + tt);
                    n=0;
                    /*
                    long sst=new Date().getTime();
                    m_fieldSearch.search(new String[] {"pid"}, "*cense*");
                    long set=new Date().getTime();
                    long stt=set-sst;
                    outTwo.append(i + " " + stt + "\n");
                    System.err.println(i + " " + stt);
                    sst=new Date().getTime();
                    m_fieldSearch.search(new String[] {"pid"}, "license");
                    set=new Date().getTime();
                    stt=set-sst;
                    outThree.append(i + " " + stt + "\n");
                    System.err.println(i + " " + stt);
                    sst=new Date().getTime();
                    m_fieldSearch.search(new String[] {"pid"}, Condition.getConditions("creator~gnu date~license"));
                    set=new Date().getTime();
                    stt=set-sst;
                    outFour.append(i + " " + stt + "\n");
                    System.err.println(i + " " + stt);
                    sendToFile(outTwo.toString(), regexOutput);
                    sendToFile(outThree.toString(), wordOutput);
                    sendToFile(outFour.toString(), fieldOutput); */
                    long kbFree=runTime.freeMemory()/1024;
                    long kbUsed=(kbTotal-kbFree);
                    double mbUsed=kbUsed/1024;
                    mem.append(i + " " + mbUsed + "\n");
                    System.err.println("Used " + kbUsed + "/" + kbTotal);
                    sendToFile(out.toString(), indexingOutput);
                    sendToFile(mem.toString(), memInfo);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            /*
            System.out.println("# finished");
            try {
                for (int x=0; x<i; x++) {
                    m_fieldSearch.delete("perftest:" + x);
                }
            } catch (Exception e) { }
            */
        }
    }

    private void sendToFile(String string, File file)
            throws Exception {
        PrintWriter out=new PrintWriter(new BufferedWriter(new FileWriter(file)));
        out.println(string);
        out.flush();
        out.close();
    }

    public void printSimpleSearch(String[] fields, String terms) {
        try {
            System.out.println("Searching...");
            printResults(fields, m_fieldSearch.findObjects(fields, 50,
                    new FieldSearchQuery(terms)).objectFieldsList());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": "
            + e.getMessage());
            e.printStackTrace();
        }
    }

    public void printAdvancedSearch(String[] fields, String conditionQuery) {
        try {
            System.out.println("Searching...");
            printResults(fields, m_fieldSearch.findObjects(fields, 50,
                    new FieldSearchQuery(Condition.getConditions(
                    conditionQuery))).objectFieldsList());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printResults(String[] fields, List results) {
        System.out.println("Got " + results.size() + " results.");
        for (int i=0; i<results.size(); i++) {
            ObjectFields f=(ObjectFields) results.get(i);
            for (int j=0; j<fields.length; j++) {
                String l=fields[j];
                if (l.equalsIgnoreCase("pid")) {
                    System.out.print("pid='" + f.getPid() + "' ");
                } else if (l.equalsIgnoreCase("label")) {
                    System.out.print("label='" + f.getLabel() + "' ");
                } else if (l.equalsIgnoreCase("fType")) {
                    System.out.print("fType='" + f.getFType() + "' ");
                } else if (l.equalsIgnoreCase("cModel")) {
                    System.out.print("cModel='" + f.getCModel() + "' ");
                } else if (l.equalsIgnoreCase("state")) {
                    System.out.print("state='" + f.getState() + "' ");
                } else if (l.equalsIgnoreCase("ownerId")) {
                    System.out.print("ownerId='" + f.getOwnerId() + "' ");
                } else if (l.equalsIgnoreCase("cDate")) {
                    System.out.print("cDate='" + m_formatter.format(f.getCDate()) + "' ");
                } else if (l.equalsIgnoreCase("mDate")) {
                    System.out.print("mDate='" + m_formatter.format(f.getMDate()) + "' ");
                } else if (l.equalsIgnoreCase("title")) {
                    System.out.print("title=" + printList(f.titles()) + " ");
                } else if (l.equalsIgnoreCase("creator")) {
                    System.out.print("creator=" + printList(f.creators()) + " ");
                } else if (l.equalsIgnoreCase("subject")) {
                    System.out.print("subject=" + printList(f.subjects()) + " ");
                } else if (l.equalsIgnoreCase("description")) {
                    System.out.print("description=" + printList(f.descriptions()) + " ");
                } else if (l.equalsIgnoreCase("publisher")) {
                    System.out.print("publisher=" + printList(f.publishers()) + " ");
                } else if (l.equalsIgnoreCase("contributor")) {
                    System.out.print("contributor=" + printList(f.contributors()) + " ");
                } else if (l.equalsIgnoreCase("date")) {
                    System.out.print("date=" + printList(f.dates()) + " ");
                } else if (l.equalsIgnoreCase("type")) {
                    System.out.print("type=" + printList(f.types()) + " ");
                } else if (l.equalsIgnoreCase("format")) {
                    System.out.print("format=" + printList(f.formats()) + " ");
                } else if (l.equalsIgnoreCase("identifier")) {
                    System.out.print("identifier=" + printList(f.identifiers()) + " ");
                } else if (l.equalsIgnoreCase("source")) {
                    System.out.print("source=" + printList(f.sources()) + " ");
                } else if (l.equalsIgnoreCase("language")) {
                    System.out.print("language=" + printList(f.languages()) + " ");
                } else if (l.equalsIgnoreCase("relation")) {
                    System.out.print("relation=" + printList(f.relations()) + " ");
                } else if (l.equalsIgnoreCase("coverage")) {
                    System.out.print("coverage=" + printList(f.coverages()) + " ");
                } else if (l.equalsIgnoreCase("rights")) {
                    System.out.print("rights=" + printList(f.rights()) + " ");
                }
            }
            System.out.println("");
        }
    }

    private String printList(List l) {
        StringBuffer out=new StringBuffer();
        out.append("{");
        for (int i=0; i<l.size(); i++) {
            if (i>0) {
                out.append(",");
            }
            out.append("'");
            out.append((String) l.get(i));
            out.append("'");
        }
        out.append("}");
        return out.toString();
    }

    private void shutdown() {
        //m_fieldSearch.shutdown();
        m_cPool.closeAllConnections();
    }

    public static void main(String[] args) {
        FieldSearchTest test=new FieldSearchTest(System.getProperty("fedora.home"), "Testing FieldSearch...Impl");
        test.setUp();
        if (args.length>0) {
            if (args[0].equals("delete")) {
                test.testDeleteAll();
            } else if (args[0].equals("perf")) {
                test.testPerformance(new File(args[1]), Integer.parseInt(args[2]),
                        Integer.parseInt(args[3]), Integer.parseInt(args[4]),
                        new File(args[5]), new File(args[6]), new File(args[7]),
                        new File(args[8]));
            } else if (args[0].equals("update")) {
                test.testUpdateAll();
            } else if (args[0].equals("simple")) {
                int li=args.length-1;
                String terms=args[li];
                String[] fields=new String[li-1];
                for (int i=0; i<li-1; i++) {
                    fields[i]=args[i+1];
                }
                test.printSimpleSearch(fields, terms);
            } else if (args[0].equals("advanced")) {
                int li=args.length-1;
                String conditionQuery=args[li];
                String[] fields=new String[li-1];
                for (int i=0; i<li-1; i++) {
                    fields[i]=args[i+1];
                }
                test.printAdvancedSearch(fields, conditionQuery);
            } else {
                System.out.println("ERROR: Unrecognized command, '" + args[0] + "'.");
            }
        } else {
            test.testDeleteAll();
            test.testUpdateAll();
            test.testSimpleSearch();
        }
        test.shutdown();
    }

}





























