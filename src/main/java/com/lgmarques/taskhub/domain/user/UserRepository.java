package com.lgmarques.taskhub.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<User, Long> {
    UserDetails findByEmail(String email);
    Boolean existsByEmail(String email);

    User findUserByEmail(String email);
}
