package fr.lirmm.graphik.elder.server.models;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "agents")
public class Agent implements Serializable {
	@Id
	private String id;
	private String email;
	private String username;
	private String password;
	private Collection<String> project_ids;
	
	public Agent() {}
	
	public Agent(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Collection<String> getProject_ids() {
		return project_ids;
	}
	public void setProject_ids(Collection<String> project_ids) {
		this.project_ids = project_ids;
	}
	
	
	
}
