package fedora.oai;

/**
 *
 * <p><b>Title:</b> DeletedRecordSupport.java</p>
 * <p><b>Description:</b> An indicator of the kind of deletion support a
 * repository has.</p>
 *
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002, 2003 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version 1.0
 */
public class DeletedRecordSupport {

    /**
     * Indicates that the repository does not maintain information about
     * deletions. A repository that indicates this level of support must not
     * reveal a deleted status in any response.
     */
    public static final DeletedRecordSupport NO=new DeletedRecordSupport("no");

    /**
     * Indicates that the repository does not guarantee that a list of
     * deletions is maintained persistently or consistently. A repository
     * that indicates this level of support may reveal a deleted status for
     * records.
     */
    public static final DeletedRecordSupport TRANSIENT=new DeletedRecordSupport("transient");

    /**
     * Indicates that the repository maintains information about deletions with
     * no time limit. A repository that indicates this level of support must
     * persistently keep track of the full history of deletions and consistently
     * reveal the status of a deleted record over time.
     */
    public static final DeletedRecordSupport PERSISTENT=new DeletedRecordSupport("persistent");

    private String m_stringValue;

    private DeletedRecordSupport(String stringValue) {
        m_stringValue=stringValue;
    }

    public String toString() {
        return m_stringValue;
    }

}