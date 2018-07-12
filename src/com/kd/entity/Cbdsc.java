package com.kd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CBDSC", schema="EVALUSYSTEM.CONFIG")
public class Cbdsc {
	private String id;
	private String psrType;
	private String type;
	
	@Id
	@Column(name = "POS", unique = true, nullable = false, length = 32)
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "PSRTYPE", length = 256)
	public String getPsrType() {
		return psrType;
	}
	public void setPsrType(String psrType) {
		this.psrType = psrType;
	}
	
	@Column(name = "TYPE", length = 256)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
