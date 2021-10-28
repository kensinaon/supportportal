package com.sinaon.supportportal.service;

import java.util.List;

import com.sinaon.supportportal.domain.User;
import com.sinaon.supportportal.exception.domain.EmailExistException;
import com.sinaon.supportportal.exception.domain.UserNotFoundException;
import com.sinaon.supportportal.exception.domain.UsernameExistException;

/**
 * @author ksinaon
 * @since Sep 30, 2021
 */
public interface UserService {

	User register(String firstname, String lastname, String username, String email) throws UserNotFoundException, UsernameExistException, EmailExistException;

	List<User> getUsers();

	User findUserByUsername(String username);
	
	User findUserByEmail(String email);
}
