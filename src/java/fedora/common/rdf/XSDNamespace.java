package fedora.common.rdf;

public class XSDNamespace extends RDFNamespace {

    public final RDFName DATE_TIME;

    public XSDNamespace() {

        this.uri = "http://www.w3.org/2001/XMLSchema#";

        this.DATE_TIME = new RDFName(this, "dateTime");
    }

}
