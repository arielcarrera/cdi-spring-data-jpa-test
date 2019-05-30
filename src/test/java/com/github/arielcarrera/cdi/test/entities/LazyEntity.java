package com.github.arielcarrera.cdi.test.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.arielcarrera.cdi.entities.Identifiable;

@Entity
public class LazyEntity implements Identifiable<Integer> {

	@Id
	private Integer id;

	private Integer value;

	public LazyEntity() {
		super();
	}

	public LazyEntity(Integer id, Integer value) {
		super();
		this.id = id;
		this.value = value;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

}
