package fedora.server.resourceIndex;

import java.util.Set;

import fedora.server.errors.ResourceIndexException;

import fedora.server.storage.BDefReader;
import fedora.server.storage.BMechReader;

/**
 * Stores and provides key information about known service method 
 * implementations.
 *
 * @author cwilper@cs.cornell.edu
 */
public interface MethodInfoStore extends MethodInfoProvider {

    void addBDef(BDefReader reader) throws ResourceIndexException;

    void addBMech(BMechReader reader) throws ResourceIndexException;

    void deleteBDef(String bDefPID) throws ResourceIndexException;

    void deleteBMech(String bMechPID) throws ResourceIndexException;

}