package melcoe.xacml.test;

import java.util.Map;
import java.util.Set;

import melcoe.xacml.pdp.finder.support.RelationshipResolver;
import melcoe.xacml.pdp.finder.support.RelationshipResolverHttpImpl;

public class TestRelationshipResolver
{
	public static void main(String[] args) throws Exception
	{
		RelationshipResolver rr = new RelationshipResolverHttpImpl("http://localhost:8080/fedora/melcoerisearch", "", "");
		Map<String, Set<String>> results = rr.getRelationships("ANU:1388");
		
		for (String s : results.keySet())
		{
			for (String t : results.get(s))
			{
				System.out.println(s + ": " + t);
			}
		}
	}
}
