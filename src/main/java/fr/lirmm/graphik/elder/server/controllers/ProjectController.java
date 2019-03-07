package fr.lirmm.graphik.elder.server.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.lirmm.graphik.elder.server.models.KnowledgeBaseRepresentation;
import fr.lirmm.graphik.elder.server.models.Project;
import fr.lirmm.graphik.elder.server.models.messages.ResponseMessage;
import fr.lirmm.graphik.elder.server.repositories.ProjectRepository;
import fr.lirmm.graphik.elder.server.security.UserPrincipal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/project")
public class ProjectController {
	
	@Autowired
	ProjectRepository projectRepository;
	
	@RequestMapping(value="/get/{projectId}", method=RequestMethod.GET)
	public Project getProject(@PathVariable String projectId) {
		Project p = this.projectRepository.findById(projectId).get();
		return p;
	}
	
	@RequestMapping(value="/get/created", method=RequestMethod.GET)
	public Collection<Project> getOwnProjects() {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<Project> ps = this.projectRepository.findByCreator_id(user.getId());
		return ps;
	}
	
	@RequestMapping(value="/get/collaboration", method=RequestMethod.GET)
	public Collection<Project> getCollaborationProjects() {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Collection<Project> ps = this.projectRepository.findCollaborations(user.getUsername());
		return ps;
	}
	
	
	@RequestMapping(value="/update", method=RequestMethod.POST)
	public Project updateProject(@RequestBody Project project) {
		Project p = this.projectRepository.findById(project.getId()).get();
		p.setName(project.getName());
		p.setDescription(project.getDescription());
		this.projectRepository.save(p);
		return p;
	}
	
	@RequestMapping(value="/delete/{projectId}", method=RequestMethod.GET)
	public ResponseEntity<ResponseMessage> deleteProject(@PathVariable String projectId) {
		Project p = this.projectRepository.findById(projectId).get();
		if(null == p) return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project not found!"),
				HttpStatus.BAD_REQUEST);
		
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(p.getCreator_id().equals(user.getId())) {
			this.projectRepository.deleteById(p.getId());
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project deleted!"),
					HttpStatus.OK);
		}
		
		return new ResponseEntity<ResponseMessage>(new ResponseMessage("Unauthorized!"),
					HttpStatus.BAD_REQUEST);
		
	}
	
	@RequestMapping(value="/add", method=RequestMethod.POST)
	public Project saveProject(@RequestBody Project project) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		project.setCreator_id(user.getId());
		project.getKbs().add(new KnowledgeBaseRepresentation("Common", user.getId(), "common"));
		this.projectRepository.save(project);
		return project;
	}
}
