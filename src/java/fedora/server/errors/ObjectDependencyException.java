package fedora.server.errors;

/**
 * <p><b>Title: </b>ObjectDependencyException.java</p>
 * <p><b>Description: </b>Signals that an object has one or more related objects that
 * depend on it. For example, a behavior definition or behavior mechanism
 * object can be shared by multiple objects. Any data objects that use a
 * specific behavior definition or behavior mechanism object "depend" on
 * those objects. To remove a dependent object, you must first remove all
 * related objects.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author rlw@virginia.edu
 * @version $Id$
 */
public class ObjectDependencyException
        extends StorageException {

    /**
     * Creates an ObjectDependencyException.
     *
     * @param message An informative message explaining what happened and
     *                (possibly) how to fix it.
     */
    public ObjectDependencyException(String message) {
        super(message);
    }

}