package fedora.server.config;

import java.util.*;

public abstract class Configuration {

    private List m_parameters;

    protected Configuration(List parameters) {
        m_parameters = parameters;
        System.out.println("Params:");
        for (int i = 0; i < m_parameters.size(); i++) {
            Parameter p = (Parameter) m_parameters.get(i);
            System.out.println("  name : " + p.getName());
            System.out.println("  desc : " + p.getComment());
            System.out.println("  val  : " + p.getValue());
            Iterator iter = p.getProfileValues().keySet().iterator();
            while (iter.hasNext()) {
                String name = (String) iter.next();
                String val = (String) p.getProfileValues().get(name);
                System.out.println("    " + name + " : " + val);
            }
            System.out.println("");
        }
    }

    public List getParameters() {
        return m_parameters;
    }

}
