package fedora.common.rdf;

/**
 * 
 * @author Edwin Shin
 * @see org.kowari.query.rdf.Tucana, 
 *      org.kowari.query.SpecialPredicates,
 *      org.kowari.resolver.xsd.XSDResolverFactory
 */
public class TucanaNamespace extends RDFNamespace {

    // Properties
    public final RDFName AFTER;
    public final RDFName BEFORE;
    public final RDFName GT;    
    public final RDFName LT;
    
    public final RDFName IS;
    public final RDFName NOT_OCCURS;
    public final RDFName OCCURS;
    public final RDFName OCCURS_LESS_THAN;
    public final RDFName OCCURS_MORE_THAN;

    public TucanaNamespace() {

        this.uri = "http://tucana.org/tucana#";

        // Properties
        this.AFTER              = new RDFName(this, "after");
        this.BEFORE             = new RDFName(this, "before");
        this.GT                 = new RDFName(this, "gt");
        this.LT                 = new RDFName(this, "lt");
        
        this.IS                 = new RDFName(this, "is");
        this.NOT_OCCURS         = new RDFName(this, "notOccurs");
        this.OCCURS             = new RDFName(this, "occurs");
        this.OCCURS_LESS_THAN   = new RDFName(this, "occursLessThan");
        this.OCCURS_MORE_THAN   = new RDFName(this, "occursMoreThan");
    }

}
