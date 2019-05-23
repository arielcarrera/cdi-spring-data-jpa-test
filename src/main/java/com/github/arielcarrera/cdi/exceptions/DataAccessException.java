package com.github.arielcarrera.cdi.exceptions;

/**
 * Data Access Exception
 * 
 * @author Ariel Carrera
 *
 */
public class DataAccessException extends RuntimeException {
	private static final long serialVersionUID = 1106710614844023088L;

	public DataAccessException() {
		super();
	}
	
	public DataAccessException(String message) {
		super(message);
	}

	public DataAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataAccessException(Throwable cause) {
		super(cause);
	}
	
}