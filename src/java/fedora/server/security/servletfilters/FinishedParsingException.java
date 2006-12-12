package fedora.server.security.servletfilters;
import org.xml.sax.SAXException;

public class FinishedParsingException extends SAXException {
	private static final long serialVersionUID = 1L;
	
    public FinishedParsingException(String message) {
    	super(message);
    }

}
