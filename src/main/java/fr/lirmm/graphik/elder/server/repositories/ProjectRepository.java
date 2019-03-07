package fr.lirmm.graphik.elder.server.repositories;

import java.util.Collection;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import fr.lirmm.graphik.elder.server.models.Project;

public interface ProjectRepository extends MongoRepository<Project, String>{
	Optional<Project> findById(String id);
	
	void deleteById(String id);
	
	@Query("{ 'creator_id' : ?0 }")
	Collection<Project> findByCreator_id(String creator_id);
	
	@Query("{ 'contributors' : ?0 }")
	Collection<Project> findCollaborations(String username);
}
