package com.github.arielcarrera.cdi.test.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.github.arielcarrera.cdi.entities.Identifiable;
import com.github.arielcarrera.cdi.entities.LogicalDeletion;

@Entity
public class TestEntity extends LogicalDeletion implements Identifiable<Integer> {

	@Id
	private Integer id;

	private Integer value;
	
	@Column(nullable=true,unique=true)
	private Integer uniqueValue;

	public TestEntity() {
		super();
	}

	public TestEntity(Integer id, Integer value) {
		super();
		this.id = id;
		this.value = value;
	}
	
	public TestEntity(Integer id, Integer value, Integer uniqueValue) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
	}
	
	public TestEntity(Integer id, Integer value, Integer uniqueValue, int status) {
		super();
		this.id = id;
		this.value = value;
		this.uniqueValue = uniqueValue;
		this.status = status;
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

	public Integer getUniqueValue() {
		return uniqueValue;
	}

	public void setUniqueValue(Integer uniqueValue) {
		this.uniqueValue = uniqueValue;
	}

}
