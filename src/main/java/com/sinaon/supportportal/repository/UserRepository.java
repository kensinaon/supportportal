package com.sinaon.supportportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinaon.supportportal.domain.User;

/**
 * @author ksinaon
 * @since Sep 30, 2021
 */
public interface UserRepository extends JpaRepository<User, Long> {

	User findUserByUsername(String username);

	User findUserByEmail(String email);
}
