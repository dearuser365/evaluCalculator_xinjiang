package com.kd.business.loader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.kd.entity.Organ;
import com.kd.service.SystemService;

public class OrganLoader {
	private Map<String, Organ> codeMap = new HashMap<String, Organ>();
	private Map<String, Organ> nameMap = new HashMap<String, Organ>();
	private Map<String, Organ> pmsNameMap = new HashMap<String, Organ>();
	private Map<String, Organ> parentCodeMap = new HashMap<String, Organ>();
	private List<String> codeList = new ArrayList<String>();
	private List<Organ> organList = new ArrayList<Organ>();
	private List<String> cityCodeList = new ArrayList<String>();
	
	
	@Autowired
	private SystemService systemService;
	
	public void loadMap() {
		organList = systemService.getList(Organ.class);
		for (Organ organ : organList) {
			codeMap.put(organ.getCode(), organ);
			nameMap.put(organ.getName(), organ);
			codeList.add(organ.getCode());
			pmsNameMap.put(organ.getPmsName(), organ);
			if (null != organ.getParentCode()) {
				parentCodeMap.put(organ.getCode(), organ);
			} else {
				cityCodeList.add(organ.getCode());
			}
		}
	}

	public Map<String, Organ> getCodeMap() {
		return codeMap;
	}

	public Map<String, Organ> getNameMap() {
		return nameMap;
	}

	public Map<String, Organ> getPmsNameMap() {
		return pmsNameMap;
	}

	public Map<String, Organ> getParentCodeMap() {
		return parentCodeMap;
	}

	public void setParentCodeMap(Map<String, Organ> parentCodeMap) {
		this.parentCodeMap = parentCodeMap;
	}

	public List<String> getCodeList() {
		return codeList;
	}

	public void setCodeList(List<String> codeList) {
		this.codeList = codeList;
	}

	public List<String> getCityCodeList() {
		return cityCodeList;
	}

	public void setCityCodeList(List<String> cityCodeList) {
		this.cityCodeList = cityCodeList;
	}

	public List<Organ> getOrganList() {
		return organList;
	}

	public void setOrganList(List<Organ> organList) {
		this.organList = organList;
	}

}
