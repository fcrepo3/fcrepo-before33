package fedora.server.test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import junit.framework.TestCase;

import fedora.server.search.Condition;
import fedora.server.search.FieldSearchExistImpl;
import fedora.server.search.ObjectFields;
import fedora.server.storage.DirectoryBasedRepositoryReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.translation.DOTranslatorImpl;
import fedora.server.storage.translation.METSDODeserializer;
import fedora.server.storage.translation.METSDOSerializer;

/**
 * Tests the implementation of the FieldSearch interface, 
 * DirectoryBasedFieldSearch.
 *
 * @author cwilper@cs.cornell.edu
 */
public class FieldSearchTest 
        extends TestCase {
        
    private File m_repoDir;
    private File m_existDir;
    private DirectoryBasedRepositoryReader m_repoReader;
    private FieldSearchExistImpl m_fieldSearch;
    private SimpleDateFormat m_formatter=
            new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
        
    public FieldSearchTest(String fedoraHome, String label) {
        super(label);
        m_repoDir=new File(new File(fedoraHome), "demo");
        m_existDir=new File(new File(fedoraHome), "exist09");
    }
    
    public void setUp() {
        try {
            String mets="mets11fedora1";
            HashMap sers=new HashMap();
            sers.put(mets, new METSDOSerializer());
            HashMap desers=new HashMap();
            desers.put(mets, new METSDODeserializer());
            DOTranslatorImpl translator=new DOTranslatorImpl(sers, desers, null);
            m_repoReader=new DirectoryBasedRepositoryReader(m_repoDir, translator,
                    mets, mets, mets, "UTF-8", null);
            m_fieldSearch=new FieldSearchExistImpl(m_existDir.toString(), null);
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
            results=m_fieldSearch.search(new String[] {"pid", "cDate"}, "*test*");
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
    
    public void printSimpleSearch(String[] fields, String terms) {
        try {
            System.out.println("Searching...");
            printResults(fields, m_fieldSearch.search(fields, terms));
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
            
    public void printAdvancedSearch(String[] fields, String conditionQuery) {
        try {
            System.out.println("Searching...");
            printResults(fields, m_fieldSearch.search(fields, Condition.getConditions(conditionQuery)));
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
                } else if (l.equalsIgnoreCase("locker")) {
                    System.out.print("locker='" + f.getLocker() + "' ");
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
    
    public static void main(String[] args) {
        FieldSearchTest test=new FieldSearchTest(System.getProperty("fedora.home"), "Testing FieldSearchExistImpl");
        test.setUp();
        if (args.length>0) {
            if (args[0].equals("delete")) {
                test.testDeleteAll();
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
    }

}