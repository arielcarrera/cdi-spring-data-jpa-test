package com.github.arielcarrera.cdi.exceptions;

/**
 * Operation Not Supported Exception
 * 
 * @author Ariel Carrera
 *
 */
public class NotSupportedException extends RuntimeException {
	private static final long serialVersionUID = 1106710614844023088L;

	public NotSupportedException(String message) {
		super(message);
	}
}