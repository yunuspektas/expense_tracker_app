package com.dbglobe.repository;

import com.dbglobe.domain.User;
import com.dbglobe.domain.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phone);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT COUNT(u) FROM User u WHERE u.userRole.roleType = ?1")
    int countAdminOrCustomer(RoleType roleType);

}
