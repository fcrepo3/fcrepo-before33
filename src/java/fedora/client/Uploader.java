package fedora.client;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.MultipartPostMethod;

import fedora.server.utilities.StreamUtility;

/**
 * A client to a Fedora server's upload facility, accessed via a 
 * basic-authenticated multipart POST to the server.
 *
 * See server.management.UploadServlet for protocol details.
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
 */
public class Uploader {

    private MultiThreadedHttpConnectionManager m_cManager=
            new MultiThreadedHttpConnectionManager();

    private String m_uploadURL;
    private UsernamePasswordCredentials m_creds;

    /**
     * Construct an uploader to a certain repository as a certain user.
     */
    public Uploader(String host, int port, String user, String pass)
            throws IOException {
        m_uploadURL="http://" + host + ":" + port + "/fedora/management/upload";
        m_creds=new UsernamePasswordCredentials(user, pass);
    }

    /**
     * Send the data from the stream to the server.
     * 
     * This is less efficient than <i>upload(File)</i>, but if you already
     * have a stream, it's convenient.
     *
     * This method takes care of temporarily making a File out of the stream, 
     * making the request, and removing the temporary file.  Having a File 
     * source for the upload is necessary because the content-length must be 
     * sent along with the request as per the HTTP Multipart POST protocol spec.
     */
    public String upload(InputStream in) throws IOException {
        File tempFile=File.createTempFile("fedora-upload-", null);
        FileOutputStream out=new FileOutputStream(tempFile);
        try {
            StreamUtility.pipeStream(in, out, 8192);
            return upload(tempFile);
        } finally {
            in.close();
            out.close();
            if (!tempFile.delete()) {
                System.err.println("WARNING: Could not remove temporary file: "
                        + tempFile.getName());
                tempFile.deleteOnExit();
            }
        }
    }

    /**
     * Get a file's size, in bytes.  Return -1 if size can't be determined.
     */
    private long getFileSize(File f) {
        long size=0;
        InputStream in=null;
        try {
            in=new FileInputStream(f);
            byte[] buf = new byte[8192];
            int len;
            while ( ( len = in.read( buf ) ) > 0 ) {
                size+=len;
            }
        } catch (IOException e) {
        } finally {
            size=-1;
            try {
                if (in!=null) {
                    in.close();
                }
            } catch (IOException e) {
                System.err.println("WARNING: Could not close stream.");
            }
        }
        return size;
    }

    /**
     * Send a file to the server, getting back the identifier.
     */
    public String upload(File in) throws IOException {
        MultipartPostMethod post=null;
        try {
            HttpClient client=new HttpClient(m_cManager);
            client.setConnectionTimeout(20000); // wait 20 seconds max
            client.getState().setCredentials(null, null, m_creds);
            client.getState().setAuthenticationPreemptive(true); // don't bother with challenges
            post=new MultipartPostMethod(m_uploadURL);
            post.setDoAuthentication(true);
            post.addParameter("file", in);
            int resultCode=0;
            if (Administrator.INSTANCE!=null) {
                // do the work in a separate thread
                // construct the message
                long size=getFileSize(in); 
                StringBuffer msg=new StringBuffer();
                msg.append("Uploading ");
                if (size!=-1) {
                    msg.append(size);
                    msg.append(" bytes ");
                }
                msg.append("to " + m_uploadURL);
                // paint it to the progress bar
                Dimension d=null;
                d=Administrator.PROGRESS.getSize();
                Administrator.PROGRESS.setString(msg.toString());
                Administrator.PROGRESS.setValue(100);
                Administrator.PROGRESS.paintImmediately(0, 0, (int) d.getWidth()-1, (int) d.getHeight()-1);
                // then start the thread, passing parms in
                HashMap PARMS=new HashMap();
                PARMS.put("client", client);
                PARMS.put("post", post);
                SwingWorker worker=new SwingWorker(PARMS) {
                    public Object construct() {
                        try {
                            return new Integer(((HttpClient) parms.get("client")).executeMethod((MultipartPostMethod) parms.get("post")));
                        } catch (Exception e) {
                            thrownException=e;
                            return "";
                        }
                    }
                };
                worker.start();
                // The following code will run in the (safe) 
                // Swing event dispatcher thread.
                int ms=200;
                while (!worker.done) {
                    try {
                        Administrator.PROGRESS.setValue(ms);
                        Administrator.PROGRESS.paintImmediately(0, 0, (int) d.getWidth()-1, (int) d.getHeight()-1);
                        Thread.sleep(100);
                        ms=ms+100;
                        if (ms>=2000) ms=200;
                    } catch (InterruptedException ie) { }
                }
                if (worker.thrownException!=null)
                    throw worker.thrownException;
                Administrator.PROGRESS.setValue(2000);
                Administrator.PROGRESS.paintImmediately(0, 0, (int) d.getWidth()-1, (int) d.getHeight()-1);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) { }
                resultCode=((Integer) worker.getValue()).intValue();
            } else {
                resultCode=client.executeMethod(post);
            }
            if (resultCode!=201) {
                throw new IOException(HttpStatus.getStatusText(resultCode)
                        + ": " 
                        + replaceNewlines(post.getResponseBodyAsString(), " "));
            }
            return replaceNewlines(post.getResponseBodyAsString(), "");
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        } finally {
            if (post!=null) post.releaseConnection();
        }

    }

    /**
     * Replace newlines with the given string.
     */
    private static String replaceNewlines(String in, String replaceWith) {
        return in.replaceAll("\r", replaceWith).replaceAll("\n", replaceWith);
    }

    /**
     * Test this class by uploading the given file three times.
     *
     * First, with the provided credentials, as an InputStream.
     * Second, with the provided credentials, as a File.
     * Third, with bogus credentials, as a File.
     */
    public static void main(String[] args) {
        try {
            if (args.length==5) {
                Uploader uploader=new Uploader(args[0], Integer.parseInt(args[1]), args[2], args[3]);
                File f=new File(args[4]);
                System.out.println(uploader.upload(new FileInputStream(f)));
                System.out.println(uploader.upload(f));
                uploader=new Uploader(args[0], Integer.parseInt(args[1]), args[2], args[3] + "test");
                System.out.println(uploader.upload(f));
            } else {
                System.err.println("Usage: Uploader host port user pass file");
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

}