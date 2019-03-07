package fr.lirmm.graphik.elder.server.models;

import java.io.Serializable;

public class SgRequestPayload implements Serializable {
	private KnowledgeBaseRepresentation[] kbs;
	private String query;
	private String semantics;
	private String projectId;
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	SgRequestPayload() {
	}

	public KnowledgeBaseRepresentation[] getKbs() {
		return kbs;
	}

	public void setKbs(KnowledgeBaseRepresentation[] kbs) {
		this.kbs = kbs;
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
