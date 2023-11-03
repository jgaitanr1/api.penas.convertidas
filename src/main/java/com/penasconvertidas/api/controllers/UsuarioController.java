package com.penasconvertidas.api.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.penasconvertidas.api.models.User;
import com.penasconvertidas.api.payload.request.ChangePasswordRequest;
import com.penasconvertidas.api.payload.response.MessageResponse;
import com.penasconvertidas.api.repository.UserRepository;
import com.penasconvertidas.api.security.services.UsuarioService;


@RestController
@RequestMapping("/api/usuario")
@CrossOrigin("*")
public class UsuarioController {

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	UserRepository userRepository;

	@PostMapping("/")
	public ResponseEntity<User> guardar(@RequestBody User usr) {
		User usrGuardada = usuarioService.agregarUsuario(usr);
		return ResponseEntity.ok(usrGuardada);
	}

	@GetMapping("/{Id}")
	public User listarUsuarioPorId(@PathVariable("Id") Long Id) {
		return usuarioService.obtenerUsuario(Id);
	}

	@GetMapping("/")
	public ResponseEntity<?> listarUsuarios() {
		return ResponseEntity.ok(usuarioService.obtenerUsuarios());
	}

	@PutMapping("/")
	public User actualizarUser(@RequestBody User user) {
		return usuarioService.actualizarUsuario(user);
	}

	@DeleteMapping("/{Id}")
	public void eliminarUser(@PathVariable("Id") Long Id) {
		usuarioService.eliminarUsuario(Id);
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
	    usuarioService.actualizarUsuario(user);
	    return ResponseEntity.ok(new MessageResponse("Password fue cambiado, successfully"));
	}
	
}
