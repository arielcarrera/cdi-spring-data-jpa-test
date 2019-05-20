package com.github.arielcarrera.cdi.entities;

import java.io.Serializable;

public interface Identifiable<ID extends Serializable> {

	public ID getId();

	public void setId(ID id);
	
}