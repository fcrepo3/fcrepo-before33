package fedora.server.resourceIndex;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.jrdf.graph.Triple;
import org.jrdf.graph.URIReference;

import fedora.common.PID;
import fedora.server.errors.ResourceIndexException;
import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;
import fedora.server.storage.DOReader;
import fedora.server.storage.types.DSBinding;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.Disseminator;

/**
 * Generates RDF triples for Fedora objects with the help of a 
 * <code>BMechInfoProvider</code>.
 *
 * This implementation produces a superset of the triples generated
 * by <code>BaseTripleGenerator</code>.  Specifically, it includes
 * the following triples for each method the object exposes:
 *
 * For all methods:
 * <ul>
 *   <li> object disseminates method</li>
 *   <li> method dependsOn datastream (for each input datastream)</li>
 *   <li> method lastModifiedDate (latest of dependent datastreams, 
 *        or disseminator's)</li>
 *   <li> method state (disseminator's)</li>
 *   <li> method mimeType (possibly multiple, based on bMech)</li>
 *   <li> method isVolatile (true if any dependent datastreams are 
 *        E or R type)</li>
 *   <li> method disseminationType disTypeURI (for each permutation)</li>
 * </ul>
 *
 * @author cwilper@cs.cornell.edu
 */
public class BMechAwareTripleGenerator extends BaseTripleGenerator {

    /**
     * The provider this instance will use for behavior mechanism info.
     */
    private BMechInfoProvider _provider;

    /**
     * Construct an instance that will use the given provider.
     *
     * @param provider the provider to use for behavior mechanism info.
     */
    public BMechAwareTripleGenerator(BMechInfoProvider provider) {
        _provider = provider;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForBDef(BDefReader reader) 
            throws ResourceIndexException {
        Set<Triple> set = new HashSet<Triple>(
                super.getTriplesForBDef(reader));
        addDisseminationTriples(reader, set);
        return set;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForBMech(BMechReader reader) 
            throws ResourceIndexException {
        Set<Triple> set = new HashSet<Triple>(
                super.getTriplesForBMech(reader));
        addDisseminationTriples(reader, set);
        return set;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Triple> getTriplesForDataObject(DOReader reader) 
            throws ResourceIndexException {
        Set<Triple> set = new HashSet<Triple>(
                super.getTriplesForDataObject(reader));
        addDisseminationTriples(reader, set);
        return set;
    }

    /**
     * For each disseminator of the object, add all per-method
     * "dissemination" triples.
     */
    private void addDisseminationTriples(DOReader reader,
                                         Set<Triple> set) 
            throws ResourceIndexException {

        try {

            Datastream[] allDatastreams = reader.GetDatastreams(null, null);
            Disseminator[] disseminators = reader.GetDisseminators(null, null);

            for (int i = 0; i < disseminators.length; i++) {

                String bMech = disseminators[i].bMechID;
                DSBinding[] bindings = disseminators[i].dsBindMap.dsBindings;
                Set<BMechMethodInfo> infoSet = _provider.getMethodInfo(bMech);

                for (BMechMethodInfo method : infoSet) {
                    addMethodTriples(reader, 
                                     disseminators[i], 
                                     method,
                                     getInputDatastreams(method,
                                                         bindings,
                                                         allDatastreams),
                                     set);
                }
            }
        } catch (ResourceIndexException e) {
            throw (ResourceIndexException) e;
        } catch (Exception e) {
            throw new ResourceIndexException("Error adding dissem triples", e);
        }
    }

    /**
     * For the given method of the object, add the triples descriptive
     * of that method as a dissemination.
     */
    private void addMethodTriples(DOReader reader,
                                  Disseminator disseminator,
                                  BMechMethodInfo method,
                                  Set<Datastream> inputDatastreams,
                                  Set<Triple> set) 
            throws Exception {

        // get some identifiers ready for use
        String pidURI = PID.toURI(reader.GetObjectPID());
        URIReference objURI = createResource(pidURI);
        URIReference disseminationURI = createResource(pidURI + "/"
                + disseminator.bDefID + "/" + method.getName());

        // add triples whose values don't depend on datastream bindings
        add(objURI, VIEW.DISSEMINATES, disseminationURI, set);

        add(disseminationURI, 
            MODEL.STATE, 
            getStateResource(disseminator.dissState),
            set);

        for (String mimeType : method.getReturnTypes()) {
            add(disseminationURI, VIEW.MIME_TYPE, mimeType, set);
        }

        for (String permutation : method.getPermutations()) {
            URIReference dissType = createResource(FEDORA.uri + "*/"
                    + disseminator.bDefID + "/" + permutation);
            add(disseminationURI, VIEW.DISSEMINATION_TYPE, dissType, set);
        }

        // add triples whose values DO depend on datastream bindings
        addDSDependentMethodTriples(pidURI,
                                    disseminationURI,
                                    disseminator.dissCreateDT,
                                    inputDatastreams,
                                    set);

    }

    private void addDSDependentMethodTriples(String pidURI,
                                             URIReference disseminationURI,
                                             Date disseminatorModifiedDate,
                                             Set<Datastream> inputDatastreams,
                                             Set<Triple> set)
            throws Exception {

        Date lastModified = disseminatorModifiedDate;
        boolean isVolatile = false;

        for (Datastream ds : inputDatastreams) {

            URIReference dsURI = createResource(pidURI + "/" + ds.DatastreamID);
            add(disseminationURI, MODEL.DEPENDS_ON, dsURI, set);

            if (ds.DSControlGrp.equals("E") || ds.DSControlGrp.equals("R")) {
                isVolatile = true;
            }

            if (lastModified.before(ds.DSCreateDT)) {
                lastModified = ds.DSCreateDT;
            }

        }

        add(disseminationURI, VIEW.LAST_MODIFIED_DATE, lastModified, set);
        add(disseminationURI, VIEW.IS_VOLATILE, isVolatile, set);
    }

    private static Set getInputDatastreams(BMechMethodInfo method,
                                           DSBinding[] bindings,
                                           Datastream[] allDatastreams) {

        Set<Datastream> inputs = new HashSet<Datastream>();

        // TODO: implement matching logic here

        return inputs;
    }
                                           
}
