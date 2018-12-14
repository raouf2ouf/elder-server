package fr.lirmm.graphik.elder.server.model;

import java.io.Serializable;

public class KnowledgeBaseRepresentation implements Serializable {
	private String source;
	private String agent_id;
	private String dlgp;
	private boolean selected;
	private boolean locked;
	private String type;
	
	public KnowledgeBaseRepresentation() {
		
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getAgent_id() {
		return agent_id;
	}

	public void setAgent_id(String agent_id) {
		this.agent_id = agent_id;
	}

	public String getDlgp() {
		return dlgp;
	}

	public void setDlgp(String dlgp) {
		this.dlgp = dlgp;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override 
	public String toString() {
		return "Source: " + this.getSource() +"; dlgp: " + this.dlgp;
	}
}
