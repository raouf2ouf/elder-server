package fr.lirmm.graphik.elder.server.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import org.bson.types.ObjectId;

public class KnowledgeBaseRepresentation implements Serializable {

	private String id;
	private String source;
	private String agent_id;
	private String dlgp;
	private boolean selected;
	private boolean locked;
	private String type;
	private Collection<String> editors;
	
	public KnowledgeBaseRepresentation() {
		
	}
	
	public KnowledgeBaseRepresentation(String source, String agent_id, String type) {
		this.id = (new ObjectId()).toString();
		this.source = source;
		this.agent_id = agent_id;
		this.dlgp = "";
		this.selected = true;
		this.type = type;
		this.editors = new LinkedList<String>();
		this.editors.add(source);
	}
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Collection<String> getEditors() {
		return editors;
	}

	public void setEditors(Collection<String> editors) {
		this.editors = editors;
	}

	@Override 
	public String toString() {
		return "Source: " + this.getSource() +"; dlgp: " + this.dlgp;
	}
}
