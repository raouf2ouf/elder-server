package persistance;

import org.springframework.data.mongodb.repository.MongoRepository;

import fr.lirmm.graphik.elder.server.model.Project;

public interface ProjectRepository extends MongoRepository<Project, String>{
	
}
