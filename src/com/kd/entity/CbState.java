package com.kd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CBSTATE", schema="EVALUSYSTEM.CONFIG")
public class CbState {
	private String pos;
	private String type;
	private String state;
	private String num;
	
	@Id
	@Column(name = "POS", unique = true, nullable = false, length = 32)
	public String getPos() {
		return pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	
	@Column(name = "TYPE", length = 256)
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Column(name = "STATE", length = 256)
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	@Column(name = "NUM", length = 256)
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	
}
