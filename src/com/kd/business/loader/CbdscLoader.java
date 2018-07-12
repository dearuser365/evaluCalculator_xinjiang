package com.kd.business.loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.kd.entity.Cbdsc;
import com.kd.service.SystemService;
/**
 * 开关刀闸类型
 * */
public class CbdscLoader {
	private Map<String, Cbdsc> cbdscMap = new HashMap<String, Cbdsc>();
	@Autowired
	private SystemService systemService;
	
	public void loadMap() {
		List<Cbdsc> cbdscList = systemService.getList(Cbdsc.class);
		for (Cbdsc cbdsc : cbdscList) {
			cbdscMap.put(cbdsc.getPsrType(), cbdsc);
		}
	}

	public Map<String, Cbdsc> getCbdscMap() {
		return cbdscMap;
	}

}
