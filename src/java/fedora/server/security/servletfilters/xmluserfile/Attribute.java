package fedora.server.security.servletfilters.xmluserfile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Attribute implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private List<String> values;
	
	public Attribute() {
		values = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getValues() {
		return values;
	}
	public void addValue(String value) {
		values.add(value);
	}
}	