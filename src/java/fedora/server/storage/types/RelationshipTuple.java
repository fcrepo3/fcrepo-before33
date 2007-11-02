package fedora.server.storage.types;

/**
 *
 * <p><b>Title:</b> RelationshipTuple.java</p>
 * <p><b>Description:</b> A data structure for holding relationships 
 * consisting of predicate and subject. </p>
 *
 * @author rh9ec@virginia.edu
 * @version $Id: RelationshipTuple.java 3966 2005-04-21 13:33:01Z rlw $
 */
public class RelationshipTuple
{
    public String subjectURI;
    public String predicate;
    public String objectURI;
    public String objectLiteral;
    public String literalType;

    public RelationshipTuple() 
    {
    }
    
    public RelationshipTuple(String subjectURI, String predicate, String objectURI, String objectLiteral, String literalType) 
    {
        this.subjectURI = subjectURI;
        this.predicate = predicate;
        this.objectURI = objectURI;
        this.objectLiteral = objectLiteral;
        this.literalType = literalType;
    }
    
    public String getObjectPID()
    {
        if (objectURI != null && objectLiteral == null && objectURI.startsWith("info:fedora/"))
        {
            String PID = objectURI.substring(12); 
            return(PID);
        }
        return(null);
    }
    
    public String getSubjectPID()
    {
        if (subjectURI != null && subjectURI.startsWith("info:fedora/"))
        {
            String PID = subjectURI.substring(12); 
            return(PID);
        }
        return(null);
    }
    
    public String getRelationship()
    {
        if (predicate != null && predicate.startsWith("rel:"))
        {
            String PID = predicate.substring(4); 
            return(PID);
        }
        String prefix = "info:fedora/fedora-system:def/relations-external#";
        if (predicate != null && predicate.startsWith(prefix))
        {
            String PID = predicate.substring(prefix.length()); 
            return(PID);
        }

        return(predicate);
    }
    

    public String toString()
    {
        String retVal = "Sub: "+ subjectURI + "  Pred: " + predicate + "  Obj: ["+ objectURI + ", " + objectLiteral + ", " + literalType + "]";
        return(retVal);
    }
}
