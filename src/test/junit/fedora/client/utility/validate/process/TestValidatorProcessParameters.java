/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client.utility.validate.process;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import fedora.server.errors.QueryParseException;
import fedora.server.search.Condition;
import fedora.server.search.FieldSearchQuery;

import static junit.framework.Assert.assertEquals;

import static fedora.client.utility.validate.process.ValidatorProcessParameters.PARAMETER_PASSWORD;
import static fedora.client.utility.validate.process.ValidatorProcessParameters.PARAMETER_QUERY;
import static fedora.client.utility.validate.process.ValidatorProcessParameters.PARAMETER_SERVER_URL;
import static fedora.client.utility.validate.process.ValidatorProcessParameters.PARAMETER_TERMS;
import static fedora.client.utility.validate.process.ValidatorProcessParameters.PARAMETER_USERNAME;

/**
 * @author Jim Blake
 */
public class TestValidatorProcessParameters {

    @Test
    public void simpleTermsSuccess() throws MalformedURLException {
        ValidatorProcessParameters parms =
                createParms(PARAMETER_USERNAME,
                            "username",
                            PARAMETER_PASSWORD,
                            "password",
                            PARAMETER_SERVER_URL,
                            "http://some.url/",
                            PARAMETER_TERMS,
                            "terms");
        assertEquals("username", "username", parms.getServiceInfo()
                .getUsername());
        assertEquals("password", "password", parms.getServiceInfo()
                .getPassword());
        assertEquals("serverurl", new URL("http://some.url/"), parms
                .getServiceInfo().getBaseUrl());
        FieldSearchQuery fsq = parms.getQuery();
        assertEquals("queryType", FieldSearchQuery.TERMS_TYPE, fsq.getType());
        assertEquals("terms", "terms", fsq.getTerms());
    }

    @Test
    public void simpleQuerySuccess() throws QueryParseException,
            MalformedURLException {
        ValidatorProcessParameters parms =
                createParms(PARAMETER_USERNAME,
                            "username",
                            PARAMETER_PASSWORD,
                            "password",
                            PARAMETER_SERVER_URL,
                            "http://some.url/",
                            PARAMETER_QUERY,
                            "pid=fred");
        assertEquals("username", "username", parms.getServiceInfo()
                .getUsername());
        assertEquals("password", "password", parms.getServiceInfo()
                .getPassword());
        assertEquals("serverurl", new URL("http://some.url/"), parms
                .getServiceInfo().getBaseUrl());
        FieldSearchQuery fsq = parms.getQuery();
        assertEquals("queryType", FieldSearchQuery.CONDITIONS_TYPE, fsq
                .getType());
        assertEquals("conditions", Condition.getConditions("pid=fred"), fsq
                .getConditions());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unrecognizedKeyword() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_TERMS,
                    "terms",
                    "-junk",
                    "junk");
    }

    @Test(expected = IllegalArgumentException.class)
    public void valueWithoutKeyword() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    "garbage",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_TERMS,
                    "terms");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noUsername() {
        createParms(PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_TERMS,
                    "terms");
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullUsername() {
        createParms(PARAMETER_USERNAME,
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_TERMS,
                    "terms");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noPassword() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_TERMS,
                    "terms");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noServerUrl() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_TERMS,
                    "terms");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidServerUrl() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "",
                    PARAMETER_TERMS,
                    "terms");
    }

    @Test(expected = IllegalArgumentException.class)
    public void noTermsOrQuery() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "http://some.url/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void bothTermsAndQuery() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_TERMS,
                    "terms",
                    PARAMETER_QUERY,
                    "pid=fred");
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidQueryString() {
        createParms(PARAMETER_USERNAME,
                    "username",
                    PARAMETER_PASSWORD,
                    "password",
                    PARAMETER_SERVER_URL,
                    "http://some.url/",
                    PARAMETER_QUERY,
                    "pid&fred");
    }

    private ValidatorProcessParameters createParms(String... args) {
        return new ValidatorProcessParameters(args);
    }
}
