package fr.lirmm.graphik.elder.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import fr.lirmm.graphik.elder.server.config.WebSocketConfig;
import fr.lirmm.graphik.elder.server.model.HelperDeleteAfterTest;
import fr.lirmm.graphik.elder.server.model.KnowledgeBaseRepresentation;
import fr.lirmm.graphik.elder.server.model.Project;
import persistance.ProjectRepository;

@Controller
@EnableMongoRepositories(basePackageClasses = ProjectRepository.class)
public class CollaborationController {
	private static final Logger logger = LoggerFactory.getLogger(CollaborationController.class);
	private static final String ENDPOINT_REGISTER = "/sg.addUser",
			ENDPOINT_SEND_MESSAGE = "/sg.sendKB",
			BROADCAST_CHANNEL = "/project",
			BROADCAST_CHANNEL_ID = "/1";
	
	
	@Autowired
    private SimpMessageSendingOperations messagingTemplate;
	
	@Autowired private ProjectRepository projectRepository;
	
	@MessageMapping(ENDPOINT_SEND_MESSAGE)
    @SendTo(BROADCAST_CHANNEL + BROADCAST_CHANNEL_ID)
    public KnowledgeBaseRepresentation sendMessage(@Payload KnowledgeBaseRepresentation kb) {
		HelperDeleteAfterTest.KBS.put(kb.getSource(), kb);
		Project p = projectRepository.findById("1").get();
		for(KnowledgeBaseRepresentation k : p.getKbs()) {
			if(k.getAgent_id().equals(kb.getAgent_id())) {
				k.setDlgp(kb.getDlgp());
				break;
			}
		}
		projectRepository.save(p);
		logger.info(kb.getSource() + " sent this: " + kb.toString());
        return kb;
    }
	
	
	@MessageMapping(ENDPOINT_REGISTER)
    public void addUser(@Payload KnowledgeBaseRepresentation kb, 
                               SimpMessageHeaderAccessor headerAccessor) {        
        
		Project p = projectRepository.findById("1").get();
		boolean agentAlreadyExists = false;
		for(KnowledgeBaseRepresentation k : p.getKbs()) {
			if(k.getAgent_id().equals(kb.getAgent_id())) {
				agentAlreadyExists = true;
				break;
			}
		}
		if(!agentAlreadyExists) {
			p.getKbs().add(kb);
			projectRepository.save(p);
			kb.setLocked(true);
			// broadcasting the new user event to everyone
			messagingTemplate.convertAndSend(BROADCAST_CHANNEL + BROADCAST_CHANNEL_ID, kb);
		}
		
		// Locking all other knowledge bases from easy access
		for(KnowledgeBaseRepresentation k : p.getKbs()) {
			if(!k.getAgent_id().equals(kb.getAgent_id())) {
				k.setLocked(true);
			} else {
				k.setLocked(false);
			}
		}
		
		// sending the current state of the whole project to the user
		messagingTemplate.convertAndSendToUser(headerAccessor.getUser().getName(),
				WebSocketConfig.SUBSCRIBE_USER_REPLY + BROADCAST_CHANNEL_ID,
				p.getKbs());
		
    }
}
