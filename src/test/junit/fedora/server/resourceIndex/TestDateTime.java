package fedora.server.resourceIndex;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.jrdf.graph.ObjectNode;
import org.trippi.TupleIterator;

import fedora.server.storage.types.DigitalObject;

/**
 * @author Edwin Shin
 */
public class TestDateTime extends TestResourceIndex {
    public static final String DATE_AFTER = "<http://tucana.org/tucana#after>";
    public static final String DATE_BEFORE = "<http://tucana.org/tucana#before>";
    public static final int DF8601_MSEC = 0;
    public static final int DF8601_SEC = 1;
    public static final int FMT_COUNT = 2;
    
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
      
    
    public void testSecondPrecision() throws Exception {
        String dateString = "2005-01-14T22:33:44";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        
        assertEquals(dateString, queryDate());
    }
    
    public void testMillisPrecision() throws Exception {
        String dateString = "2005-01-14T22:33:44.555";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        
        assertEquals(dateString, queryDate());
    }
    
    public void testFractionalZero() throws Exception {
        String dateString = "2005-01-14T22:33:44.0";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString.substring(0, dateString.length() -2), queryDate());
    }
    
    public void testFractionalZero2() throws Exception {
        String dateString = "2005-01-14T22:33:44.00";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString.substring(0, dateString.length() -3), queryDate());
    }
    
    public void testFractionalZero3() throws Exception {
        String dateString = "2005-01-14T22:33:44.000";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString.substring(0, dateString.length() -4), queryDate());
    }
    
    public void testFractionalZero4() throws Exception {
        String dateString = "2005-01-14T22:33:44.01";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString, queryDate());
    }
    
    public void testFractionalZero5() throws Exception {
        String dateString = "2005-01-14T22:33:44.010";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString.substring(0, dateString.length() -1), queryDate());
    }
    
    public void testFractionalZero6() throws Exception {
        String dateString = "2005-01-14T22:33:44.1";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString, queryDate());
    }
    
    public void testFractionalZero7() throws Exception {
        String dateString = "2005-01-14T22:33:44.10";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString.substring(0, dateString.length() -1), queryDate());
    }
    
    public void testFractionalZero8() throws Exception {
        String dateString = "2005-01-14T22:33:44.100";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString.substring(0, dateString.length() -2), queryDate());
    }
    
    public void testAfter() throws Exception {
        String dateString = "2005-01-14T22:33:44.001";
        String compareToDate = "2005-01-14T22:33:44";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString, queryAfterDate(compareToDate));
    }
    
    public void testBefore() throws Exception {
        String dateString = "2005-01-14T22:33:44.001";
        String compareToDate = "2005-01-14T22:33:44.002";
        Date date = parseDate(dateString);
        
        DigitalObject obj = getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR
                + "/dataobjects/demo_ri1000.xml"));
        obj.setCreateDate(date);
        m_ri.addDigitalObject(obj);
        m_ri.commit();
        assertEquals(dateString, queryBeforeDate(compareToDate));
    }
    
    private Date parseDate(String lexicalForm) throws Exception {
        int pos = lexicalForm.length() - 4;
        int index = lexicalForm.indexOf('.', pos);
        if (index == -1) {
            lexicalForm = lexicalForm.concat(".000");
        } else {
            int pad = pos - index;
            while (pad < 0) {
                lexicalForm = lexicalForm.concat("0");
                pad++;
            }
        }
        return df.parse(lexicalForm);
    }
    
    private String queryAfterDate(String compareToDate) throws Exception {
        return queryDate(DATE_AFTER, compareToDate);
    }
    
    private String queryBeforeDate(String compareToDate) throws Exception {
        return queryDate(DATE_BEFORE, compareToDate);
    }
    
    private String queryDate() throws Exception {
        return queryDate(DATE_BEFORE, "2030-12-31T23:59:59.999");
    }
    
    private String queryDate(String predicate, String compareToDate) throws Exception {
        String query = "select $subject $date from <#ri> " +
                       "where ($subject <info:fedora/fedora-system:def/model#createdDate> $date) " +
                       "and $date " + predicate + " '" + compareToDate + "' in <#xsd>";

        TupleIterator it = m_ri.findTuples("itql", query, 0, true);
        Map tuples;
        String result = "";
        while (it.hasNext()) {
            tuples = it.next();
            result = ((ObjectNode)tuples.get("date")).toString();
        }
        return result;
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestDateTime.class);
    }
}
