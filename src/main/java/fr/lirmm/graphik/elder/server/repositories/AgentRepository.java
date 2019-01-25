package fr.lirmm.graphik.elder.server.repositories;

import java.util.Collection;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import fr.lirmm.graphik.elder.server.models.Agent;

public interface AgentRepository extends MongoRepository<Agent, String>{
	Agent findByUsername(String username);
	Agent findByEmail(String email);
	
	Boolean existsByUsername(String username);
	Boolean existsByEmail(String email);
	
	@Query("{ 'username' : {'$regex' : '^?0' }}")
	Collection<Agent> getAllByUsername(String username);
}
