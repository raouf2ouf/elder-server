package fr.lirmm.graphik.elder.server.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import fr.lirmm.graphik.elder.server.model.KnowledgeBaseRepresentation;
import fr.lirmm.graphik.elder.server.model.Project;
import fr.lirmm.graphik.elder.server.model.UserInput;
import fr.lirmm.graphik.graal.api.core.AtomSetException;
import fr.lirmm.graphik.graal.api.forward_chaining.ChaseException;
import fr.lirmm.graphik.graal.api.homomorphism.HomomorphismException;
import fr.lirmm.graphik.graal.defeasible.core.DefeasibleKnowledgeBase;
import fr.lirmm.graphik.graal.elder.core.StatementGraph;
import fr.lirmm.graphik.graal.elder.persistance.StatementGraphJSONRepresentation;
import fr.lirmm.graphik.util.stream.IteratorException;

@RestController
public class StatementGraphController {
	public static final String origins = "*";
	
	@CrossOrigin(origins = origins)
	@RequestMapping(path = "/api/sg/build", method = RequestMethod.POST)
	public StatementGraphJSONRepresentation build(@RequestBody UserInput input) throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add(input.getDlgp());
		StatementGraph sg = new StatementGraph(kb);
		sg.build();
		
		return sg.getRepresentation();
	}
	
	@CrossOrigin(origins = origins)
	@RequestMapping(path = "/api/sg/query", method = RequestMethod.POST)
	public StatementGraphJSONRepresentation query(@RequestBody UserInput input) throws AtomSetException, IteratorException, ChaseException, HomomorphismException {
		// Creating the knowledgeBase
		DefeasibleKnowledgeBase kb = new DefeasibleKnowledgeBase();
		kb.add(input.getDlgp());

		// Reasoning with Statement Graph
		StatementGraph sg = new StatementGraph(kb, input.getSemantics());
		sg.build();
		
		String query = input.getQuery();
		String[] queries = query.split("\\.");
		for(String q : queries) {
			q = q.trim();
			if(!q.isEmpty()) {
				sg.groundQuery(q +".");
			}
		}
		
		return sg.getRepresentation();
	}
	
	
	
	@CrossOrigin(origins = origins)
	@RequestMapping(path = "/api/sg/test", method = RequestMethod.POST)
	public void test(@RequestBody Project project) {
		for(KnowledgeBaseRepresentation kb: project.getKbs()) {
			System.out.println(kb.toString());
		}
	}
}
