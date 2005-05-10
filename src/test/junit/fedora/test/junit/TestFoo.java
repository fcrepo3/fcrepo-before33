package fedora.test.junit;

/**
 * @author Edwin Shin
 */
public class TestFoo extends FedoraServerTestCase {

    public static void main(String[] args) {
        junit.textui.TestRunner.run(TestFoo.class);
    }
    
    public void testFoo() { }
    public void testFooFoo() { }
}
