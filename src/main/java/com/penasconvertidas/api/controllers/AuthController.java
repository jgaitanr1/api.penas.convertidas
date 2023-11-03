package com.penasconvertidas.api.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penasconvertidas.api.models.ERole;
import com.penasconvertidas.api.models.Role;
import com.penasconvertidas.api.models.User;
import com.penasconvertidas.api.payload.request.ChangePasswordRequest;
import com.penasconvertidas.api.payload.request.LoginRequest;
import com.penasconvertidas.api.payload.request.SignupRequest;
import com.penasconvertidas.api.payload.response.MessageResponse;
import com.penasconvertidas.api.payload.response.UserInfoResponse;
import com.penasconvertidas.api.repository.RoleRepository;
import com.penasconvertidas.api.repository.UserRepository;
import com.penasconvertidas.api.security.jwt.JwtUtils;
import com.penasconvertidas.api.security.services.UserDetailsImpl;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		System.out.println("Ingreso auth");
		SecurityContextHolder.getContext().setAuthentication(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		System.out.println("Ingreso Implemets");
		ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(
				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
						userDetails.getNombre(), userDetails.getApellido(), userDetails.getDocIdentidad(),
						userDetails.getNroTelefono(), userDetails.getInstitucion(), userDetails.isEstado(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getNombre(),
				signUpRequest.getApellido(), signUpRequest.getDocIdentidad(),
				 signUpRequest.getInstitucion(), signUpRequest.getNroTelefono(), signUpRequest.isEstado());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					break;
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	@PostMapping("/signout")
	public ResponseEntity<?> logoutUser() {
		ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
				.body(new MessageResponse("You've been signed out!"));
	}
	
	@PostMapping("/changepassword")
	public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
	    User user = null;
		try {
			user = userRepository.findById(changePasswordRequest.getId()).orElseThrow(() -> new RuntimeException("User not found"));
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
	    if (!encoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
	        return ResponseEntity.badRequest().body(new MessageResponse("Error:  password anterior es incorrecto"));
	    }
	    user.setPassword(encoder.encode(changePasswordRequest.getNewPassword()));
	    userRepository.save(user);
	    return ResponseEntity.ok(new MessageResponse("Password fue cambiado, successfully"));
	}
	
	@PostMapping("/passgenerico")
	public ResponseEntity<?> changePasswordGenerico(@Valid @RequestBody User user) {
	    user.setPassword(encoder.encode(user.getDocIdentidad()));
	    userRepository.save(user);
	    return ResponseEntity.ok(new MessageResponse("Password fue cambiado, successfully"));
	}
	
}
