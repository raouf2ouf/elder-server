package fr.lirmm.graphik.elder.server.models;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "projects")
public class Project implements Serializable {
	@Id
	private String id;
	private String name;
	private String description;
	private String semantic;
	private String query;
	private boolean isPublic;
	private String creator_id;
	private Collection<String> contributors;
	private List<KnowledgeBaseRepresentation> kbs;
	
	
	public Project() {}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
	
	public String getSemantic() {
		return semantic;
	}

	public void setSemantic(String semantic) {
		this.semantic = semantic;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
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
	
	public Collection<String> getContributors() {
		return contributors;
	}

	public void setContributors(Collection<String> contributors) {
		this.contributors = contributors;
	}

	public List<KnowledgeBaseRepresentation> getKbs() {
		return kbs;
	}

	public void setKbs(List<KnowledgeBaseRepresentation> kbs) {
		this.kbs = kbs;
	}


	
	
	
	
	
}
