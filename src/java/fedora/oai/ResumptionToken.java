package fedora.oai;

import java.util.Date;

/**
 *
 * <p><b>Title:</b> ResumptionToken.java</p>
 * <p><b>Description:</b> A token that can be used to retrieve the remaining
 * portion of an incomplete list response.</p>
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
 * <p>The entire file consists of original code.  Copyright © 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 * @see <a href="http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl">
 *      http://www.openarchives.org/OAI/openarchivesprotocol.html#FlowControl</a>
 */
public interface ResumptionToken {

    /**
     * Get the value of the token.
     *
     * A null value indicates that the associated list is complete.
     */
    public abstract String getValue();

    /**
     * Get the expiration date of the token.
     *
     * A null value indicates an unknown or unprovided expiration date.
     */
    public abstract Date getExpirationDate();

    /**
     * Get the size of the list.
     *
     * A negative value indicates an unknown or unprovided list size.
     */
    public abstract long getCompleteListSize();

    /**
     * Get the position in the list that this record starts at.
     *
     * A negative value indicates an unknown or unprovided position.
     */
    public abstract long getCursor();

}