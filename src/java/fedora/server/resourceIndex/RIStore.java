package fedora.server.resourceIndex;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

import fedora.server.errors.ResourceIndexException;

/**
 * Interface for TripleStore that backs the ResourceIndex
 * (e.g. Kowari or Jena)
 * 
 * In general, the idea is to use the Jena Model API for actions that need to
 * modify (e.g. add or remove statements) the underlying triplestore, but
 * use an RDF query language (e.g. RDQL or ITQL) to query the triplestore.
 * 
 * @author eddie
 */
public interface RIStore {
	/** Add statements from an RDF/XML serialization.
	 * @param rdfxml the source of the RDF/XML
	 * @param base the base to use when converting relative to absolute uri's.
	 * The base URI may be null if there are no relative URIs to convert.
	 * A base URI of "" may permit relative URIs to be used in the
	 * model unconverted.
	 */
	public void read(InputStream rdfxml, String base);
	
	public void write(OutputStream rdfxml);
	
	public RIResultIterator executeQuery(RIQuery query) throws ResourceIndexException;
	
	public Set getSupportedQueryLanguages();
	
	public void insert(String subject, String predicate, String object);
	
	public void insertLiteral(String subject, String predicate, String object);
	
	public void insertTypedLiteral(String subject, String predicate, String object, String datatype);
	
	public void insertLocalLiteral(String subject, String predicate, String object, String language);
}
