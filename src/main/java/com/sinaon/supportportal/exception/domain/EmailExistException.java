package com.sinaon.supportportal.exception.domain;

/**
 * @author ksinaon
 * @since Oct 7, 2021
 */
public class EmailExistException extends Exception {
	public EmailExistException(String message) {
		super(message);
	}
}
