package fedora.client.objecteditor;

import java.util.*;

/**
 * Compares binding sequences for sorting.
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */
import fedora.server.types.gen.DatastreamBinding;

    public class DatastreamBindingComparator
            implements Comparator {

        public int compare(Object o1, Object o2) {
            int seqNo1=getInt(((DatastreamBinding) o1).getSeqNo());
            int seqNo2=getInt(((DatastreamBinding) o2).getSeqNo());
            return seqNo1-seqNo2; // negative if lt, 0 if equal, positive if gt
        }

        private int getInt(String number) {
            try {
                return Integer.parseInt(number);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }