package fedora.server.utilities;

import java.util.StringTokenizer;

/**
 *
 * <p><b>Title:</b> StringUtility.java</p>
 * <p><b>Description:</b> A utility class for common string operations.</p>
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
 * @author rlw@virginia.edu
 * @version $Id $
 */
public class StringUtility
{

  public StringUtility()
  {  }

  /**
   * <p> Method that attempts to break a string up into lines no longer
   * than the specified line length. The string is assumed to consist of tokens
   * separated by a delimeter. The default delimiter is a space. If the last
   * token to be added to a line exceeds the specified line length, it is
   * written on the next line so actual line length is approximate given the
   * specified line length and the length of tokens in the string.</p>
   *
   *
   * @param in The input string to be split into lines.
   * @param lineLength The maximum length of each line.
   * @param delim The character delimiter separating each token in the input
   *              string; if null, defaults to the space character.
   * @return A string split into multiple lines whose lenght is less than the
   *         specified length. Actual length is approximate depending on line
   *         length, token size, and how many complete tokens will fit into
   *         the specified line length.
   */
  public static String prettyPrint(String in, int lineLength, String delim)
  {
    StringBuffer sb = new StringBuffer();
    if (delim==null)
      delim = " ";
    StringTokenizer st = new StringTokenizer(in, delim);
    int charCount = 0;
    while (st.hasMoreTokens()) {
      String s = st.nextToken();
      charCount = charCount + s.length();
      if (charCount < lineLength)
      {
        sb.append(s+" ");
        charCount++;
      } else
      {
        sb.append("\n");
        sb.append(s+" ");
        charCount = s.length() + 1;
      }
     }
    return sb.toString();
  }

  public static void main(String[] args)
  {
    StringUtility su = new StringUtility();
    String pid = "demo:1";
    String in = "org.apache.axis.AxisFault: The digital object \""
                    + pid + "\" is used by one or more other objects "
                    + "in the repository. All related objects must be removed "
                    + "before this object may be deleted. Use the search "
                    + "interface with the query \"bDef~" + pid
                    + "\" to obtain a list of dependent objects.";
    System.out.println("123456789+123456789+123456789+123456789+123456789+123456789+123456789+123456789+");
    System.out.println(StringUtility.prettyPrint(in, 70, null));
  }
}
