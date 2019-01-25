package fr.lirmm.graphik.elder.server.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.lirmm.graphik.elder.server.models.Agent;
import fr.lirmm.graphik.elder.server.models.messages.LoginForm;
import fr.lirmm.graphik.elder.server.models.messages.ResponseMessage;
import fr.lirmm.graphik.elder.server.models.messages.SignUpForm;
import fr.lirmm.graphik.elder.server.security.UserPrincipal;
import fr.lirmm.graphik.elder.server.security.UserService;
import fr.lirmm.graphik.elder.server.security.jwt.JwtProvider;
import fr.lirmm.graphik.elder.server.security.jwt.JwtResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserService userService;

	@Autowired
	JwtProvider jwtProvider;
	
	@GetMapping("/signout")
	public ResponseEntity<ResponseMessage> logoutUser(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth != null){
	        new SecurityContextLogoutHandler().logout(request, response, auth);
	    }
		return new ResponseEntity<ResponseMessage>(new ResponseMessage("Logged out successfully!"),
				HttpStatus.OK);
	}
	
	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginForm loginRequest) {
		return this.login(loginRequest.getUsername(), loginRequest.getPassword());
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm signUpRequest) {
		if (userService.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Fail -> Username is already taken!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userService.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<ResponseMessage>(new ResponseMessage("Fail -> Email is already in use!"),
					HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		Agent agent = new Agent(signUpRequest.getUsername(), signUpRequest.getEmail(),
				signUpRequest.getPassword());

		userService.save(agent);

		
		//return new ResponseEntity<ResponseMessage>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
		// Log the user in
		return this.login(signUpRequest.getUsername(), signUpRequest.getPassword());
	}
	
	private ResponseEntity<JwtResponse> login(String username, String password) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtProvider.generateJwtToken(authentication);
		UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();

		return new ResponseEntity<JwtResponse>(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()),
				HttpStatus.OK);
	}
}
