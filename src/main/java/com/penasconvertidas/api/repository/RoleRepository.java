package com.penasconvertidas.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.penasconvertidas.api.models.ERole;
import com.penasconvertidas.api.models.Role;


public interface RoleRepository extends JpaRepository<Role, Long> {
	Optional<Role> findByName(ERole name);
}
