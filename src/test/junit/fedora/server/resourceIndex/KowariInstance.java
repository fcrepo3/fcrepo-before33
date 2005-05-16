package fedora.server.resourceIndex;

import java.io.File;
import java.net.URI;
import java.util.HashMap;

import org.kowari.itql.ItqlInterpreter;
import org.kowari.itql.ItqlInterpreterBean;
import org.kowari.query.Answer;
import org.kowari.server.Session;
import org.kowari.server.driver.SessionFactoryFinder;
import org.kowari.server.local.LocalSessionFactory;

/**
 * @author eddie
 */
public class KowariInstance {

	public static void main(String[] args) throws Exception {
		// tell commons-logging to use log4j
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");

        if (args.length > 0 && args[0].equalsIgnoreCase("true")) {
			new KowariInstance(true);
		} else {
			new KowariInstance();
		}
	}
	
	private final String MODEL_NAME = "resourceIndex";
	private final String SERVER_NAME = "fedora";
	private final String SERVER_DIR = "/tmp/kowari" + File.separator + SERVER_NAME;
	private final String RDF_INPUT = "file:/tmp/rdf/a.rdf";
	private URI XSD_MODEL_URI;
	
	private URI m_modelURI;
	private URI m_xsdModelURI;
	private Session m_session;
	private boolean m_load;
	
	private KowariInstance(boolean load) throws Exception {
		m_load = load;
		createSession();
		createModels();
		if (m_load) {
			load();
		}
		
		String queryString = "select $s $p $o from <" + m_modelURI.toString() + "> where $s $p $o;";
		ItqlInterpreter interpreter = new ItqlInterpreter(new HashMap());
		Answer ans = m_session.query(interpreter.parseQuery(queryString));
		System.out.println("Found " + ans.getRowCount() + " triples.");
		System.exit(1);
	}
	
	private KowariInstance() throws Exception {
		this(false);
	}
	
	private void createSession() throws Exception {
		URI serverURI = new URI("rmi", "localhost", "/" + SERVER_NAME, null);
		m_modelURI = new URI(serverURI.toString() + "#" + MODEL_NAME);
		m_xsdModelURI = new URI(serverURI.toString() + "#xsd");
		XSD_MODEL_URI = new URI("http://tucana.org/tucana#XMLSchemaModel");
		File serverDir = new File(SERVER_DIR);
		serverDir.mkdirs();
		LocalSessionFactory factory;
		factory = (LocalSessionFactory) SessionFactoryFinder
				.newSessionFactory(serverURI);
		if (factory.getDirectory() == null) {
			factory.setDirectory(serverDir);
		}
		m_session = factory.newSession();
	}
	
	private void createModels() throws Exception {
		if (!m_session.modelExists(m_modelURI)) {
			m_session.createModel(m_modelURI, Session.KOWARI_MODEL_URI);
		}
		if (!m_session.modelExists(m_xsdModelURI)) {
			m_session.createModel(m_xsdModelURI, XSD_MODEL_URI);
		}
	}
	
	private void load() throws Exception {
		ItqlInterpreterBean interpreter = new ItqlInterpreterBean(m_session,
			    null);
		String query = "load local <" + RDF_INPUT +"> into <" + m_modelURI + ">;";
		Answer answer = interpreter.executeQuery(query);
	}
}
