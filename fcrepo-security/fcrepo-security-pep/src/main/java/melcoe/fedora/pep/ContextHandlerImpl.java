/*
 * File: ContextHandlerImpl.java
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

package melcoe.fedora.pep;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import melcoe.fedora.util.ContextUtil;
import melcoe.fedora.util.RelationshipResolver;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;

import fedora.common.Constants;

/**
 * @author nishen@melcoe.mq.edu.au
 * @see ContextHandler
 */
public class ContextHandlerImpl implements ContextHandler {
	private static Logger log = Logger.getLogger(ContextHandlerImpl.class
			.getName());

	private ContextUtil contextUtil = null;
	private static ContextHandler contextHandler = null;
	private PDPClient client = null;
	private EvaluationEngine evaluationEngine = null;
	private ResponseCache responseCache = null;

	/**
	 * The default constructor that initialises a new ContextHandler instance.
	 * This is a private constructor as this is a singleton class.
	 * 
	 * @throws PEPException
	 */
	private ContextHandlerImpl() throws PEPException {
		super();
		init();
	}

	/**
	 * @return an instance of a ContextHandler
	 * @throws PEPException
	 */
	public static ContextHandler getInstance() throws PEPException {
		if (contextHandler == null)
			try {
				contextHandler = new ContextHandlerImpl();
			} catch (Exception e) {
				log.error("Could not initialise ContextHandler.");
				throw new PEPException("Could not initialise ContextHandler.",
						e);
			}

		return contextHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see melcoe.fedora.pep.ContextHandler#buildRequest(java.util.List,
	 * java.util.Map, java.util.Map, java.util.Map)
	 */
	public RequestCtx buildRequest(
			List<Map<URI, List<AttributeValue>>> subjects,
			Map<URI, AttributeValue> actions,
			Map<URI, AttributeValue> resources,
			Map<URI, AttributeValue> environment) throws PEPException {
		return contextUtil.buildRequest(subjects, actions, resources,
				environment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * melcoe.fedora.pep.ContextHandler#evaluate(com.sun.xacml.ctx.RequestCtx)
	 */
	public ResponseCtx evaluate(RequestCtx reqCtx) throws PEPException {
		return evaluationEngine.evaluate(reqCtx);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see melcoe.fedora.pep.ContextHandler#evaluate(java.lang.String)
	 */
	public String evaluate(String request) throws PEPException {
		return evaluationEngine.evaluate(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see melcoe.fedora.pep.ContextHandler#evaluateBatch(java.lang.String[])
	 */
	public String evaluateBatch(String[] requests) throws PEPException {
		return evaluationEngine.evaluate(requests);
	}

	public ResponseCache getResponseCache()
	{
		return responseCache;
	}

	/**
	 * Reads a configuration file and configures this instance of the
	 * ContextHandler. It can instantiate a client (that communicates with the
	 * PEP), a relationship resolver (that communicates with the risearch REST
	 * service to determine parental relationships) and a response cache (that
	 * caches requests/responses for quicker evaluations).
	 * 
	 * @throws PEPException
	 */
	private void init() throws PEPException {
		try {
			// get the log configuration
			URL filenameURL = new File(Constants.FEDORA_HOME,
					"server/config/log4j.properties").toURL();
			if (filenameURL != null) {
				PropertyConfigurator.configure(filenameURL);
				log.info("Logging configured using: " + filenameURL);
			} else {
				BasicConfigurator.configure();
				log.info("Logging configured using default configuration.");
			}

			// get the PEP configuration
			InputStream is = this.getClass().getClassLoader()
					.getResourceAsStream("config-melcoe-pep.xml");
			if (is == null)
				throw new PEPException(
						"Could not locate config file: config-melcoe-pep.xml");

			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = factory.newDocumentBuilder();
			Document doc = docBuilder.parse(is);
			NodeList nodes = null;

			if (log.isDebugEnabled())
				log.debug("Obtained the config file: config-melcoe-pep.xml");

			String className = null;
			Constructor<?> c = null;

			Map<String, String> options = new HashMap<String, String>();

			// get the PDP Client
			nodes = doc.getElementsByTagName("pdp-client");
			if (nodes.getLength() != 1)
				throw new PEPException(
						"Config file needs to contain exactly 1 'pdp-client' section.");

			className = nodes.item(0).getAttributes().getNamedItem("class")
					.getNodeValue();
			NodeList optionNodes = nodes.item(0).getChildNodes();
			for (int x = 0; x < optionNodes.getLength(); x++) {
				Node n = optionNodes.item(x);
				if (optionNodes.item(x).getNodeType() == Node.ELEMENT_NODE) {
					log.debug("Node [name]: "
							+ n.getAttributes().getNamedItem("name")
									.getNodeValue());
					String key = n.getAttributes().getNamedItem("name")
							.getNodeValue();
					String value = n.getFirstChild().getNodeValue();
					options.put(key, value);
				}
			}

			c = Class.forName(className).getConstructor(
					new Class[] { Map.class });
			client = (PDPClient) c.newInstance(new Object[] { options });

			if (log.isDebugEnabled())
				log.debug("Instantiated PDPClient: " + className);

			// get the Response Cache
			nodes = doc.getElementsByTagName("response-cache");
			if (nodes.getLength() != 1)
				throw new PEPException(
						"Config file needs to contain exactly 1 'response-cache' section.");

			className = nodes.item(0).getAttributes().getNamedItem("class")
					.getNodeValue();
			if ("true".equals(nodes.item(0).getAttributes().getNamedItem(
					"active").getNodeValue())) {
				int cacheSize = 1000; // default
				long cacheTTL = 10000; // default
				NodeList children = nodes.item(0).getChildNodes();
				for (int x = 0; x < children.getLength(); x++) {
					if (children.item(x).getNodeType() == Node.ELEMENT_NODE) {
						if ("cache-size".equals(children.item(x).getNodeName()))
							cacheSize = Integer.parseInt(children.item(x)
									.getFirstChild().getNodeValue());

						if ("cache-item-ttl".equals(children.item(x)
								.getNodeName()))
							cacheTTL = Long.parseLong(children.item(x)
									.getFirstChild().getNodeValue());
					}
				}

				c = Class.forName(className).getConstructor(
						new Class[] { Integer.class, Long.class });
				responseCache = (ResponseCache) c.newInstance(new Object[] {
						new Integer(cacheSize), new Long(cacheTTL) });

				if (log.isDebugEnabled())
					log.debug("Instantiated ResponseCache: " + className);
			}

			// Get the evaluation engine
			nodes = doc.getElementsByTagName("evaluation-engine");
			if (nodes.getLength() != 1)
				throw new PEPException(
						"Config file needs to contain exactly 1 'evaluation-engine' section.");

			className = nodes.item(0).getAttributes().getNamedItem("class")
					.getNodeValue();
			evaluationEngine = (EvaluationEngine) Class.forName(className)
					.newInstance();
			evaluationEngine.setClient(client);
			evaluationEngine.setResponseCache(responseCache);

			if (log.isDebugEnabled())
				log.debug("Instantiated EvaluationEngine: " + className);

			// Get the relationship resolver
			RelationshipResolver relationshipResolver = null;
			nodes = doc.getElementsByTagName("relationship-resolver");
			if (nodes.getLength() != 1)
				throw new PEPException(
						"Config file needs to contain exactly 1 'relationship-resolver' section.");

			Element relationshipResolverElement = (Element) nodes.item(0);
			className = relationshipResolverElement.getAttributes()
					.getNamedItem("class").getNodeValue();

			NodeList optionList = relationshipResolverElement
					.getElementsByTagName("option");
			if (optionList == null || optionList.getLength() == 0) {
				if (log.isDebugEnabled())
					log.debug("creating relationship resolver WITHOUT options");

				relationshipResolver = (RelationshipResolver) Class.forName(
						className).newInstance();
			} else {
				if (log.isDebugEnabled())
					log.debug("creating relationship resolver WITH options");

				options = new HashMap<String, String>();
				for (int x = 0; x < optionList.getLength(); x++) {
					Node n = optionList.item(x);
					String key = n.getAttributes().getNamedItem("name")
							.getNodeValue();
					String value = n.getFirstChild().getNodeValue();
					options.put(key, value);
					if (log.isDebugEnabled())
						log.debug("Node [name]: " + key + ": " + value);
				}
				c = Class.forName(className).getConstructor(
						new Class[] { Map.class });
				relationshipResolver = (RelationshipResolver) c
						.newInstance(new Object[] { options });
			}
			if (log.isDebugEnabled())
				log.debug("Instantiated RelationshipResolver: " + className);

			contextUtil = new ContextUtil(relationshipResolver);

			if (log.isDebugEnabled())
				log.debug("Instantiated ContextUtil.");
		} catch (Exception e) {
			log.fatal("Failed to initialse the PEP ContextHandler");
			log.fatal(e.getMessage(), e);
			throw new PEPException(e.getMessage(), e);
		}
	}
}
