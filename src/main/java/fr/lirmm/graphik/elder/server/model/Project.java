package fr.lirmm.graphik.elder.server.model;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import fr.lirmm.graphik.graal.elder.persistance.StatementGraphJSONRepresentation;


@Document(collection = "projects")
public class Project implements Serializable {
	@Id
	private String id;
	private boolean isPublic;
	private String creator_id;
	private List<KnowledgeBaseRepresentation> kbs;
	
	public Project() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public String getCreator_id() {
		return creator_id;
	}

	public void setCreator_id(String creator_id) {
		this.creator_id = creator_id;
	}

	public List<KnowledgeBaseRepresentation> getKbs() {
		return kbs;
	}

	public void setKbs(List<KnowledgeBaseRepresentation> kbs) {
		this.kbs = kbs;
	}

	
	
	
	
	
}
