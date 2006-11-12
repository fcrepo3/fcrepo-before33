package fedora.server.resourceIndex;

import java.io.IOException;
import java.io.OutputStream;

import java.util.List;
import java.util.Map;

import org.jrdf.graph.ObjectNode;
import org.jrdf.graph.PredicateNode;
import org.jrdf.graph.SubjectNode;
import org.jrdf.graph.Triple;

import org.trippi.FlushErrorHandler;
import org.trippi.RDFFormat;
import org.trippi.TriplestoreWriter;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.server.errors.ResourceIndexException;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;

/**
 * Implementation of the <code>ResourceIndex</code>.
 *
 * @author cwilper@cs.cornell.edu
 */
public class NewResourceIndexImpl implements NewResourceIndex {

    /**
     * Interface to the underlying triplestore.
     */
    private TriplestoreWriter _trippi;

    /**
     * The current index level.
     */
    private int _indexLevel;

    /**
     * Whether triples should be flushed to storage before returning.
     */
    private boolean _syncUpdates;

    ////////////////////
    // Initialization //
    ////////////////////

    public NewResourceIndexImpl(TriplestoreWriter trippi,
                                int indexLevel,
                                boolean syncUpdates) {
        _trippi = trippi;
        _indexLevel = indexLevel;
        _syncUpdates = syncUpdates;
    }


    ///////////////////////////
    // ResourceIndex methods //
    ///////////////////////////

    /**
     * {@inheritDoc}
     */
	public int getIndexLevel() {
        return _indexLevel;
    }

    /**
     * {@inheritDoc}
     */
    public void addBDefObject(BDefReader reader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void addBMechObject(BMechReader reader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void addDataObject(DOReader reader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void modifyBDefObject(BDefReader oldReader, BDefReader newReader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void modifyBMechObject(BMechReader oldReader, BMechReader newReader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void modifyDataObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void deleteBDefObject(BDefReader oldReader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void deleteBMechObject(BMechReader oldReader)
            throws ResourceIndexException {
    }

    /**
     * {@inheritDoc}
     */
    public void deleteDataObject(DOReader oldReader)
            throws ResourceIndexException {
    }
	
    /**
     * {@inheritDoc}
     */
	public void export(OutputStream out, RDFFormat format)
	        throws ResourceIndexException {
    }
   

    ///////////////////////////////
    // TriplestoreReader methods //
    ///////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void setAliasMap(Map aliasToPrefix) throws TrippiException {
        _trippi.setAliasMap(aliasToPrefix);
    }

    /**
     * {@inheritDoc}
     */
    public Map getAliasMap() throws TrippiException {
        return _trippi.getAliasMap();
    }

    /**
     * {@inheritDoc}
     */
    public TupleIterator findTuples(String queryLang, String tupleQuery,
            int limit, boolean distinct)
            throws TrippiException {
        return _trippi.findTuples(queryLang, tupleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public int countTuples(String queryLang, String tupleQuery, int limit,
            boolean distinct)
            throws TrippiException {
        return _trippi.countTuples(queryLang, tupleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public TripleIterator findTriples(String queryLang, String tripleQuery,
            int limit, boolean distinct)
            throws TrippiException {
        return _trippi.findTriples(queryLang, tripleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public int countTriples(String queryLang, String tripleQuery, int limit,
            boolean distinct)
            throws TrippiException {
        return _trippi.countTriples(queryLang, tripleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public TripleIterator findTriples(SubjectNode subject, 
            PredicateNode predicate, ObjectNode object, int limit)
            throws TrippiException {
        return _trippi.findTriples(subject, predicate, object, limit);
    }

    /**
     * {@inheritDoc}
     */
    public int countTriples(SubjectNode subject, PredicateNode predicate,
            ObjectNode object, int limit)
            throws TrippiException {
        return _trippi.countTriples(subject, predicate, object, limit);
    }

    /**
     * {@inheritDoc}
     */
    public TripleIterator findTriples(String queryLang, String tupleQuery, 
            String tripleTemplate, int limit, boolean distinct)
            throws TrippiException {
        return _trippi.findTriples(queryLang, tupleQuery, tripleTemplate,
                limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public int countTriples(String queryLang, String tupleQuery, 
            String tripleTemplate, int limit, boolean distinct)
            throws TrippiException {
        return _trippi.countTriples(queryLang, tupleQuery, tripleTemplate,
                limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public String[] listTupleLanguages() {
        return _trippi.listTupleLanguages();
    }

    /**
     * {@inheritDoc}
     */
    public String[] listTripleLanguages() {
        return _trippi.listTripleLanguages();
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws TrippiException {
        _trippi.close();
    }


    ///////////////////////////////
    // TriplestoreWriter methods //
    ///////////////////////////////
   
    /**
     * {@inheritDoc}
     */
	public void add(List triples, boolean flush)
	        throws IOException, TrippiException {
        _trippi.add(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void add(TripleIterator triples, boolean flush)
	        throws IOException, TrippiException {
        _trippi.add(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void add(Triple triple, boolean flush)
	        throws IOException, TrippiException {
        _trippi.add(triple, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(List triples, boolean flush)
	        throws IOException, TrippiException {
        _trippi.delete(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(TripleIterator triples, boolean flush)
	        throws IOException, TrippiException {
        _trippi.delete(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(Triple triple, boolean flush)
	        throws IOException, TrippiException {
        _trippi.delete(triple, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void flushBuffer()
	        throws IOException, TrippiException {
        _trippi.flushBuffer();
	}

    /**
     * {@inheritDoc}
     */
	public void setFlushErrorHandler(FlushErrorHandler h) {
		_trippi.setFlushErrorHandler(h);
	}

    /**
     * {@inheritDoc}
     */
	public int getBufferSize() {
		return _trippi.getBufferSize();
	}

    /**
     * {@inheritDoc}
     */
	public List findBufferedUpdates(SubjectNode subject, 
	        PredicateNode predicate, ObjectNode object, int updateType) {
		return _trippi.findBufferedUpdates(subject, predicate, object, 
		        updateType);
	}
	
}
