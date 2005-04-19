package fedora.common.rdf;

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
