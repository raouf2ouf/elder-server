package fr.lirmm.graphik.elder.server.controllers;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.context.SecurityContextHolder;
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
import fr.lirmm.graphik.elder.server.security.UserPrincipal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/collaboration")
public class CollaborationController {
	private static final Logger logger = LoggerFactory.getLogger(CollaborationController.class);
	private static final String ENDPOINT = "/api/collaboration";
	
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
		KnowledgeBaseRepresentation kb = new KnowledgeBaseRepresentation(agent.getUsername(), agent.getId(), "");
		
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
		Project p = projectRepository.findById(projectId).get();
		if(null == p) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project not found"), HttpStatus.BAD_REQUEST);
		}
		
		if(null == kb.getId()) {// Kb does not exist
			kb.setId((new ObjectId()).toString());
			p.getKbs().add(kb);
		} else {
			for(KnowledgeBaseRepresentation k: p.getKbs()) {
				if(kb.getId().equals(k.getId())) {
					k.setSource(kb.getSource());
					k.setDlgp(kb.getDlgp());
					break;
				}
			}
		}
		projectRepository.save(p);
		
		// inform everyone
		messagingTemplate.convertAndSend(ENDPOINT + "/project/"+p.getId(), kb);
        return new ResponseEntity<KnowledgeBaseRepresentation>(kb, HttpStatus.OK);
    }
	
	@RequestMapping(value="/deleteKB/{projectId}", method=RequestMethod.POST)
    public ResponseEntity<?> deleteKB(@PathVariable String projectId, @RequestBody KnowledgeBaseRepresentation kb) {
		UserPrincipal user = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Project p = projectRepository.findById(projectId).get();
		if(null == p) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project not found"), HttpStatus.BAD_REQUEST);
		}
		
		Iterator<KnowledgeBaseRepresentation> it = p.getKbs().iterator();
		while(it.hasNext()) {
			KnowledgeBaseRepresentation k = it.next();
			if(kb.getId().equals(k.getId())) {
				it.remove();
				break;
			}
		}
		projectRepository.save(p);
		
		// inform everyone
		messagingTemplate.convertAndSend(ENDPOINT + "/project/"+p.getId()+"/delete", kb);
        return new ResponseEntity<KnowledgeBaseRepresentation>(kb, HttpStatus.OK);
    }
	
	@RequestMapping(value="/get/{projectId}/{kbId}", method=RequestMethod.GET)
    public ResponseEntity<?> getKB(@PathVariable String projectId, @PathVariable String kbId) {
		Project p = projectRepository.findById(projectId).get();
		if(null == p) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project not found"), HttpStatus.BAD_REQUEST);
		}
		KnowledgeBaseRepresentation k = null;
		for(KnowledgeBaseRepresentation kb: p.getKbs()) {
			if(kbId.equals(kb.getId())) {
				k = kb;
				break;
			}
		}
        return new ResponseEntity<KnowledgeBaseRepresentation>(k, HttpStatus.OK);
    }
}
