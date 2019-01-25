package fr.lirmm.graphik.elder.server.controllers;

import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.lirmm.graphik.elder.server.models.Agent;
import fr.lirmm.graphik.elder.server.models.KnowledgeBaseRepresentation;
import fr.lirmm.graphik.elder.server.models.Project;
import fr.lirmm.graphik.elder.server.models.messages.ResponseMessage;
import fr.lirmm.graphik.elder.server.repositories.AgentRepository;
import fr.lirmm.graphik.elder.server.repositories.ProjectRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/collaboration")
public class CollaborationController {
	private static final Logger logger = LoggerFactory.getLogger(CollaborationController.class);
	private static final String ENDPOINT = "/api/collaboration",
			ENDPOINT_SEND_MESSAGE = "/sg.sendKB",
			BROADCAST_CHANNEL = "/project",
			BROADCAST_CHANNEL_ID = "/1";
	
	
	@Autowired
    private SimpMessageSendingOperations messagingTemplate;
	
	@Autowired 
	private ProjectRepository projectRepository;
	
	@Autowired
	private AgentRepository agentRepository;
	
	
	@RequestMapping(value="/user/{username}", method=RequestMethod.GET)
	public Collection<String> getAgents(@PathVariable String username) {
		Collection<Agent> agents = this.agentRepository.getAllByUsername(username);
		Collection<String> usernames = new LinkedList<String>();
		for(Agent a: agents) {
			usernames.add(a.getUsername());
		}
		return usernames;
	}
	
	@RequestMapping(value="/invite/{username}/to/{projectId}", method=RequestMethod.GET)
	public ResponseEntity<ResponseMessage> inviteUser(@PathVariable String username, @PathVariable String projectId) {
		Agent agent = agentRepository.findByUsername(username);

		Project p = this.projectRepository.findById(projectId).get();
		
		if(null == agent || null == p) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("user or project not found"), HttpStatus.BAD_REQUEST);
		}
		KnowledgeBaseRepresentation kb = new KnowledgeBaseRepresentation(agent.getUsername(), agent.getId());
		
		boolean agentAlreadyExists = false;
		for(KnowledgeBaseRepresentation k : p.getKbs()) {
			if(k.getSource().equals(kb.getSource())) {
				agentAlreadyExists = true;
				break;
			}
		}
		if(!agentAlreadyExists) {
			if(null == p.getContributors()) p.setContributors(new LinkedList<String>());
			p.getContributors().add(agent.getUsername());
			p.getKbs().add(kb);
			projectRepository.save(p);
			kb.setLocked(true);
			// inform everyone
			messagingTemplate.convertAndSend(ENDPOINT + "/project/"+p.getId(), kb);
		}
		
		
		return new ResponseEntity<ResponseMessage>(new ResponseMessage("User added"), HttpStatus.OK);
	}
	
	@RequestMapping(value="/saveKB/{projectId}", method=RequestMethod.POST)
    public ResponseEntity<?> saveKB(@PathVariable String projectId, @RequestBody KnowledgeBaseRepresentation kb) {
		logger.info("saving KB!!!");
		Project p = projectRepository.findById(projectId).get();
		if(null == p) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project not found"), HttpStatus.BAD_REQUEST);
		}
		
		boolean alreadyExists = false;
		for(KnowledgeBaseRepresentation k : p.getKbs()) {
			if(k.getSource().equals(kb.getSource())) {
				k.setDlgp(kb.getDlgp());
				k.setEditors(kb.getEditors());
				alreadyExists = true;
				break;
			}
		}
		
		if(!alreadyExists) {
			p.getKbs().add(kb);
		}
		projectRepository.save(p);
		
		logger.info(kb.getSource() + " sent this: " + kb.toString());
		// inform everyone
		messagingTemplate.convertAndSend(ENDPOINT + "/project/"+p.getId(), kb);
        return new ResponseEntity<KnowledgeBaseRepresentation>(kb, HttpStatus.OK);
    }
}
