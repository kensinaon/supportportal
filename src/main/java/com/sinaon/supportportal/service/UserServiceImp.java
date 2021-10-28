package com.sinaon.supportportal.service;

import static com.sinaon.supportportal.constant.UserImplConstant.DEFAULT_USER_IMAGE_PATH;
import static com.sinaon.supportportal.constant.UserImplConstant.EMAIL_ALREADY_EXIST;
import static com.sinaon.supportportal.constant.UserImplConstant.FOUND_USER_BY_USERNAME;
import static com.sinaon.supportportal.constant.UserImplConstant.NO_USER_FOUND_BY_USERNAME;
import static com.sinaon.supportportal.constant.UserImplConstant.USERNAME_ALREADY_EXIST;
import static com.sinaon.supportportal.enumeration.Role.ROLE_USER;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.sinaon.supportportal.domain.User;
import com.sinaon.supportportal.domain.UserPrincipal;
import com.sinaon.supportportal.exception.domain.EmailExistException;
import com.sinaon.supportportal.exception.domain.UserNotFoundException;
import com.sinaon.supportportal.exception.domain.UsernameExistException;
import com.sinaon.supportportal.repository.UserRepository;

/**
 * @author ksinaon
 * @since Sep 30, 2021
 */
@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImp implements UserService, UserDetailsService {

	private Logger LOGGER = LoggerFactory.getLogger(UserServiceImp.class);

	private UserRepository userRepository;
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	public UserServiceImp(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findUserByUsername(username);
		if (user == null) {
			LOGGER.error(NO_USER_FOUND_BY_USERNAME + username);
			throw new UsernameNotFoundException(NO_USER_FOUND_BY_USERNAME + username);
		} else {
			user.setLastLoginDateDisplay(user.getLastLoginDate());
			user.setLastLoginDate(new Date());
			userRepository.save(user);
			UserPrincipal userPrincipal = new UserPrincipal(user);
			LOGGER.info(FOUND_USER_BY_USERNAME + username);
			return userPrincipal;

		}

	}

	@Override
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@Override
	public User findUserByUsername(String username) {
		return userRepository.findUserByUsername(username);
	}

	@Override
	public User findUserByEmail(String email) {
		return userRepository.findUserByEmail(email);
	}

	@Override
	public User register(String firstname, String lastname, String username, String email)
			throws UserNotFoundException, UsernameExistException, EmailExistException {
		validateNewUsernameAndEmail(EMPTY, username, email);

		User user = new User();

		user.setUserId(generateUserId());
		String password = generatePassword();
		String encodedPassword = encodePassword(password);

		user.setFirstname(firstname);
		user.setLastname(lastname);
		user.setUsername(username);
		user.setEmail(email);
		user.setJoinDate(new Date());
		user.setPassword(encodedPassword);
		user.setActive(true);
		user.setNotLocked(true);
		user.setRoles(ROLE_USER.name());
		user.setAuthorities(ROLE_USER.getAuthorities());
		user.setProfileImageUrl(getTemporaryProfileImageUrl());
		userRepository.save(user);
		LOGGER.info("New User Password: " + password);
		return user;
	}

	private User validateNewUsernameAndEmail(String currentUsername, String newUsername, String newEmail)
			throws UserNotFoundException, UsernameExistException, EmailExistException {

		User userByNewUsername = findUserByUsername(newUsername);
		User userByNewEmail = findUserByEmail(newEmail);

		if (isNotBlank(currentUsername)) {
			User currentUser = findUserByUsername(currentUsername);
			if (currentUser == null) {
				throw new UserNotFoundException(NO_USER_FOUND_BY_USERNAME + currentUsername);
			}

			if (userByNewUsername != null && !currentUser.getId().equals(userByNewUsername.getId())) {
				throw new UsernameExistException(USERNAME_ALREADY_EXIST + userByNewUsername.getUsername());
			}

			if (userByNewEmail != null && !currentUser.getId().equals(userByNewEmail.getId())) {
				throw new EmailExistException(EMAIL_ALREADY_EXIST + userByNewEmail.getEmail());
			}
			return currentUser;
		} else {
			if (userByNewUsername != null) {
				throw new UsernameExistException(USERNAME_ALREADY_EXIST + userByNewUsername.getUsername());
			}
			if (userByNewEmail != null) {
				throw new EmailExistException(EMAIL_ALREADY_EXIST + userByNewEmail.getEmail());
			}
			return null;
		}
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String getTemporaryProfileImageUrl() {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(DEFAULT_USER_IMAGE_PATH).toUriString();
	}

}
