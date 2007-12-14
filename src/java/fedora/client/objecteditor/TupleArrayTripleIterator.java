package fedora.client.objecteditor;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.GraphElementFactoryException;
import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.Triple;
import org.trippi.RDFUtil;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

import fedora.server.storage.types.RelationshipTuple;


public class TupleArrayTripleIterator extends TripleIterator
{
    int size = 0;
    int index = 0;
    ArrayList<RelationshipTuple> m_TupleArray = null;
    static RDFUtil util = null;
    Map<String,String> m_map = null;
    
    public TupleArrayTripleIterator(ArrayList<RelationshipTuple> array, Map<String,String> map)
    {
        m_TupleArray = array;
        size = array.size();    
        if (util == null) util = new RDFUtil();
        m_map = map;
    }

    public TupleArrayTripleIterator(ArrayList<RelationshipTuple> array)
    {
        m_TupleArray = array;
        size = array.size();    
        if (util == null) util = new RDFUtil();
        m_map = new HashMap<String,String>();
        m_map.put("rel", "info:fedora/fedora-system:def/relations-external#");
        m_map.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    }

    public boolean hasNext() throws TrippiException
    {
        return (index < size);
    }

    @Override
    public Triple next() throws TrippiException
    {
        RelationshipTuple tuple = m_TupleArray.get(index++);
        try
        {
            Triple triple = util.createTriple(util.createResource(new URI(tuple.subject)),
                                         makePredicateResourceFromRel(tuple.predicate, m_map),
                                         makeObjectFromURIandLiteral(tuple.object, tuple.isLiteral, tuple.datatype));
            return(triple);
        }
        catch (GraphElementFactoryException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (URISyntaxException e)
        {
            throw new TrippiException("Invalid URI in Triple", e);// TODO Auto-generated catch block
        }
        return(null);
    }
    
    public static ObjectNode makeObjectFromURIandLiteral(String objURI, boolean isLiteral, String literalType) throws GraphElementFactoryException, URISyntaxException
    {
        ObjectNode obj = null;
        if (util == null) util = new RDFUtil();
        if (isLiteral) {
            if (literalType == null || literalType.length() == 0) {
                obj = util.createLiteral(objURI);
            } else {
                obj = util.createLiteral(objURI, new URI(literalType));
            }
        } else {
            obj = util.createResource(new URI(objURI)); 
        }
        return obj;
    }
    
    
    public static PredicateNode makePredicateResourceFromRel(String predicate, Map<String, String> map) throws URISyntaxException, GraphElementFactoryException
    {
        URI predURI = makePredicateFromRel(predicate, map);
        if (util == null) util = new RDFUtil();
        PredicateNode node = util.createResource(predURI);
        return node;
    }

    public static URI makePredicateFromRel(String relationship, Map map) throws URISyntaxException
    {
        String predicate = relationship;
        Set keys = map.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext())
        {
            String key = (String)(iter.next());
            if (predicate.startsWith(key + ":"))
            {
                predicate = predicate.replaceFirst(key + ":", (String)(map.get(key)));
            }
        }

        URI retVal = null;
        retVal = new URI(predicate);
        return(retVal);
    }

    @Override
    public void close() throws TrippiException
    {
        // TODO Auto-generated method stub
        
    }    
}