package fedora.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
 *
 * @author cwilper@cs.cornell.edu
 */
public class Uploader {

    private MultiThreadedHttpConnectionManager m_cManager=
            new MultiThreadedHttpConnectionManager();

    private String m_uploadURL;
    private UsernamePasswordCredentials m_creds;
    private File m_tempDir;

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
            int resultCode=client.executeMethod(post);
            if (resultCode!=201) {
                throw new IOException(HttpStatus.getStatusText(resultCode)
                        + ": " 
                        + replaceNewlines(post.getResponseBodyAsString(), " "));
            }
            return post.getResponseBodyAsString();
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