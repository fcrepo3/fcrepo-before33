package fedora.client.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import fedora.client.ingest.AutoIngestor;
import fedora.oai.sample.RandomDCMetadataFactory;

/**
 *
 * <p><b>Title:</b> MassIngest.java</p>
 * <p><b>Description:</b> </p>
 *
 * -----------------------------------------------------------------------------
 *
 * <p><b>License and Copyright: </b>The contents of this file are subject to the
 * Mozilla Public License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License
 * at <a href="http://www.mozilla.org/MPL">http://www.mozilla.org/MPL/.</a></p>
 *
 * <p>Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.</p>
 *
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2004 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class MassIngest {

    private ArrayList m_wordList;

    public MassIngest(AutoIngestor ingestor, File templateFile,
            File dictFile, int numTimes) throws Exception {
        // load the template file into two parts... with splitter=##SPLITTER##
        BufferedReader in=new BufferedReader(new FileReader(templateFile));
        String nextLine="";
        StringBuffer startBuffer=new StringBuffer();
        StringBuffer endBuffer=new StringBuffer();
        boolean seenSplitter=false;
        while (nextLine!=null) {
            nextLine=in.readLine();
            if (nextLine!=null) {
                if (!seenSplitter) {
                    if (nextLine.startsWith("##SPLITTER##")) {
                        seenSplitter=true;
                    } else {
                        startBuffer.append(nextLine + "\n");
                    }
                } else {
                    endBuffer.append(nextLine + "\n");
                }
            }
        }
        in.close();
        String start=startBuffer.toString();
        String end=endBuffer.toString();
        RandomDCMetadataFactory dcFactory=new RandomDCMetadataFactory(dictFile);
        for (int i=0; i<numTimes; i++) {
            String xml=start + dcFactory.get(2, 13) + end;
            String pid=ingestor.ingestAndCommit(new ByteArrayInputStream(
                xml.getBytes("UTF-8")), "part of massingest of " + numTimes
                + " auto-generated objects.");
            int t=i+1;
            System.out.println(pid + " " + t + "/" + numTimes);
        }

    }



    public static void showUsage(String message) {
        System.out.println("ERROR: " + message);
        System.out.println("Usage: MassIngest host port username password templateFile dictionaryFile numTimes");
    }

    public static void main(String[] args) throws Exception {
        try {
            if (args.length!=7) {
                MassIngest.showUsage("You must provide six arguments.");
            } else {
                String hostName=args[0];
                int portNum=Integer.parseInt(args[1]);
                String username=args[2];
                String password=args[3];
                File dictFile=new File(args[5]);
                // third arg==file... must exist
                File f=new File(args[4]);
                AutoIngestor a=new AutoIngestor(hostName, portNum, username, password);
                MassIngest m=new MassIngest(a, f, dictFile, Integer.parseInt(args[6]));
            }
        } catch (Exception e) {
            MassIngest.showUsage(e.getClass().getName() + " - "
                + (e.getMessage()==null ? "(no detail provided)" : e.getMessage()));
        }
    }

}