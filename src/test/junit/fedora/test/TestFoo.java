package fedora.test;

/**
 * @author Edwin Shin
 */
public class TestFoo extends FedoraServerTestCase {

    /**
     * @param name
     */
    public TestFoo(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFoo.class);
    }
    
    public void testFoo() { }
    public void testFooFoo() { }
}
