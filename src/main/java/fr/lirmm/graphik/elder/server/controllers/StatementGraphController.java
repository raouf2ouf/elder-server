package fr.lirmm.graphik.elder.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.lirmm.graphik.elder.server.models.KnowledgeBaseRepresentation;
import fr.lirmm.graphik.elder.server.models.Project;
import fr.lirmm.graphik.elder.server.models.SgRequestPayload;
import fr.lirmm.graphik.elder.server.models.messages.ResponseMessage;
import fr.lirmm.graphik.elder.server.repositories.ProjectRepository;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBaseCollection;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.graal.elder.persistance.StatementGraphJSONRepresentation;
import fr.lirmm.graphik.util.stream.IteratorException;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/sg")
public class StatementGraphController {
	@Autowired
	ProjectRepository projectRepository;
	
	@RequestMapping(path = "/build", method = RequestMethod.POST)
	public ResponseEntity<?> build(@RequestBody SgRequestPayload input) throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		// Creating the knowledgeBase
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		for(KnowledgeBaseRepresentation k: input.getKbs()) {
			try {
				kbs.addKnowledgeBase(k.getDlgp(), k.getSource());
			} catch(Exception e) {
				return new ResponseEntity<ResponseMessage>(
						new ResponseMessage("Agent " + k.getSource() + ": " +e.getCause().toString()),
						HttpStatus.BAD_REQUEST);
			}
		}
		try {
			StatementGraph sg = new StatementGraph(kbs);
		
			sg.build();
			return new ResponseEntity<StatementGraphJSONRepresentation>(sg.getRepresentation(), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage(ex.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
	}
	
	@RequestMapping(path = "/query", method = RequestMethod.POST)
	public ResponseEntity<?> query(@RequestBody SgRequestPayload input) throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		Project p = this.projectRepository.findById(input.getProjectId()).get();
		if(null == p) return new ResponseEntity<ResponseMessage>(new ResponseMessage("Project not found!"),
				HttpStatus.BAD_REQUEST);
		
		// Creating the knowledgeBase
		DefeasibleKnowledgeBaseCollection kbs = new DefeasibleKnowledgeBaseCollection();
		for(KnowledgeBaseRepresentation k: input.getKbs()) {
			try {
				kbs.addKnowledgeBase(k.getDlgp(), k.getSource());
			} catch(Exception e) {
				return new ResponseEntity<ResponseMessage>(
						new ResponseMessage("Agent " + k.getSource() + ":" +e.getCause().toString()),
						HttpStatus.BAD_REQUEST);
			}
		}
		try {
			// Reasoning with Statement Graph
			StatementGraph sg = new StatementGraph(kbs, input.getSemantics());
			sg.build();
			
			String query = input.getQuery();
			if(null == query) {
				return new ResponseEntity<ResponseMessage>(new ResponseMessage("Empty query!"), HttpStatus.BAD_REQUEST);
			}
			String[] queries = query.split("\\.");
			for(String q : queries) {
				q = q.trim();
				if(!q.isEmpty()) {
					sg.groundQuery(q +".");
				}
			}
			p.setSemantic(input.getSemantics());
			p.setQuery(input.getQuery());
			projectRepository.save(p);
			return new ResponseEntity<StatementGraphJSONRepresentation>(sg.getRepresentation(), HttpStatus.OK);
		} catch (Exception ex) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Query: " + ex.getCause().toString()), HttpStatus.BAD_REQUEST);
		}
	}
}
