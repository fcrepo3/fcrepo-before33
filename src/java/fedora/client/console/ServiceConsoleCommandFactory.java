package fedora.client.console;

import java.lang.reflect.Method;
import javax.wsdl.PortType;

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