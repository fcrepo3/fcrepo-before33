package fedora.server.resourceIndex;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.trippi.*;

/**
 * @author eddie
 */
public class TrippiInstance {

	public static void main(String[] args) throws Exception {
		// tell commons-logging to use log4j
        System.setProperty("org.apache.commons.logging.LogFactory", "org.apache.commons.logging.impl.Log4jFactory");
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.Log4JLogger");
        
        if (args.length > 0 && args[0].equalsIgnoreCase("true")) {
        	new TrippiInstance(true);
        } else {
        	new TrippiInstance(false);
        }
	}
	
	private final String MODEL_NAME = "resourceIndex";
	private final String SERVER_NAME = "fedora";
	private final String SERVER_DIR = "d:\\tmp\\kowari" + File.separator + SERVER_NAME;
	private final String RDF_INPUT = "d:/tmp/kowari/a.rdf";
	private URI XSD_MODEL_URI;
	
	private TriplestoreConnector m_conn;
	private TriplestoreWriter m_writer;
	private Map m_cfg;
	private boolean m_load;
	
	private TrippiInstance(boolean load) throws Exception {
		m_load = load;
		
		m_cfg = new HashMap();
		m_cfg.put("remote", "false");
		m_cfg.put("path", "D:\\tmp\\kowari\\fedora");
		m_cfg.put("serverName", "fedora");
		m_cfg.put("modelName", "resourceIndex");
		m_cfg.put("poolInitialSize", "3");
		m_cfg.put("poolMaxGrowth", "-1");
		m_cfg.put("readOnly", "false");
		m_cfg.put("autoCreate", "true");
		m_cfg.put("autoTextIndex", "false");
		m_cfg.put("memoryBuffer", "true");
		m_cfg.put("autoFlushDormantSeconds", "5");
		m_cfg.put("autoFlushBufferSize", "20000");
		m_cfg.put("bufferFlushBatchSize", "20000");
		m_cfg.put("bufferSafeCapacity", "40000");
		
		m_conn = TriplestoreConnector.init("org.trippi.impl.kowari.KowariConnector", m_cfg);
		m_writer = m_conn.getWriter();
		
		if (m_load) {
			System.out.println("Loading RDF/XML...");
			m_writer.add(TripleIterator.fromStream(new FileInputStream(RDF_INPUT), RDFFormat.RDF_XML), true);
			System.out.println("Loaded RDF/XML..");
		}
		
		TripleIterator t = m_writer.findTriples(null, null, null, 0);
		System.out.println("Found " + t.count() + " triples.");
		
		System.exit(1);
	}
}
