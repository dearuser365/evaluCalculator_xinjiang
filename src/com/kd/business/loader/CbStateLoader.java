package com.kd.business.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.kd.entity.CbState;
import com.kd.service.SystemService;

public class CbStateLoader {
	private Map<String, CbState> cbStateMap = new HashMap<String, CbState>();
	@Autowired
	private SystemService systemService;
	
	public void loadMap() {
		List<CbState> cbStateList = systemService.getList(CbState.class);
		for (CbState state:cbStateList) {
			cbStateMap.put(state.getState(), state);
		}
	}

	public Map<String, CbState> getCbStateMap() {
		return cbStateMap;
	}

	public void setCbStateMap(Map<String, CbState> cbStateMap) {
		this.cbStateMap = cbStateMap;
	}

}
