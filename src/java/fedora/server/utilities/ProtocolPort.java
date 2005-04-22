/*
 * Created on Apr 20, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package fedora.server.utilities;

import fedora.server.errors.GeneralException;

/**
 * @author wdn5e
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class ProtocolPort {
	private String protocol;
	private String port;
	
	public ProtocolPort (String protocol, String port) throws GeneralException {
		if ((! ServerUtility.HTTP.equals(protocol)) && (! ServerUtility.HTTPS.equals(protocol))) {
			throw new GeneralException("bad protocol in ProtocolPort constructor");
		}
		if ((port == null) || "".equals(port)) {
			throw new GeneralException("bad port in ProtocolPort constructor");
		}
		this.protocol = protocol;
		this.port = port;
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String getPort() {
		return port;
	}

	public static void main(String[] args) {
	}
}
