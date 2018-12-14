package fr.lirmm.graphik.elder.server.config;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

public class AssignPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    
	public static final String ATTR_PRINCIPAL = "__principal__";

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        final String name;
        if (!attributes.containsKey(ATTR_PRINCIPAL)) {
            name = UUID.randomUUID().toString();
            attributes.put(ATTR_PRINCIPAL,name);
        }
        else {
            name = (String)attributes.get(ATTR_PRINCIPAL);
        }
        return new Principal() {

            public String getName() {
                return name;
            }
        };
    }
}