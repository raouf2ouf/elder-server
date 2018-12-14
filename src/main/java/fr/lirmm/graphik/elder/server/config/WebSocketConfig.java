package fr.lirmm.graphik.elder.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    public static final String SUBSCRIBE_USER_PREFIX = "/agent";
    public static final String SUBSCRIBE_USER_REPLY = "/reply";
    public static final String SUBSCRIBE_PROJECT = "/project";
    public static final String APPLICATION_PREFIX = "/app";
    
    
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
		        .setHandshakeHandler(new AssignPrincipalHandshakeHandler())
        		.setAllowedOrigins("*");
    }


    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(APPLICATION_PREFIX);
        registry.setUserDestinationPrefix(SUBSCRIBE_USER_PREFIX);
        registry.enableSimpleBroker(SUBSCRIBE_PROJECT, SUBSCRIBE_USER_REPLY);
    }
}