package fedora.client.console;

import java.lang.reflect.Method;
import javax.wsdl.PortType;

/**
 *
 * <p><b>Title:</b> ServiceConsoleCommandFactory.java</p>
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
 * <p>The entire file consists of original code.  Copyright &copy; 2002-2005 by The
 * Rector and Visitors of the University of Virginia and Cornell University.
 * All rights reserved.</p>
 *
 * -----------------------------------------------------------------------------
 *
 * @author cwilper@cs.cornell.edu
 * @version $Id$
 */
public class ServiceConsoleCommandFactory {

    public static ConsoleCommand[] getConsoleCommands(Class javaInterface,
            PortType wsdlInterface) {
        if (!javaInterface.isInterface()) {
            return null;
        }
        Method[] methods=javaInterface.getDeclaredMethods();
        ConsoleCommand[] commands=new ConsoleCommand[methods.length];
        for (int i=0; i<methods.length; i++) {
        /*
            public ConsoleCommand(Method method, String methodDescription,
            String[] paramNames, String[] paramDescriptions,
            String returnDescription) {
        */
            commands[i]=new ConsoleCommand(methods[i], null, null, null, null);
        }
        return commands;
    }

}