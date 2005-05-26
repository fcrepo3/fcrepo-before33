package fedora.test;

/**
 * @author Edwin Shin
 */
public class TestBar extends FedoraServerTestCase {

    /**
     * @param name
     */
    public TestBar(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestBar.class);
    }
    
    public void testBar() {
    	System.out.println("\n     * testBar() *\n");
	}

}
