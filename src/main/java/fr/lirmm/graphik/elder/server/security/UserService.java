package fr.lirmm.graphik.elder.server.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.lirmm.graphik.elder.server.models.Agent;
import fr.lirmm.graphik.elder.server.repositories.AgentRepository;

@Service(value = "userService")
public class UserService implements UserDetailsService {
	@Autowired
	private AgentRepository agentRepository;
	
	@Autowired
	PasswordEncoder encoder;

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Agent user = agentRepository.findByUsername(username);
		if(null == user) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return UserPrincipal.build(user);
	}
	
	public Agent findByEmail(String email) {
		return agentRepository.findByEmail(email);
	}
	
	public Agent findByUsername(String username) {
		return null;
	}
	
	public boolean existsByUsername(String username) {
		return agentRepository.existsByUsername(username);
	}
	
	public boolean existsByEmail(String email) {
		return agentRepository.existsByEmail(email);
	}
	
	public Agent save(Agent user) {
		user.setPassword(this.encoder.encode(user.getPassword()));
		return agentRepository.save(user);
	}
	
	public Agent update(Agent user) {
		return agentRepository.save(user);
	}
}
