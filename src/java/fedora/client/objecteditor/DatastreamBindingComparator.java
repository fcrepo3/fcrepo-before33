package fedora.client.objecteditor;

import java.util.*;

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