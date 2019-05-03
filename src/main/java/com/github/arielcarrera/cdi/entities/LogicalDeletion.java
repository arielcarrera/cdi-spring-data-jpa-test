package com.github.arielcarrera.cdi.entities;

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class LogicalDeletion {

	public static final int NORMAL_STATUS = 10;
	public static final int DRAFT_STATUS = 50;
	public static final int DELETED_STATUS = 99;
	

	private int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	@Transient
	public void setLogicalDeleted() {
		this.status = DELETED_STATUS;
	}
	
	@Transient
	public boolean isLogicalDeleted() {
		return getStatus() == DELETED_STATUS;
	}

	@Transient
	public boolean isDraft() {
		return getStatus() == DRAFT_STATUS;
	}
	
	@Transient
	public void statusNormal() {
		setStatus(NORMAL_STATUS);
	}
	
	@Transient
	public void statusDeleted() {
		setStatus(DELETED_STATUS);
	}
	
	@Transient
	public void statusDraft() {
		setStatus(DRAFT_STATUS);
	}
	
}