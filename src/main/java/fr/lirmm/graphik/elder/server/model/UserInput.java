package fr.lirmm.graphik.elder.server.model;

import java.io.Serializable;

public class UserInput implements Serializable {
	private String dlgp;
	private String query;
	private String semantics;
	
	UserInput() {
	}

	public String getDlgp() {
		return dlgp;
	}

	public void setDlgp(String dlgp) {
		this.dlgp = dlgp;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getSemantics() {
		return semantics;
	}

	public void setSemantics(String semantics) {
		this.semantics = semantics;
	}
	
	
	
}
