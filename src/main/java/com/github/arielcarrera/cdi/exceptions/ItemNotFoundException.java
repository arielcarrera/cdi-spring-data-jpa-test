package com.github.arielcarrera.cdi.exceptions;

/**
 * Item Not Found Exception
 * 
 * @author Ariel Carrera
 *
 */
public class ItemNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1106710614844023088L;

	public ItemNotFoundException(String message) {
		super(message);
	}
}