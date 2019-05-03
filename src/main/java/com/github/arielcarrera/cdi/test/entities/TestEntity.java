package com.github.arielcarrera.cdi.test.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TestEntity {

	@Id
	public Integer id;

	public Integer value;

	public TestEntity() {
		super();
	}

	public TestEntity(Integer id, Integer point) {
		super();
		this.id = id;
		this.value = point;
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
