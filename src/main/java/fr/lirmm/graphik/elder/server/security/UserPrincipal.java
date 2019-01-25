package fr.lirmm.graphik.elder.server.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import fr.lirmm.graphik.elder.server.models.Agent;

public class UserPrincipal implements UserDetails {
	
	private String id;
	private String username;
	private String email;
	@JsonIgnore
	private String password;
	
	public UserPrincipal(String id, String username, String email, String password) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	public static UserPrincipal build(Agent agent) {
		return new UserPrincipal(agent.getId(), agent.getUsername(), agent.getEmail(), agent.getPassword());
	}
	
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> authorities = new LinkedList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("AGENT"));
		return authorities;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(null == o || getClass() != o.getClass()) return false;
		UserPrincipal user = (UserPrincipal) o;
		return id.equals(user.getId());
	}
}
