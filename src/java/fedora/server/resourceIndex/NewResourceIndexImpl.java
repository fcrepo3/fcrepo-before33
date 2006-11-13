package fedora.server.resourceIndex;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /** Interface to the underlying triplestore. */
    private TriplestoreWriter _trippi;

    /** The MethodInfoStore this instance will use. */
    private MethodInfoStore _methodInfoStore;

    /** The TripleGenerator this instance will use. */
    private TripleGenerator _generator;

    /** The current index level. */
    private int _indexLevel;

    /**
     * Whether triples should be flushed to storage before returning from
     * each object modification method.
     */
    private boolean _syncUpdates;

    ////////////////////
    // Initialization //
    ////////////////////

    public NewResourceIndexImpl(TriplestoreWriter trippi,
                                MethodInfoStore methodInfoStore,
                                TripleGenerator generator,
                                int indexLevel,
                                boolean syncUpdates) {
        _trippi = trippi;
        _methodInfoStore = methodInfoStore;
        _generator = generator;
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
        updateTriples(_generator.getTriplesForBDef(reader), false);
        _methodInfoStore.putBDefInfo(reader);
    }

    /**
     * {@inheritDoc}
     */
    public void addBMechObject(BMechReader reader)
            throws ResourceIndexException {
        updateTriples(_generator.getTriplesForBMech(reader), false);
        _methodInfoStore.putBMechInfo(reader);
    }

    /**
     * {@inheritDoc}
     */
    public void addDataObject(DOReader reader)
            throws ResourceIndexException {
        updateTriples(_generator.getTriplesForDataObject(reader), false);
    }

    /**
     * {@inheritDoc}
     */
    public void modifyBDefObject(BDefReader oldReader, BDefReader newReader)
            throws ResourceIndexException {
        updateTripleDiffs(_generator.getTriplesForBDef(oldReader),
                    _generator.getTriplesForBDef(newReader));
        _methodInfoStore.putBDefInfo(newReader);
    }

    /**
     * {@inheritDoc}
     */
    public void modifyBMechObject(BMechReader oldReader, BMechReader newReader)
            throws ResourceIndexException {
        updateTripleDiffs(_generator.getTriplesForBMech(oldReader),
                    _generator.getTriplesForBMech(newReader));
        _methodInfoStore.putBMechInfo(newReader);
    }

    /**
     * {@inheritDoc}
     */
    public void modifyDataObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException {
        updateTripleDiffs(_generator.getTriplesForDataObject(oldReader),
                    _generator.getTriplesForDataObject(newReader));
    }

    /**
     * {@inheritDoc}
     */
    public void deleteBDefObject(BDefReader oldReader)
            throws ResourceIndexException {
        updateTriples(_generator.getTriplesForBDef(oldReader), true);
        _methodInfoStore.deleteBDefInfo(getPID(oldReader));
    }

    /**
     * {@inheritDoc}
     */
    public void deleteBMechObject(BMechReader oldReader)
            throws ResourceIndexException {
        updateTriples(_generator.getTriplesForBMech(oldReader), true);
        _methodInfoStore.deleteBMechInfo(getPID(oldReader));
    }

    /**
     * {@inheritDoc}
     */
    public void deleteDataObject(DOReader oldReader)
            throws ResourceIndexException {
        updateTriples(_generator.getTriplesForDataObject(oldReader), true);
    }
	
    /**
     * {@inheritDoc}
     */
	public void export(OutputStream out, RDFFormat format)
	        throws ResourceIndexException {
        try {
            TripleIterator it = _trippi.findTriples(null, null, null, 0);
            it.setAliasMap(_trippi.getAliasMap());
            it.toStream(out, format);
        } catch (TrippiException e) {
            throw new ResourceIndexException("Unable to export RI", e);
        }
    }


    /////////////////////
    // Private Methods //
    /////////////////////

    /**
     * Applies the given adds or deletes to the triplestore.
     * If _syncUpdates is true, changes will be flushed before returning.
     */
    private void updateTriples(Set<Triple> set, boolean delete)
            throws ResourceIndexException {
        try {
            if (delete) {
                _trippi.delete(getTripleIterator(set), _syncUpdates);
            } else {
                _trippi.add(getTripleIterator(set), _syncUpdates);
            }
        } catch (Exception e) {
            throw new ResourceIndexException("Error updating triples", e);
        }
    }

    /**
     * Computes the difference between the given sets and applies
     * the appropriate deletes and adds to the triplestore.
     * If _syncUpdates is true, changes will be flushed before returning.
     */
    private void updateTripleDiffs(Set<Triple> existing, Set<Triple> desired)
            throws ResourceIndexException {

        // delete all in existing but not desired
        existing.removeAll(desired);
        updateTriples(existing, true);

        // add all in desired but not existing
        desired.removeAll(existing);
        updateTriples(desired, false);
    }

    /**
     * Gets a Trippi TripleIterator for the given set.
     */
    private static TripleIterator getTripleIterator(final Set<Triple> set) {
        return new TripleIterator() {
            private Iterator<Triple> _iter = set.iterator();
            public boolean hasNext() { return _iter.hasNext(); }
            public Triple next() { return _iter.next(); }
            public void close() { }
        };
    }

    /**
     * Gets the PID for a given object.  If there's an error, the
     * original exception is wrapped in a ResourceIndeException.
     */
    private static String getPID(DOReader reader) throws ResourceIndexException {
        try {
            return reader.GetObjectPID();
        } catch (Exception e) {
            throw new ResourceIndexException("Unable to get PID", e);
        }
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
