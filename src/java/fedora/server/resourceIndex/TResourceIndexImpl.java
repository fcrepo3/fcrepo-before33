package fedora.server.resourceIndex;

import java.io.*;
import java.util.*;

import org.trippi.*;
import org.jrdf.graph.*;

import fedora.server.*;
import fedora.server.errors.*;
import fedora.server.storage.types.*;

public class TResourceIndexImpl extends StdoutLogging
                                implements TResourceIndex {

    private TriplestoreConnector m_connector;
    private TriplestoreReader m_reader;
    private TriplestoreWriter m_writer;

    public TResourceIndexImpl(TriplestoreConnector connector, 
                              Logging target) {
		super(target);
        m_connector = connector;
        m_writer = m_connector.getWriter();
        m_reader = m_connector.getReader();
    }

//// from TriplestoreReader interface


    public void setAliasMap(Map aliasToPrefix) throws TrippiException {
        m_reader.setAliasMap(aliasToPrefix);
    }

    public Map getAliasMap() throws TrippiException {
        return m_reader.getAliasMap();
    }

    public TupleIterator findTuples(String queryLang,
                                    String tupleQuery,
                                    int limit,
                                    boolean distinct) throws TrippiException {
        return m_reader.findTuples(queryLang, tupleQuery, limit, distinct);
    }

    public int countTuples(String queryLang,
                           String tupleQuery,
                           int limit,
                           boolean distinct) throws TrippiException {
        return m_reader.countTuples(queryLang, tupleQuery, limit, distinct);
    }

    public TripleIterator findTriples(String queryLang,
                                      String tupleQuery,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_reader.findTriples(queryLang, tupleQuery, limit, distinct);
    }

    public int countTriples(String queryLang,
                            String tupleQuery,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_reader.countTriples(queryLang, tupleQuery, limit, distinct);
    }

    public TripleIterator findTriples(SubjectNode subject,
                                      PredicateNode predicate,
                                      ObjectNode object,
                                      int limit) throws TrippiException {
        return m_reader.findTriples(subject, predicate, object, limit);
    }

    public int countTriples(SubjectNode subject,
                            PredicateNode predicate,
                            ObjectNode object,
                            int limit) throws TrippiException {
        return m_reader.countTriples(subject, predicate, object, limit);
    }

    public TripleIterator findTriples(String queryLang,
                                      String tupleQuery,
                                      String tripleTemplate,
                                      int limit,
                                      boolean distinct) throws TrippiException {
        return m_reader.findTriples(queryLang, tupleQuery, tripleTemplate, limit, distinct);
    }

    public int countTriples(String queryLang,
                            String tupleQuery,
                            String tripleTemplate,
                            int limit,
                            boolean distinct) throws TrippiException {
        return m_reader.countTriples(queryLang, tupleQuery, tripleTemplate, limit, distinct);
    }

    public void dump(OutputStream out) throws IOException,
                                              TrippiException {
        m_reader.dump(out);
    }

    public String[] listTupleLanguages() {
        return m_reader.listTupleLanguages();
    }

    public String[] listTripleLanguages() {
        return m_reader.listTripleLanguages();
    }

    public void close() throws TrippiException {
        m_reader.close();
    }

///////////////////////////////////////////////////// 

	public int getIndexLevel() {
        return 0;
    }
	
	public void addDigitalObject(DigitalObject digitalObject) { } //throws ResourceIndexException
	
	public void addDatastream(DigitalObject digitalObject, String datastreamID) { } //throws ResourceIndexException;
	
	public void addDisseminator(DigitalObject digitalObject, String disseminatorID) { } //throws ResourceIndexException;
	
	public void modifyDigitalObject(DigitalObject digitalObject) { } //throws ResourceIndexException;
	
	public void modifyDatastream(Datastream ds) { } //;
	
	public void modifyDisseminator(Disseminator diss) { } //;
	
	public void deleteDigitalObject(String pid) { } //;
	
	public void deleteDatastream(Datastream ds) { } //;
	
	public void deleteDisseminator(Disseminator diss) { } //;



}
