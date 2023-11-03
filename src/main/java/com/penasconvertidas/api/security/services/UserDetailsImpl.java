package com.penasconvertidas.api.security.services;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.penasconvertidas.api.models.User;

public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String email;

	@JsonIgnore
	private String password;

	private String nombre;
	private String apellido;
	private String docIdentidad;
	private String nroTelefono;
	private String institucion;
	private boolean estado;

	private Collection<? extends GrantedAuthority> authorities;
	
	public UserDetailsImpl(Long id, String username, String email, String password,String nombre,
			String apellido, String docIdentidad, String nroTelefono, String institucion, boolean estado,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.password = password;
		this.nombre = nombre;
		this.apellido = apellido;
		this.docIdentidad = docIdentidad;
		this.nroTelefono = nroTelefono;
		this.institucion = institucion;
		this.estado = estado;
		this.authorities = authorities;
	}
	
	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toList());

		return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(),
				user.getNombre(), user.getApellido(), user.getDocIdentidad(), user.getNroTelefono(),
				user.getInstitucion(), user.isEstado(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public String getNombre(){
		return nombre;
	}
	
	public String getApellido() {
		return apellido;
	}

	public String getDocIdentidad() {
		return docIdentidad;
	}

	public String getNroTelefono() {
		return nroTelefono;
	}
	

	public String getInstitucion() {
		return institucion;
	}

	public boolean isEstado() {
		return estado;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
	
}
