package fedora.server.security.servletfilters;
import org.xml.sax.SAXException;

public class FinishedParsingException extends SAXException {
	
    public FinishedParsingException(String message) {
    	super(message);
    }

}
