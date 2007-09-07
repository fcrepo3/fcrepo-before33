package fedora.server.utilities;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jrdf.graph.Triple;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

public class FilteredTripleIterator extends TripleIterator
{
    TripleIterator m_baseIter;    
    boolean m_add;
    Triple m_filter;
    Triple m_next;
    boolean m_changeMade = false;
    
    public FilteredTripleIterator(TripleIterator baseIter, Triple filter, boolean add) throws TrippiException
    {
        m_baseIter = baseIter;
        m_filter = filter;
        m_add = add;
        Map map = m_baseIter.getAliasMap();
//        map.put("rel", "info:fedora/fedora-system:def/relations-external#");
        setAliasMap(map);  
        m_next = getNext();
    }
    
    public FilteredTripleIterator(Map aliasMap, Triple filter, boolean add) throws TrippiException
    {
        m_baseIter = null;
        m_filter = filter;
        m_add = add;
//        map.put("rel", "info:fedora/fedora-system:def/relations-external#");
        setAliasMap(aliasMap);  
        m_next = getNext();
    }

    @Override
    public boolean hasNext() throws TrippiException
    {
        return( m_next != null);
    }

    @Override
    public Triple next() throws TrippiException
    {
        Triple last = m_next;
        m_next = getNext();
        return last;
    }

    private Triple getNext() throws TrippiException
    {
        Triple next;
        if (!m_add)  // purging entries
        {
            next = m_baseIter.next();
            while(next != null && matches(next, m_filter))
            {
                m_changeMade = true;
                next = m_baseIter.next();
            }
            return(next);
        }
        else if (m_baseIter == null) // adding entry to empty set
        {
            m_changeMade = true;
            next = m_filter;
            m_filter = null;
            return(next);
        }
        else  // adding entry to existing set
        {
            next = m_baseIter.next();
            if (matches(next, m_filter))
            {
                m_filter = null;  // Triple to be added already present 
            }
            if (next == null) 
            {
                if (m_filter != null) m_changeMade = true;
                next = m_filter;
                m_filter = null;
            }
            return(next);
        }
    }

    private boolean matches(Triple next, Triple filter)
    {
        if( filter == null || next == null) return(false);
        return (partMatches(next.getSubject().toString(), filter.getSubject().toString()) &&
                partMatches(next.getPredicate().toString(), filter.getPredicate().toString()) &&
                partMatches(next.getObject().toString(), filter.getObject().toString()));
    }

    private boolean partMatches(String next, String filter)
    {
        if (next.equals(filter)) return(true);
        Map map = getAliasMap();
        Set keys = map.keySet();
        Iterator iter = keys.iterator();
        while (iter.hasNext())
        {
            String key = (String)(iter.next());
            if (next.startsWith(key + ":"))
            {
                next = next.replaceFirst(key + ":", (String)(map.get(key)));
            }
            if (filter.startsWith(key + ":"))
            {
                filter = filter.replaceFirst(key + ":", (String)(map.get(key)));
            }
            if (next.equals(filter)) return(true);
        }
        return false;
    }

    @Override
    public void close() throws TrippiException
    {
        // TODO Auto-generated method stub
        m_filter = null;
        if (m_baseIter != null) m_baseIter.close();
    }

    public boolean wasChangeMade()
    {
        return m_changeMade;
    }

}
