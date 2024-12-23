package com.friendbook.Exception;

public class UserException extends Exception {
	private static final long serialVersionUID = -6510309245117420350L;

	
	public UserException() {}
	
	public UserException(String message) {
		super(message);
	}
}
