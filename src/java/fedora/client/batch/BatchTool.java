import java.io.FileOutputStream;
import java.io.PrintStream;
import com.icl.saxon.StyleSheet;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.util.Properties;
import java.io.FileInputStream;

class BatchTool {
	private final Properties miscProperties;
	private final Properties datastreamProperties;
	private final Properties metadataProperties;	
	private final Properties batchAdditionsValues;
	private final Properties batchXformsValues;
	private final Properties batchIngestValues;	
	
	BatchTool(Properties miscProperties, Properties datastreamProperties, Properties metadataProperties) throws Exception {
		this.miscProperties = miscProperties;
		this.metadataProperties = metadataProperties;
		this.datastreamProperties = datastreamProperties;
		
		batchAdditionsValues = (Properties) miscProperties.clone();
		batchXformsValues = (Properties) miscProperties.clone();
		batchIngestValues = (Properties) miscProperties.clone();
	}
	
 	private boolean good2go = false;
	
	final void prep() {
		good2go = true;
	}
	
	final void process() throws Exception {
		if (good2go) {
			BatchAdditions batchAdditions = null;
			BatchXforms batchXforms = null;
			BatchIngest batchIngest = null;
			
			//make each phase		
			
			if ( (miscProperties.getProperty(AGGREGATE) != null) && miscProperties.getProperty(AGGREGATE).equals("yes") ) {
				batchAdditions = new BatchAdditions(batchAdditionsValues, datastreamProperties, metadataProperties);
			}
			if ( (miscProperties.getProperty(DISCRETE) != null) && miscProperties.getProperty(DISCRETE).equals("yes") ) {
				batchXforms = new BatchXforms(batchXformsValues);				
			}
			if ( (miscProperties.getProperty(EAT) != null) && miscProperties.getProperty(EAT).equals("yes") ) {
				batchIngest = new BatchIngest(batchIngestValues);				
			}
						
			//check in with each phase
			if ( (miscProperties.getProperty(AGGREGATE) != null) && miscProperties.getProperty(AGGREGATE).equals("yes") ) {
				batchAdditions.prep();
			}
			if ( (miscProperties.getProperty(DISCRETE) != null) && miscProperties.getProperty(DISCRETE).equals("yes") ) {
				batchXforms.prep();
			}
			if ( (miscProperties.getProperty(EAT) != null) && miscProperties.getProperty(EAT).equals("yes") ) {
				batchIngest.prep();
			}
			
			//perform each phase
			if ( (miscProperties.getProperty(AGGREGATE) != null) && miscProperties.getProperty(AGGREGATE).equals("yes") ) {
				batchAdditions.process();
			}
			if ( (miscProperties.getProperty(DISCRETE) != null) && miscProperties.getProperty(DISCRETE).equals("yes") ) {
				batchXforms.process();
			}
			if ( (miscProperties.getProperty(EAT) != null) && miscProperties.getProperty(EAT).equals("yes") ) {
				batchIngest.process();				
			}
		}
	}

	static final String STARTOBJECT = "initial-pid";
	static final String KEYPATH = "key-path";
	static final String METADATAPATH = "metadata";
	static final String URLPATH = "url";
	static final String DATAPATH = "data";
	static final String STRINGPREFIX = "url-prefix";
	static final String DECLARATIONS = "namespace-declarations";		

	private static final String AGGREGATE = "process-tree";

	static final String ADDITIONSPATH = "specifics";

	static final String XFORMPATH = "xform";
	static final String CMODEL = "template";
	private static final String DISCRETE = "merge-objects";

	static final String OBJECTSPATH = "objects";

	static final String SERVERFQDN = "server-fqdn";
	static final String SERVERPORT = "server-port";	
	private static final String EAT = "ingest";
	static final String PIDSPATH = "ingested-pids";

	static final boolean argOK(String value) {
		return (value != null) && ! value.equals("");
	}	

	public static final void main(String[] args) throws Exception {
		Properties miscProperties = new Properties();
		Properties datastreamProperties = new Properties();
		Properties metadataProperties = new Properties();
		
		Getopt getopt = new Getopt("thispgm",args,"g:d:m:");
			
		int c;
		while ((c = getopt.getopt()) != -1) {
			switch (c) {
				case 'g':
					String temp = getopt.getOptarg();
					miscProperties.load(new FileInputStream(temp)); //"c:\\batchdemo\\batchtool.properties"));
					break;
				case 'd':
					datastreamProperties.load(new FileInputStream(getopt.getOptarg())); //"c:\\batchdemo\\batchtool.properties"));
					break;
				case 'm':
					metadataProperties.load(new FileInputStream(getopt.getOptarg())); //"c:\\batchdemo\\batchtool.properties"));
					break;
			}			
		}
		
		BatchTool batchTool = new BatchTool(miscProperties,datastreamProperties,metadataProperties);
		batchTool.prep();
		batchTool.process();
	}
	
	
	
	
}

