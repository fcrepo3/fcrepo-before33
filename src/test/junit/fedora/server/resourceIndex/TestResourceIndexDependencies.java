package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fedora.common.PID;
import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamXMLMetadata;
import fedora.server.storage.types.DigitalObject;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TripleMaker;
import org.trippi.TupleIterator;

/**
 * @author Edwin Shin
 *  
 */
public class TestResourceIndexDependencies extends TestResourceIndex {
    public static final String DEP_PRED = "info:fedora/fedora-system:def/relations-external#isMemberOfCollection";

    protected void setUp() throws Exception {
        super.setUp();
    }
    
    public void testFoo() throws Exception {
        DigitalObject bdef = getBdef("demo_Collection.xml");
        DigitalObject bmech = getBmech("demo_DualResImageCollection.xml");
        DigitalObject dobj = getDataobject("demo_SmileyStuff.xml");
        
        m_ri.addDigitalObject(bdef);
        m_ri.addDigitalObject(bmech);
        m_ri.addDigitalObject(dobj);
        m_ri.commit();
        export("/tmp/rdf/out.rdf");
        
        //query2();
        //dobj.
    }

//    public void testAddDigitalObject() throws Exception {
//        String[] filenames = {"demo_ri1200.xml", "demo_ri1201.xml", "demo_ri1202.xml"};
//        DigitalObject obj;
//        
//        List results = new ArrayList();
//        for (int i = 0; i < filenames.length; i++) {
//            obj = getDataobject(filenames[i]);
//            cDependencies(obj);
//            m_ri.addDigitalObject(obj);
//            m_ri.commit();
//            results.add(query());
//        }
//        Iterator it = results.iterator();
//        while (it.hasNext()) {
//            System.out.println(it.next());
//        }
//        
//    }

    private DigitalObject getDataobject(String filename) throws Exception {
        return getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR + "/dataobjects/" + filename));
    }
    
    private DigitalObject getBdef(String filename) throws Exception {
        return getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bdefs/" + filename));
    }
    
    private DigitalObject getBmech(String filename) throws Exception {
        return getFoxmlObject(new File(DEMO_OBJECTS_ROOT_DIR + "/bmechs/" + filename));
    }
    
    private void cDependencies(DigitalObject obj) throws Exception {
        List children, parents;
        children = getChildren(obj);
        parents = getParents(obj);
        
        Iterator it = parents.iterator();
        Iterator cit = children.iterator();
        while (it.hasNext()) {
            String parent = (String)it.next();
            if (children.contains(parent)) {
                fail("child, " + obj.getPid() + ", contains parent, " + parent);
            } else {
                while (cit.hasNext()) {
                    String child = (String)cit.next();
                    System.out.println("* would add " + parent + " as a parent to " + child);
                }
                
            }
        }
        // for each parent in parents, 
        //      if children contains parent, fail
        //      else add parent as a parent to each child in children
        
    }
    
    private List getChildren(DigitalObject obj) throws Exception {
        URIReference predicate, object;
        predicate = TripleMaker.createResource(DEP_PRED);
        object = TripleMaker.createResource(PID.toURI(obj.getPid()));
        TripleIterator it = m_ri.findTriples(null, predicate, object, 0);
        Triple t;
        List children = new ArrayList();
        while (it.hasNext()) {
            t = (Triple)it.next();
            children.add(t.getSubject().toString());
        }
        return children;
    }
    
    private List getParents(DigitalObject obj) throws Exception {
        List parents = new ArrayList();
        URIReference predicate = TripleMaker.createResource(DEP_PRED);
        DatastreamXMLMetadata rels = getRels(obj);
        Triple t;
        TripleIterator it = TripleIterator.fromStream(rels.getContentStream(), 
                                                      RDFFormat.RDF_XML);
        while (it.hasNext()) {
            t = (Triple)it.next();
            if (t.getPredicate().equals(predicate)) {
                System.out.println("+++ adding predicate: " + t.getPredicate());
                parents.add(t.getObject().toString());
            }
        }
        return parents;
    }
    
    private String query() throws Exception {
        String query = "select $subject $object " +
                       "from <#ri> " +
                       "where $subject <" + DEP_PRED + "> $object";
        List children = new ArrayList();
        List parents = new ArrayList();
        
        TupleIterator it;
        it = m_ri.findTuples("itql", query, 0, false);
        StringBuffer sb = new StringBuffer();
        while (it.hasNext()) {
            Map tuples = it.next();
            sb.append("s: " + ((SubjectNode)tuples.get("subject")).toString() + 
                      " -> o: " + ((ObjectNode)tuples.get("object")).toString() + "\n");
        }
        return sb.toString();
        /*
         select $diss $dissDate $dep $depDate from <#ri> 
where 
$diss <http://tucana.org/tucana#is> <info:fedora/demo:SmileyStuff/demo:Collection/view>
and 
$diss <info:fedora/fedora-system:def/view#lastModifiedDate> $dissDate
and 
$diss <info:fedora/fedora-system:def/model#dependsOn> $dep
and 
$dep <info:fedora/fedora-system:def/view#lastModifiedDate> $depDate
         */
    }
    
    private void query2() throws Exception {
        String query = "select $diss $dissDate $dep $depDate from <#ri> " +
                       "where " +
                       "$diss <http://tucana.org/tucana#is> <info:fedora/demo:SmileyStuff/demo:Collection/view> " +
                       "and " +
                       "$diss <info:fedora/fedora-system:def/view#lastModifiedDate> $dissDate " +
                       "and " +
                       "$diss <info:fedora/fedora-system:def/model#dependsOn> $dep " +
                       "and " +
                       "$dep <info:fedora/fedora-system:def/view#lastModifiedDate> $depDate";
        TripleIterator it;
        m_ri.findTriples("itql", query, "$s $p $o", 0, false);
        it = m_ri.findTriples("itql", query, 0, false);
        String path = "/tmp/rdf/q2.rdf";
        it.toStream(new FileOutputStream(path), RDFFormat.RDF_XML);
    }
    
    private DatastreamXMLMetadata getRels(DigitalObject obj) throws Exception {
        Datastream ds = null;
        Iterator it;
        it = obj.datastreamIdIterator();
        while (it.hasNext()) {
            String datastreamID = (String)it.next();
            if (datastreamID.equalsIgnoreCase("RELS-EXT")) {
                ds = getLatestDatastream(obj.datastreams(datastreamID));
            }
        }
        return (DatastreamXMLMetadata)ds;
    }
    
    private Datastream getLatestDatastream(List datastreams) throws Exception {
        Iterator it = datastreams.iterator();
        long latestDSCreateDT = -1;
        Datastream ds, latestDS = null;
        while (it.hasNext()) {
            ds = (Datastream)it.next();
            if (ds.DSCreateDT == null) {
                throw new ResourceIndexException("Datastream, " + ds.DSVersionID + ", is missing create date");
            } else if (ds.DSCreateDT.getTime() > latestDSCreateDT) {
                latestDS = ds;
            }
        }
        return latestDS;
    }
}