package fedora.client.console;

/**
 *
 * <p><b>Title:</b> InputPanelFactory.java</p>
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
public abstract class InputPanelFactory {

    public static InputPanel getPanel(Class cl) {
        if (cl.getName().equals("java.lang.String")) {
            return new StringInputPanel();
        }
        if (cl.getName().equals("[B")) {
            return new ByteArrayInputPanel(true);
        }
        if (cl.getName().equals("boolean")) {
            return new BooleanInputPanel(true);
        }
        if (cl.getName().equals("java.lang.Boolean")) {
            return new BooleanInputPanel(false);
        }
        if (cl.getName().equals("java.util.Calendar")) {
          return new DateTimeInputPanel();
        }
        if (cl.getName().equals("org.apache.axis.types.NonNegativeInteger")) {
          return new NonNegativeIntegerInputPanel();
        }
        if (cl.getName().startsWith("[L")) {
            try {
                return new ArrayInputPanel(Class.forName(
                        cl.getName().substring(2, cl.getName().length()-1)));
            } catch (ClassNotFoundException cnfe) {
                // will fall through as unrecognized
            }
        }
        System.out.println("Unrecognized type: " + cl.getName());
        return NullInputPanel.getInstance();
    }

}