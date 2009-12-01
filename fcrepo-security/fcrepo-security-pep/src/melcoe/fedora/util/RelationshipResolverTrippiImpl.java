/*
 * File: RelationshipResolverTrippiImpl.java
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

package melcoe.fedora.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import melcoe.fedora.pep.PEPException;

import org.apache.log4j.Logger;
import org.jrdf.graph.Triple;
import org.trippi.TripleIterator;
import org.trippi.TriplestoreReader;
import org.trippi.TrippiException;

import fedora.common.Constants;
import fedora.server.Server;
import fedora.server.errors.ModuleInitializationException;
import fedora.server.errors.ServerException;

/**
 * @author nishen@melcoe.mq.edu.au
 */

public class RelationshipResolverTrippiImpl implements RelationshipResolver
{
	private static Logger log = Logger.getLogger(RelationshipResolverTrippiImpl.class.getName());
	private static RelationshipResolverTrippiImpl instance = null;
	private static String DEFAULT_RELATIONSHIP = "info:fedora/fedora-system:def/relations-external#isMemberOf";

	private List<String> relationships = null;
	private TriplestoreReader riReader = null;

	public RelationshipResolverTrippiImpl()
	{
		relationships = new ArrayList<String>(1);
		relationships.add(DEFAULT_RELATIONSHIP);
	}

	public RelationshipResolverTrippiImpl(Map<String, String> options)
	{
		relationships = new ArrayList<String>();
		List<String> keys = new ArrayList<String>(options.keySet());
		Collections.sort(keys);
		for (String s : keys)
			if (s.startsWith("parent-child-relationship"))
				relationships.add(options.get(s));
	}
	
	public static RelationshipResolverTrippiImpl getInstance()
	{
		if (instance == null)
			instance = new RelationshipResolverTrippiImpl();

		return instance;
	}

	private TripleIterator getTriples(String query) throws PEPException
	{
		if (riReader == null)
		{
			try
			{
				String role = "fedora.server.resourceIndex.ResourceIndex";

				Server server = Server.getInstance(new File(Constants.FEDORA_HOME), true);
				if (log.isDebugEnabled())
					log.debug("Obtained Fedora Server Instance");

				riReader = (TriplestoreReader) server.getModule(role);
				if (riReader == null)
					throw new ModuleInitializationException("Could not instantiate ResourceIndex.", role);

				if (log.isDebugEnabled())
					log.debug("Obtained ResourceIndexModule");
			}
			catch (ServerException e)
			{
				log.fatal("Failed to initialize RelationshipResolver");
				throw new PEPException(e);
			}
		}

		try
		{
			TripleIterator results = riReader.findTriples("spo", query, 100, true);
			return results;
		}
		catch (TrippiException e)
		{
			throw new PEPException("Error getting triple iterator: " + e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see melcoe.fedora.util.RelationshipResolver#getParents(java.lang.String)
	 */
	public List<String> getParents(String pid) throws PEPException
	{
		String pidDN = "<info:fedora/" + pid + ">";

		if (log.isDebugEnabled())
			log.debug("Obtaining parents for: " + pid);

		List<String> parents = new ArrayList<String>();

		for (String relationship : relationships)
		{
			try
			{
				String query = pidDN + " <" + relationship + "> *";
				if (log.isDebugEnabled())
					log.debug("triplestore query: " + query);

				TripleIterator i = getTriples(query);
				while (i.hasNext())
				{
					Triple t = i.next();
					String parentPid = t.getObject().toString();
					parents.add(parentPid.substring(12));
	
					if (log.isDebugEnabled())
						log.debug("Found parent: " + parentPid);
				}
			}
			catch (TrippiException te)
			{
				throw new PEPException("Error retrieving triple.", te);
			}
			catch (PEPException e)
			{
				throw new PEPException("Error obaining parents.", e);
			}
		}

		return parents;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see melcoe.fedora.util.RelationshipResolver#buildRESTParentHierarchy(java.lang.String)
	 */
	public String buildRESTParentHierarchy(String pid) throws PEPException
	{
		List<String> parents = getParents(pid);
		if (parents == null || parents.size() == 0)
			return "/" + pid;

		String[] parentArray = parents.toArray(new String[parents.size()]);

		return buildRESTParentHierarchy(parentArray[0]) + "/" + pid;
	}
}
