/* The contents of this file are subject to the license and copyright terms
 * detailed in the license directory at the root of the source tree (also 
 * available online at http://www.fedora.info/license/).
 */

package fedora.client;

import java.awt.Dimension;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import org.apache.log4j.Logger;

import fedora.server.utilities.StreamUtility;

/**
 * A client to a Fedora server's upload facility, accessed via a
 * basic-authenticated multipart POST to the server. See
 * server.management.UploadServlet for protocol details.
 * 
 * @author Chris Wilper
 */
public class Uploader {

    private final MultiThreadedHttpConnectionManager m_cManager =
            new MultiThreadedHttpConnectionManager();

    private final String m_uploadURL;

    private final UsernamePasswordCredentials m_creds;

    private final FedoraClient fc;

    private static final Logger logger =
            Logger.getLogger(FedoraClient.class.getName());

    /**
     * Construct an uploader to a certain repository as a certain user.
     */
    public Uploader(String host, int port, String user, String pass)
            throws IOException {
        m_uploadURL =
                Administrator.getProtocol() + "://" + host + ":" + port
                        + "/fedora/management/upload";
        m_creds = new UsernamePasswordCredentials(user, pass);
        String baseURL =
                Administrator.getProtocol() + "://" + host + ":" + port
                        + "/fedora";
        fc = new FedoraClient(baseURL, user, pass);
    }

    /**
     * Construct an uploader to a certain repository as a certain user.
     */
    public Uploader(String protocol,
                    String host,
                    int port,
                    String user,
                    String pass)
            throws IOException {
        m_uploadURL =
                protocol + "://" + host + ":" + port
                        + "/fedora/management/upload";
        m_creds = new UsernamePasswordCredentials(user, pass);
        String baseURL = protocol + "://" + host + ":" + port + "/fedora";
        fc = new FedoraClient(baseURL, user, pass);
    }

    /**
     * Send the data from the stream to the server. This is less efficient than
     * <i>upload(File)</i>, but if you already have a stream, it's convenient.
     * This method takes care of temporarily making a File out of the stream,
     * making the request, and removing the temporary file. Having a File source
     * for the upload is necessary because the content-length must be sent along
     * with the request as per the HTTP Multipart POST protocol spec.
     */
    public String upload(InputStream in) throws IOException {
        File tempFile = File.createTempFile("fedora-upload-", null);
        FileOutputStream out = new FileOutputStream(tempFile);
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
     * Send a file to the server, getting back the identifier.
     */
    public String upload(File file) throws IOException {
        if (Administrator.INSTANCE == null) {
            return fc.uploadFile(file);
        } else {
            // paint initial status to the progress bar
            String msg =
                    "Uploading " + file.length() + " bytes to "
                            + fc.getUploadURL();
            Dimension d = Administrator.PROGRESS.getSize();
            Administrator.PROGRESS.setString(msg);
            Administrator.PROGRESS.setValue(100);
            Administrator.PROGRESS.paintImmediately(0,
                                                    0,
                                                    (int) d.getWidth() - 1,
                                                    (int) d.getHeight() - 1);

            // then start the thread, passing parms in
            HashMap<String, Object> PARMS = new HashMap<String, Object>();
            PARMS.put("fc", fc);
            PARMS.put("file", file);
            SwingWorker worker = new SwingWorker(PARMS) {

                @Override
                public Object construct() {
                    try {
                        FedoraClient fc = (FedoraClient) parms.get("fc");
                        File file = (File) parms.get("file");
                        return fc.uploadFile(file);
                    } catch (IOException e) {
                        thrownException = e;
                        return "";
                    }
                }
            };
            worker.start();

            // keep updating status till the worker's finished
            int ms = 200;
            while (!worker.done) {
                try {
                    Administrator.PROGRESS.setValue(ms);
                    Administrator.PROGRESS.paintImmediately(0, 0, (int) d
                            .getWidth() - 1, (int) d.getHeight() - 1);
                    Thread.sleep(100);
                    ms = ms + 100;
                    if (ms >= 2000) {
                        ms = 200;
                    }
                } catch (InterruptedException ie) {
                }
            }

            // reset the status bar to normal
            Administrator.PROGRESS.setValue(2000);
            Administrator.PROGRESS.paintImmediately(0,
                                                    0,
                                                    (int) d.getWidth() - 1,
                                                    (int) d.getHeight() - 1);
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }

            // report if there was an error; otherwise return the response
            if (worker.thrownException != null) {
                throw (IOException) worker.thrownException;
            } else {
                return (String) worker.getValue();
            }

        }
    }

    /**
     * Test this class by uploading the given file three times. First, with the
     * provided credentials, as an InputStream. Second, with the provided
     * credentials, as a File. Third, with bogus credentials, as a File.
     */
    public static void main(String[] args) {
        try {
            if (args.length == 5) {
                Uploader uploader =
                        new Uploader(args[0],
                                     Integer.parseInt(args[1]),
                                     args[2],
                                     args[3]);
                File f = new File(args[4]);
                System.out.println(uploader.upload(new FileInputStream(f)));
                System.out.println(uploader.upload(f));
                uploader =
                        new Uploader(args[0],
                                     Integer.parseInt(args[1]),
                                     args[2],
                                     args[3] + "test");
                System.out.println(uploader.upload(f));
            } else {
                System.err.println("Usage: Uploader host port user pass file");
            }
        } catch (Exception e) {
            System.err.println("ERROR: " + e.getMessage());
        }
    }

}