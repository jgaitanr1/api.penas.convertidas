package com.penasconvertidas.api.security.services;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.penasconvertidas.api.models.User;
import com.penasconvertidas.api.repository.UserRepository;


@Service
public class UsuarioServiceImpl implements UsuarioService {

	@Autowired
	private UserRepository usuarioRepository;

	@Override
	public User agregarUsuario(User user) {
		return usuarioRepository.save(user);
	}

	@Override
	public User actualizarUsuario(User user) {
		return usuarioRepository.save(user);
	}

	@Override
	public Set<User> obtenerUsuarios() {
		return new LinkedHashSet<>(usuarioRepository.findAll());
	}

	@Override
	public User obtenerUsuario(Long id) {
		return usuarioRepository.findById(id).get();
	}

	@Override
	public void eliminarUsuario(Long id) {
		User usr = new User();
		usr.setId(id);
		usuarioRepository.delete(usr);
	}
}
