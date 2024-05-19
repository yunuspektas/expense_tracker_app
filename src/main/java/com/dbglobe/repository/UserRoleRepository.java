package com.dbglobe.repository;


import com.dbglobe.domain.UserRole;
import com.dbglobe.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole,Integer> {

	Optional<UserRole> findByRoleType(RoleType roleType);

	boolean existsByRoleType(RoleType roleType);
}
