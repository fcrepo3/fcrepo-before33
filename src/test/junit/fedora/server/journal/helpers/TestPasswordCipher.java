package fedora.server.journal.helpers;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class TestPasswordCipher extends TestCase {
    private static final String CIPHER_TYPE_1 = "1";

    private static final String KEY_1 = "Now is the time for all";

    private static final String KEY_2 = "X";

    private static final String KEY_3 = "@lk<>2lkj-\u1234***";

    private static final String[] KEYS = new String[] { KEY_1, KEY_2, KEY_3 };

    private static final String TEXT_1 = "Silly boys!";

    private static final String TEXT_2 = "234!@#$@#$@%$^&*";

    private static final String TEXT_3 = "FRED\u6655";

    private static final String[] TEXTS = new String[] { TEXT_1, TEXT_2, TEXT_3 };

    public TestPasswordCipher(String name) {
        super(name);
    }

    public void testUniqueArgumentsOnEncipher() {
        try {
            PasswordCipher.encipher(null, TEXT_1);
            fail("Expected a NullPointerException");
        } catch (NullPointerException e) {
            // expected the exception
        }

        String cipher1 = PasswordCipher.encipher(KEY_1, null);
        assertNull(cipher1);

        String cipher2 = PasswordCipher.encipher("", TEXT_1);
        assertEquals(TEXT_1, cipher2);

    }

    /**
     * If the cipher text is null, the result will be null. If no key is
     * provided, or no cipher type is provided, the clear text will be the same
     * as the cipher text.
     */
    public void testUniqueArgumentsOnDecipher() {
        String clear1 = PasswordCipher.decipher(KEY_1, null, CIPHER_TYPE_1);
        assertNull(clear1);

        String clear2 = PasswordCipher.decipher(null, TEXT_1, CIPHER_TYPE_1);
        assertEquals(TEXT_1, clear2);

        String clear3 = PasswordCipher.decipher("", TEXT_2, CIPHER_TYPE_1);
        assertEquals(TEXT_2, clear3);

        String clear4 = PasswordCipher.decipher(KEY_2, TEXT_1, null);
        assertEquals(TEXT_1, clear4);

        String clear5 = PasswordCipher.decipher(KEY_2, TEXT_2, "");
        assertEquals(TEXT_2, clear5);
    }

    /**
     * The cipherText should not be the same as the clear text (unless the key
     * is empty).
     */
    public void testCipherDoesSomething() {
        for (int k = 0; k < KEYS.length; k++) {
            for (int t = 0; t < TEXTS.length; t++) {
                String cipher = PasswordCipher.encipher(KEYS[k], TEXTS[t]);
                assertFalse(TEXTS[t].equals(cipher));
            }
        }
    }

    /**
     * If I don't supply the type of cipher, deciphering has no effect.
     */
    public void testNullCipherDoesNothing() {
        for (int k = 0; k < KEYS.length; k++) {
            for (int t = 0; t < TEXTS.length; t++) {
                String deciphered = PasswordCipher.decipher(KEYS[k], TEXTS[t],
                        null);
                assertEquals(TEXTS[t], deciphered);
            }
        }
    }

    /**
     * If I encipher and then decipher with the same key, I should get my
     * original text back.
     */
    public void testCipherIsReversible() {
        for (int k = 0; k < KEYS.length; k++) {
            for (int t = 0; t < TEXTS.length; t++) {
                String cipher = PasswordCipher.encipher(KEYS[k], TEXTS[t]);
                String clear = PasswordCipher.decipher(KEYS[k], cipher,
                        CIPHER_TYPE_1);
                assertEquals(TEXTS[t], clear);
            }
        }
    }

    /**
     * If I pick a bunch of keys and texts at random, the ciphers should not
     * come out the same.
     */
    public void testCipherIsWellDistributed() {
        Set ciphers = new HashSet();

        for (int k = 0; k < KEYS.length; k++) {
            for (int t = 0; t < TEXTS.length; t++) {
                String cipher = PasswordCipher.encipher(KEYS[k], TEXTS[t]);
                assertTrue("Set of ciphers already contains '" + cipher + "'",
                        ciphers.add(cipher));
            }
        }
    }

}
