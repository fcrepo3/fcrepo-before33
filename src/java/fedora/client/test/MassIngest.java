package fedora.client.test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import fedora.client.ingest.AutoIngestor;

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
        // load the dictionary file
        in=new BufferedReader(new FileReader(dictFile));
        nextLine="";
        m_wordList=new ArrayList();
        while (nextLine!=null) {
            nextLine=in.readLine();
            if (nextLine!=null) {
                String[] words=nextLine.split(" ");
                for (int i=0; i<words.length; i++) {
                    String w=words[i];
                    if ( allLetters(w) ) {
                        m_wordList.add(w);
                    }
                }
            }
        }
        in.close();
        String[] dcElements=new String[] {"title", "creator", "subject",
                "description", "publisher", "contributor", "date", "type", 
                "format", "identifier", "source", "language", "relation",
                "coverage", "rights"};
                
        String start=startBuffer.toString(); 
        String end=endBuffer.toString();
        
        
        for (int i=0; i<numTimes; i++) {
            String xml=start + getRandomRecords(dcElements) + end;
            String pid=ingestor.ingestAndCommit(new ByteArrayInputStream(
                xml.getBytes("UTF-8")), "part of massingest of " + numTimes 
                + " auto-generated objects.");
            int t=i+1;
            System.out.println(pid + " " + t + "/" + numTimes);
        }
        
    }
    
    private String getRandomRecords(String[] dcElements) {
        StringBuffer out=new StringBuffer();
        for (int x=0; x<dcElements.length; x++) {
            String dcElement=dcElements[x];
            int num=1+getRandom(2);
            for (int i=0; i<num; i++) {
                out.append("<dc:" + dcElement + ">" + getRandomWords() + "</dc:" + dcElement + ">\n");
            }
        }
        return out.toString();
    }

    private String getRandomWords() {
        int count=2 + getRandom(12);
        StringBuffer out=new StringBuffer();
        for (int i=0; i<count; i++) {
            if (i>0) {
                out.append(" ");
            }
            out.append(getRandomWord());
        }
        return out.toString();
    }

    private String getRandomWord() {
        return (String) m_wordList.get(getRandom(m_wordList.size()));
    }
    
    public static int getRandom(int belowThis) {
        return (int) (Math.random() * belowThis);
    }
    
    private boolean allLetters(String w) {
        if (w.length()==0) return false;
        String l=w.toLowerCase();
        for (int i=0; i<l.length(); i++) {
            char c=l.charAt(i);
            if (c<'a' || c>'z') {
                return false;
            }
        }
        return true;
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