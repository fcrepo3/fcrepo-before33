package fedora.server.utilities;

import java.util.List;

/**
 *
 * <p><b>Title:</b> DDLConverter.java</p>
 * <p><b>Description:</b> Interface for a converter of TableSpec objects to
 * RDBMS-specific DDL code.</p>
 *
 * <p>Implementations of this class must be thread-safe.  That is, one
 * instance can be used simultanously without problems.  This typically
 * just means that no varying fields should be used.</p>
 *
 * <p>Implementations must also have a public no-arg constructor.</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public interface DDLConverter {

    public abstract boolean supportsTableType();

    public abstract List getDDL(TableSpec tableSpec);

}

