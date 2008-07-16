
package fedora.test.integration.cma;

import java.util.ArrayList;

import fedora.client.FedoraClient;
import fedora.server.types.gen.ObjectMethodsDef;

public abstract class Util {

    /* Remove any system methods */
    public static ObjectMethodsDef[] filterMethods(ObjectMethodsDef[] initial) {
        ArrayList<ObjectMethodsDef> desiredDefs =
                new ArrayList<ObjectMethodsDef>();

        for (ObjectMethodsDef def : initial) {
            if (!def.getServiceDefinitionPID().startsWith("fedora-system:")
                    && def != null) {
                desiredDefs.add(def);
            }
        }

        return desiredDefs.toArray(new ObjectMethodsDef[0]);
    }

    /* Get a given dissemination as a string */
    public static String getDissemination(FedoraClient client,
                                          String pid,
                                          String sDef,
                                          String method) throws Exception {
        return new String(client.getAPIA().getDissemination(pid,
                                                            sDef,
                                                            method,
                                                            null,
                                                            null).getStream(),
                          "UTF-8");

    }
}
