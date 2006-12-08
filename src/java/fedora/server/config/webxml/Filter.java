package fedora.server.config.webxml;

public class Filter {
	private String filterName;
	private String filterClass;
	
	public String getFilterClass() {
		return filterClass;
	}

	public void setFilterClass(String filterClass) {
		this.filterClass = filterClass;
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public Filter() {}

}
