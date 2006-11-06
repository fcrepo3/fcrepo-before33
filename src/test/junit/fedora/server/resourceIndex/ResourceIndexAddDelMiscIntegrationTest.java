package fedora.server.resourceIndex;

import java.util.Collections;
import java.util.Set;

import org.jrdf.graph.Triple;

import fedora.server.storage.types.DigitalObject;

/**
 * Miscellaneous tests of adding and deleting objects from the RI.
 *
 * Note: All tests run at RI level 1 unless otherwise noted.
 *
 * @author cwilper@cs.cornell.edu
 */
public class ResourceIndexAddDelMiscIntegrationTest
        extends ResourceIndexIntegrationTest {

    /**
     * Construct the test.
     */
    public ResourceIndexAddDelMiscIntegrationTest(String name) {
        super(name);
    }

    /**
     * Add, then delete an object with the RI at level 0.
     */
    public void testAddDelObjLv0()
            throws Exception {
        Set<DigitalObject> objects = getTestObjects(1, 0);

        // add at level 0
        initRI(0);
        addAll(objects, true);

        assertEquals("Did not get expected triples after add",
                     Collections.EMPTY_SET,
                     getActualTriples());

        // add at level 1
        initRI(1);
        addAll(objects, true);
        Set<Triple> expected = getExpectedTriples(1, objects);

        // delete at level 0
        initRI(0);
        deleteAll(objects, true);

        assertTrue("Did not get expected triples after delete",
                   sameTriples(expected, getActualTriples(), true));
    }

    /**
     * Add, then delete several objects, each with one datastream.
     */
    public void testAddDelMultiObjOneDS()
            throws Exception {
        Set<DigitalObject> objects = getTestObjects(10, 1);
        doAddDelTest(1, objects);
    }

    /**
     * Add, then delete several objects, each with several datastreams.
     */
    public void testAddDelMultiObjMultiDS()
            throws Exception {
        Set<DigitalObject> objects = getTestObjects(10, 10);
        doAddDelTest(1, objects);
    }

}