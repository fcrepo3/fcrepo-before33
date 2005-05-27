/*
 * Created on May 26, 2005
 *
 */
package fedora.test;

import java.io.File;
import java.io.FileOutputStream;

import junit.extensions.TestSetup;
import junit.framework.Test;
import fedora.server.config.ServerConfiguration;

/**
 * Writes the provided ServerConfiguration in the place of the original fcfg, 
 * restoring the original on tearDown();
 * @author Edwin Shin
 *
 */
public class FedoraConfigurationTestSetup extends TestSetup implements FedoraTestConstants {
    private ServerConfiguration fcfg;
    private static final String BACKUP = FCFG + "_bak";
    
    public FedoraConfigurationTestSetup(Test test, ServerConfiguration fcfg) {
        super(test);
        this.fcfg = fcfg;
    }
    
    public FedoraConfigurationTestSetup(Test test) {
        super(test);
    }
    
    protected void setUp() throws Exception {
        File original = new File(FCFG);
        original.renameTo(new File(BACKUP));
        FileOutputStream testCopy = new FileOutputStream(FCFG);
        fcfg.serialize(testCopy);
    }
    
    protected void tearDown() {
        File original = new File(BACKUP);
        original.renameTo(new File(FCFG));
    }
}
