package fedora.server.security;

import java.util.ArrayList;

import fedora.server.Context;
import fedora.server.errors.DisallowedHostException;
import fedora.server.errors.InvalidIPSpecException;

public class IPRestriction {

    private ArrayList m_allowRanges=new ArrayList();
    private ArrayList m_denyRanges=new ArrayList();
    private boolean m_allowSpecified=false;
    private boolean m_denySpecified=false;

    /**
     * Create an IPRestriction that allows hosts in the provided
     * range(s), and denies hosts in the provided range(s).
     * <p></p>
     * <b>allowHosts</b>
     * <dir>
     * A comma-separated list of IP ranges that the client's address 
     * is compared to. If this is specified, the remote address 
     * must match.  If this is not specified, all requests will 
     * be accepted unless the remote address matches a deny pattern.
     * </dir>
     * <b>denyHosts</b>
     * <dir>
     * A comma-separated list of IP ranges that the client's address 
     * is compared to. If this is specified, the remote address 
     * must not match.  If this is not specified, request 
     * acceptance is governed solely by the allowHosts value.
     * </dir>
     */
    public IPRestriction(String allowHosts, String denyHosts) 
            throws InvalidIPSpecException {
        if ( (allowHosts!=null) && (!allowHosts.equals("")) ) {
            m_allowSpecified=true;
            String[] aRanges=allowHosts.split(",");
            for (int i=0; i<aRanges.length; i++) {
                m_allowRanges.add(parseRange(aRanges[i]));
            }
        }
        if ( (denyHosts!=null) && (!denyHosts.equals("")) ) {
            m_denySpecified=true;
            String[] dRanges=denyHosts.split(",");
            for (int i=0; i<dRanges.length; i++) {
                m_denyRanges.add(parseRange(dRanges[i]));
            }
        }
    }

    public boolean allows(String address) 
            throws InvalidIPSpecException {
        long a=parseAddress(address);
        return allowed(a) && !denied(a);
    }
    
    public void enforce(Context context) 
            throws DisallowedHostException, InvalidIPSpecException {
        if (context.get("host")!=null && !allows(context.get("host"))) {
            throw new DisallowedHostException("Host " + context.get("host") 
                    + " is not allowed due to ip restriction.");
        }
    }
    
    private boolean allowed(long address) {
        if (m_allowSpecified) {
            for (int i=0; i<m_allowRanges.size(); i++) {
                if (((IPRange) m_allowRanges.get(i)).has(address)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }
    
    private boolean denied(long address) {
        if (m_denySpecified) {
            for (int i=0; i<m_denyRanges.size(); i++) {
                if (((IPRange) m_denyRanges.get(i)).has(address)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
    
    private IPRange parseRange(String range) 
            throws InvalidIPSpecException {
        long beginning;
        long ending;
        if (range.indexOf("-")==-1) {
            beginning=parseAddress(range);
            ending=beginning;
        } else {
            String[] r=range.split("-");
            if (r.length!=2) {
                throw new InvalidIPSpecException("Invalid IP range: '" + range + "'");
            }
            beginning=parseAddress(r[0]);
            ending=parseAddress(r[1]);
        }
        return new IPRange(beginning, ending);
    }
    
    private long parseAddress(String address) 
            throws InvalidIPSpecException {
        String[] parts=address.split("\\.");
        if (parts.length!=4) {
            throw new InvalidIPSpecException("Invalid IP address: '" + address + "'");
        }
        return parseOctet(parts[3], 0)
                + parseOctet(parts[2], 1)
                + parseOctet(parts[1], 2)
                + parseOctet(parts[0], 3);
    }

    // least-significant byte num is 0
    private long parseOctet(String octet, int byteNum)
            throws InvalidIPSpecException {
        try {
            long l=Long.parseLong(octet);
            if ( (l<0) || (l>255) ) {
                throw new InvalidIPSpecException("Invalid octet: '" + octet + "'");
            }
            if (byteNum==1) {
                return l*256;
            } else if (byteNum==2) {
                 return l*256*256;
            } else if (byteNum==3) {
                return l*256*256*256;
            } else {
                return l;
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidIPSpecException("Invalid octet: '" + octet + "'");
        }
    }

    protected class IPRange {

        private long m_beginning;
        private long m_ending;

        public IPRange(long beginning, long ending)
                throws InvalidIPSpecException {
            if ( (beginning<0) ) {
                throw new InvalidIPSpecException("IP range cannot be outside 0.0.0.0 and 255.255.255.255");
            }
            if (beginning>ending) {
                // swap if reversed order
                long oldEnding=ending;
                ending=beginning;
                beginning=oldEnding;
            }
            m_beginning=beginning;
            m_ending=ending;
        }

        public boolean has(long number) {
            return ((number>=m_beginning) && (number<=m_ending));
        }

    }

}