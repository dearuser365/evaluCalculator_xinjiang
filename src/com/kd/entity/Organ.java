package com.kd.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ORGAN", schema="EVALUSYSTEM.CONFIG")
public class Organ {
	private String id;
	private String code;
	private String parentCode;
	private String name;
	private String pmsName;
	private String subType;
	private String flag;
	
	@Id
	@Column(name = "POS", unique = true, nullable = false, length = 32)	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Column(name = "ORGAN_CODE", length = 256)
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	@Column(name = "PARENT_CODE", length = 256)
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	
	@Column(name = "ORGAN_NAME", length = 256)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name = "PMS_NAME", length = 256)
	public String getPmsName() {
		return pmsName;
	}
	public void setPmsName(String pmsName) {
		this.pmsName = pmsName;
	}
	
	@Column(name = "SUBTYPE", length = 256)
	public String getSubType() {
		return subType;
	}
	public void setSubType(String subType) {
		this.subType = subType;
	}
	
	@Column(name = "FLAG", length = 32)
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}

}
