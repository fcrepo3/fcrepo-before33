package fedora.common.rdf;

/**
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 */
public class DublinCoreNamespace extends RDFNamespace {

    public final RDFName TITLE;
    public final RDFName CREATOR;
    public final RDFName SUBJECT;
    public final RDFName DESCRIPTION;
    public final RDFName PUBLISHER;
    public final RDFName CONTRIBUTOR;
    public final RDFName DATE;
    public final RDFName TYPE;
    public final RDFName FORMAT;
    public final RDFName IDENTIFIER;
    public final RDFName SOURCE;
    public final RDFName LANGUAGE;
    public final RDFName RELATION;
    public final RDFName COVERAGE;
    public final RDFName RIGHTS;

    public DublinCoreNamespace() {

        this.uri = "http://purl.org/dc/elements/1.1/";

        this.TITLE        = new RDFName(this, "title");
        this.CREATOR      = new RDFName(this, "creator");
        this.SUBJECT      = new RDFName(this, "subject");
        this.DESCRIPTION  = new RDFName(this, "description");
        this.PUBLISHER    = new RDFName(this, "publisher");
        this.CONTRIBUTOR  = new RDFName(this, "contributor");
        this.DATE         = new RDFName(this, "date");
        this.TYPE         = new RDFName(this, "type");
        this.FORMAT       = new RDFName(this, "format");
        this.IDENTIFIER   = new RDFName(this, "identifier");
        this.SOURCE       = new RDFName(this, "source");
        this.LANGUAGE     = new RDFName(this, "language");
        this.RELATION     = new RDFName(this, "relation");
        this.COVERAGE     = new RDFName(this, "coverage");
        this.RIGHTS       = new RDFName(this, "rights");

    }

}
