package com.gentics.cr.util;

import java.util.Map;

public class MapWrapper extends ResolvableWrapper {
	private Map<Object,Object> map;
	
	public MapWrapper(Map<Object,Object> map)
	{
		this.map=map;
	}
	
	
	@Override
	public Object get(String key) {
		return map.get(key);
		
	}

}
