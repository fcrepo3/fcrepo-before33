package fedora.client.utility.ingest;

/**
 * <p><b>Title:</b> IngestCounter.java</p>
 * <p><b>Description: Class to hold counts of ingest failures and successes.
 * This class will be passed as a by reference argument to methods of the Ingest
 * utility class.
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 */
public class IngestCounter {    
    public int successes;
    public int failures;
    
    public IngestCounter(){
    	successes=0;
    	failures=0;
    }
}