/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.server.resourceIndex;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
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
import org.trippi.TripleIterator;
import org.trippi.TripleUpdate;
import org.trippi.TriplestoreConnector;
import org.trippi.TriplestoreWriter;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;

import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.DOReader;

/**
 * Implementation of the <code>ResourceIndex</code>.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexImpl implements ResourceIndex {

    /** Interface to the underlying triplestore. */
    private TriplestoreConnector _connector;

    /** Writer for the underlying triplestore. */
    private TriplestoreWriter _writer;

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

    public ResourceIndexImpl(TriplestoreConnector connector,
                             TripleGenerator generator,
                             int indexLevel,
                             boolean syncUpdates) {
        _connector = connector;
        _writer = _connector.getWriter();
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
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTriples(_generator.getTriplesForBDef(reader), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void addDataObject(DOReader reader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTriples(_generator.getTriplesForDataObject(reader), false);
        }
    }
  
    /**
     * {@inheritDoc}
     */
    public void addCModelObject(DOReader reader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTriples(_generator.getTriplesForCModelObject(reader), false);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void modifyBDefObject(BDefReader oldReader, BDefReader newReader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTripleDiffs(_generator.getTriplesForBDef(oldReader),
                        _generator.getTriplesForBDef(newReader));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void modifyDataObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTripleDiffs(_generator.getTriplesForDataObject(oldReader),
                        _generator.getTriplesForDataObject(newReader));
        }
    }
   
    /**
     * {@inheritDoc}
     */
    public void modifyCModelObject(DOReader oldReader, DOReader newReader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTripleDiffs(_generator.getTriplesForCModelObject(oldReader),
                        _generator.getTriplesForCModelObject(newReader));
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteBDefObject(BDefReader oldReader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTriples(_generator.getTriplesForBDef(oldReader), true);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void deleteDataObject(DOReader oldReader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTriples(_generator.getTriplesForDataObject(oldReader), true);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void deleteCModelObject(DOReader oldReader)
            throws ResourceIndexException {
        if (_indexLevel > INDEX_LEVEL_OFF) {
            updateTriples(_generator.getTriplesForCModelObject(oldReader), true);
        }
    }
	
    /**
     * {@inheritDoc}
     */
	public void export(OutputStream out, RDFFormat format)
	        throws ResourceIndexException {
        try {
            TripleIterator it = _writer.findTriples(null, null, null, 0);
            it.setAliasMap(_writer.getAliasMap());
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
                _writer.delete(getTripleIterator(set), _syncUpdates);
            } else {
                _writer.add(getTripleIterator(set), _syncUpdates);
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

        // Delete any existing triples that are no longer desired,
        // leaving the ones we want in place
        HashSet<Triple> obsoleteTriples = new HashSet<Triple>(existing);
        obsoleteTriples.removeAll(desired);
        updateTriples(obsoleteTriples, true);

        // Add only new desired triples
        HashSet<Triple> newTriples = new HashSet<Triple>(desired);
        newTriples.removeAll(existing);
        updateTriples(newTriples, false);

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

    ///////////////////////////////
    // TriplestoreReader methods //
    ///////////////////////////////

    /**
     * {@inheritDoc}
     */
    public void setAliasMap(Map<String, String> aliasToPrefix) throws TrippiException {
        _writer.setAliasMap(aliasToPrefix);
    }

    /**
     * {@inheritDoc}
     */
    public Map<String, String> getAliasMap() throws TrippiException {
        return _writer.getAliasMap();
    }

    /**
     * {@inheritDoc}
     */
    public TupleIterator findTuples(String queryLang, String tupleQuery,
            int limit, boolean distinct)
            throws TrippiException {
        return _writer.findTuples(queryLang, tupleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public int countTuples(String queryLang, String tupleQuery, int limit,
            boolean distinct)
            throws TrippiException {
        return _writer.countTuples(queryLang, tupleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public TripleIterator findTriples(String queryLang, String tripleQuery,
            int limit, boolean distinct)
            throws TrippiException {
        return _writer.findTriples(queryLang, tripleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public int countTriples(String queryLang, String tripleQuery, int limit,
            boolean distinct)
            throws TrippiException {
        return _writer.countTriples(queryLang, tripleQuery, limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public TripleIterator findTriples(SubjectNode subject, 
            PredicateNode predicate, ObjectNode object, int limit)
            throws TrippiException {
        return _writer.findTriples(subject, predicate, object, limit);
    }

    /**
     * {@inheritDoc}
     */
    public int countTriples(SubjectNode subject, PredicateNode predicate,
            ObjectNode object, int limit)
            throws TrippiException {
        return _writer.countTriples(subject, predicate, object, limit);
    }

    /**
     * {@inheritDoc}
     */
    public TripleIterator findTriples(String queryLang, String tupleQuery, 
            String tripleTemplate, int limit, boolean distinct)
            throws TrippiException {
        return _writer.findTriples(queryLang, tupleQuery, tripleTemplate,
                limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public int countTriples(String queryLang, String tupleQuery, 
            String tripleTemplate, int limit, boolean distinct)
            throws TrippiException {
        return _writer.countTriples(queryLang, tupleQuery, tripleTemplate,
                limit, distinct);
    }

    /**
     * {@inheritDoc}
     */
    public String[] listTupleLanguages() {
        return _writer.listTupleLanguages();
    }

    /**
     * {@inheritDoc}
     */
    public String[] listTripleLanguages() {
        return _writer.listTripleLanguages();
    }

    /**
     * {@inheritDoc}
     */
    public void close() throws TrippiException {
        _connector.close();
    }


    ///////////////////////////////
    // TriplestoreWriter methods //
    ///////////////////////////////
   
    /**
     * {@inheritDoc}
     */
	public void add(List<Triple> triples, boolean flush)
	        throws IOException, TrippiException {
        _writer.add(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void add(TripleIterator triples, boolean flush)
	        throws IOException, TrippiException {
        _writer.add(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void add(Triple triple, boolean flush)
	        throws IOException, TrippiException {
        _writer.add(triple, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(List<Triple> triples, boolean flush)
	        throws IOException, TrippiException {
        _writer.delete(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(TripleIterator triples, boolean flush)
	        throws IOException, TrippiException {
        _writer.delete(triples, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void delete(Triple triple, boolean flush)
	        throws IOException, TrippiException {
        _writer.delete(triple, flush);
	}

    /**
     * {@inheritDoc}
     */
	public void flushBuffer()
	        throws IOException, TrippiException {
        _writer.flushBuffer();
	}

    /**
     * {@inheritDoc}
     */
	public void setFlushErrorHandler(FlushErrorHandler h) {
		_writer.setFlushErrorHandler(h);
	}

    /**
     * {@inheritDoc}
     */
	public int getBufferSize() {
		return _writer.getBufferSize();
	}

    /**
     * {@inheritDoc}
     */
	public List<TripleUpdate> findBufferedUpdates(SubjectNode subject, 
	        PredicateNode predicate, ObjectNode object, int updateType) {
		return _writer.findBufferedUpdates(subject, predicate, object, 
		        updateType);
	}
	
}
