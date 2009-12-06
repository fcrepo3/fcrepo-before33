/*
 * File: PopulatePolicyDatabase.java
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

package melcoe.xacml.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import melcoe.xacml.pdp.data.DbXmlPolicyDataManager;
import melcoe.xacml.pdp.data.PolicyDataManagerException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/**
 * @author nishen@melcoe.mq.edu.au
 * 
 */
public class PopulatePolicyDatabase {
	private static final Logger log = Logger
			.getLogger(PopulatePolicyDatabase.class);

	private static final String POLICY_HOME = System.getenv("MELCOEPDP_HOME")
			+ "/policies";
	private static DbXmlPolicyDataManager dbXmlPolicyDataManager;

	private static Set<String> policyNames = new HashSet<String>();

	public static void main(String[] args) throws PolicyDataManagerException,
			FileNotFoundException {
		BasicConfigurator.configure();
		dbXmlPolicyDataManager = new DbXmlPolicyDataManager();
		log.info("Adding");
		add();
		log.info("Listing");
		list();
	}

	public static void add() throws PolicyDataManagerException,
			FileNotFoundException {
		log.info("Starting clock!");
		long time1 = System.nanoTime();
		addDocuments();
		long time2 = System.nanoTime();
		log.info("Stopping clock!");
		log.info("Time taken: " + (time2 - time1));
	}

	public static void addDocuments() throws PolicyDataManagerException,
			FileNotFoundException {
		File[] files = getPolicyFiles();
		if (files.length == 0)
			return;

		for (File f : files) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] bytes = new byte[1024];
			FileInputStream fis = new FileInputStream(f);

			log.info("Adding: " + f.getName());

			try {
				int count = fis.read(bytes);
				while (count > -1) {
					out.write(bytes, 0, count);
					count = fis.read(bytes);
				}
			} catch (IOException e) {
				log.error("Error reading file: " + f.getName(), e);
			}

			policyNames.add(dbXmlPolicyDataManager.addPolicy(out.toString()));
		}
	}

	public static void list() throws PolicyDataManagerException {
		List<String> docNames = dbXmlPolicyDataManager.listPolicies();
		for (String s : docNames) {
			log.info("doc: " + s);
		}
	}

	public static File[] getPolicyFiles() {
		File policyHome = new File(POLICY_HOME);
		File[] policies = policyHome.listFiles(new PolicyFileFilter());
		return policies;
	}
}
