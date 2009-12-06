/*
 * File: RelationshipResolverImpl.java
 *
 * Copyright 2007 Macquarie E-Learning Centre Of Excellence
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package melcoe.xacml.pdp.finder.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import melcoe.xacml.pdp.finder.AttributeFinderException;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.jrdf.graph.Triple;
import org.trippi.RDFFormat;
import org.trippi.TripleIterator;
import org.trippi.TrippiException;

/**
 * @author nishen@melcoe.mq.edu.au
 */

public class RelationshipResolverHttpImpl implements RelationshipResolver {
	private static Logger log = Logger
			.getLogger(RelationshipResolverHttpImpl.class.getName());

	private URL baseURL = null;
	private String username = null;
	private String password = null;

	public RelationshipResolverHttpImpl(String url, String username,
			String password) throws AttributeFinderException {
		try {
			if (log.isDebugEnabled())
				log.debug("Resolver URL: " + url);
			this.baseURL = new URL(url);
		} catch (MalformedURLException mue) {
			throw new AttributeFinderException(
					"Resolver URL is not a valid URL", mue);
		}
		this.username = username;
		this.password = password;
	}

	/**
	 * Get an HTTP resource with the response as an InputStream, given a URL.
	 * 
	 * Note that if the HTTP response has no body, the InputStream will be
	 * empty. The success of a request can be checked with getResponseCode().
	 * Usually you'll want to see a 200. See
	 * http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html for other codes.
	 * 
	 * @param url
	 *            A URL that we want to do an HTTP GET upon
	 * @param failIfNotOK
	 *            boolean value indicating if an exception should be thrown if
	 *            we do NOT receive an HTTP 200 response (OK)
	 * @param followRedirects
	 *            boolean value indicating whether HTTP redirects should be
	 *            handled in this method, or be passed along so that they can be
	 *            handled later.
	 * @return HttpInputStream the HTTP response
	 * @throws IOException
	 */
	private InputStream get(URL url, boolean failIfNotOK,
			boolean followRedirects) throws IOException {
		String urlString = url.toString();
		log.debug("FedoraClient is getting " + urlString);

		HttpClient client = new HttpClient();
		Credentials credentials = new UsernamePasswordCredentials(username,
				password);
		client.getParams().setAuthenticationPreemptive(true);
		client.getState().setCredentials(
				new AuthScope(baseURL.getHost(), baseURL.getPort(),
						AuthScope.ANY_REALM), credentials);
		GetMethod getMethod = new GetMethod(urlString);
		getMethod.setDoAuthentication(true);
		getMethod.setFollowRedirects(followRedirects);

		int status = client.executeMethod(getMethod);

		if (failIfNotOK) {
			if (status != 200) {
				// if (followRedirects && in.getStatusCode() == 302){
				if (followRedirects && ((300 <= status) && (status <= 399))) {
					// Handle the redirect here !
					log
							.debug("FedoraClient is handling redirect for HTTP STATUS="
									+ status);

					Header hLoc = getMethod.getResponseHeader("location");
					if (hLoc != null) {
						log.debug("FedoraClient is trying redirect location: "
								+ hLoc.getValue());
						return get(new URL(hLoc.getValue()), true, false);
					}
				}

				throw new IOException("Request failed ["
						+ getMethod.getStatusCode() + " "
						+ getMethod.getStatusText() + "]");
			}
		}

		return getMethod.getResponseBodyAsStream();
	}

	private TripleIterator getTriples(String query) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("type", "triples");
		params.put("format", RDFFormat.N_TRIPLES.getName());
		params.put("lang", "spo");
		params.put("query", query);

		try {
			String url = getRIQueryURL(params);

			return TripleIterator.fromStream(get(new URL(url), true, true),
					RDFFormat.N_TRIPLES);
		} catch (TrippiException e) {
			throw new IOException("Error getting triple iterator: "
					+ e.getMessage());
		}
	}

	private String getRIQueryURL(Map params) throws IOException {
		if (params.get("type") == null) {
			throw new IOException("'type' parameter is required");
		}

		if (params.get("lang") == null) {
			throw new IOException("'lang' parameter is required");
		}

		if (params.get("query") == null) {
			throw new IOException("'query' parameter is required");
		}

		if (params.get("format") == null) {
			throw new IOException("'format' parameter is required");
		}

		return baseURL.toString() + "?" + encodeParameters(params);
	}

	private String encodeParameters(Map params) {
		StringBuffer encoded = new StringBuffer();
		Iterator iter = params.keySet().iterator();
		int n = 0;

		while (iter.hasNext()) {
			String name = (String) iter.next();

			if (n > 0) {
				encoded.append("&");
			}

			n++;
			encoded.append(name);
			encoded.append('=');

			try {
				encoded.append(URLEncoder.encode((String) params.get(name),
						"UTF-8"));
			} catch (UnsupportedEncodingException e) { // UTF-8 won't fail
			}
		}

		return encoded.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see melcoe.fedora.pep.RelationshipResolver#getParents(java.lang.String)
	 */
	public Set<String> getParents(String pid) throws AttributeFinderException {
		String pidDN = "<info:fedora/" + pid + ">";
		String relationship = "<info:fedora/fedora-system:def/relations-external#isMemberOf>";
		TripleIterator i = null;
		try {
			String query = pidDN + " " + relationship + " *";
			if (log.isDebugEnabled())
				log.debug("triplestore query: " + query);
			i = getTriples(query);
		} catch (IOException ioe) {
			throw new AttributeFinderException("Error obraining parents.", ioe);
		}

		Set<String> parents = new HashSet<String>();
		try {
			while (i.hasNext()) {
				Triple t = i.next();
				String parentPid = t.getObject().toString();
				parents.add(parentPid.substring(12));
			}
		} catch (TrippiException te) {
			throw new AttributeFinderException("Error retrieving triple.", te);
		}

		return parents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * melcoe.fedora.util.RelationshipResolver#buildRESTParentHierarchy(java
	 * .lang.String)
	 */
	public String buildRESTParentHierarchy(String pid)
			throws AttributeFinderException {
		Set<String> parents = getParents(pid);
		if (parents == null || parents.size() == 0)
			return "/" + pid;

		String[] parentArray = parents.toArray(new String[parents.size()]);

		return buildRESTParentHierarchy(parentArray[0]) + "/" + pid;
	}

	public Map<String, Set<String>> getRelationships(String pid)
			throws AttributeFinderException {
		String pidDN = pid;
		if (pid.startsWith("/")) {
			String[] parts = pid.split("\\/");
			if (parts.length > 2) {
				if (parts[parts.length - 1].indexOf(':') != -1) {
					// is an object, not a datastream
					pidDN = parts[parts.length - 1];
				} else {
					// is a datastream
					pidDN = parts[parts.length - 2] + "/"
							+ parts[parts.length - 1];
				}
			}

			if (pidDN.startsWith("/"))
				pidDN = pidDN.substring(1);
		}

		TripleIterator i = null;
		try {
			String query = "<info:fedora/" + pidDN + "> * *";
			if (log.isDebugEnabled())
				log.debug("triplestore query: " + query);
			i = getTriples(query);
		} catch (IOException ioe) {
			throw new AttributeFinderException(
					"Error obraining relationships.", ioe);
		}

		Map<String, Set<String>> relationships = new HashMap<String, Set<String>>();
		try {
			while (i.hasNext()) {
				Triple t = i.next();
				String p = t.getPredicate().toString();
				String o = t.getObject().toString();

				Set<String> values = relationships.get(p);
				if (values == null) {
					values = new HashSet<String>();
					relationships.put(p, values);
				}
				values.add(o);
			}
		} catch (TrippiException te) {
			throw new AttributeFinderException("Error retrieving triple.", te);
		}

		return relationships;
	}
}
