/**
 * <p>Title: PIDGenerator.java</p>
 * <p>Description: Interface for generating Fedora PIDs</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author Paul Charlton
 * @version 1.0
 */

import java.io.*;

interface PIDGenerator {
        public String generatePID(String NamespaceID) throws IOException;
        public String getLastPID() throws IOException;
}
