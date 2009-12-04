package melcoe.test;

import java.util.HashMap;
import java.util.Map;

import melcoe.fedora.util.RelationshipResolverHttpImpl;

public class TestRelationshipResolverHttpImpl
{
	private RelationshipResolverHttpImpl rels = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		TestRelationshipResolverHttpImpl app = null;
		try
		{
			app = new TestRelationshipResolverHttpImpl();
			app.test01();
		}
		catch(Exception e)
		{
			System.err.println(e.getMessage());
		}
	}
	
	public TestRelationshipResolverHttpImpl() throws Exception
	{
		Map<String, String> options = new HashMap<String, String>();
		options.put("url", "http://localhost:8080/fedora/melcoerisearch");
		options.put("username", "");
		options.put("password", "");
		rels = new RelationshipResolverHttpImpl(options);
	}
	
	public void test01() throws Exception
	{
		String result = rels.buildRESTParentHierarchy("changeme:8848");
		System.out.println(result);
	}
}
