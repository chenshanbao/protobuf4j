public static ${fullClassName} valueToEnum(int value) {
	
	if(${fullClassName}.enumTemplateCache.size() == 0) {
		synchronized (${fullClassName}.enumTemplateCache) {
			if(${fullClassName}.enumTemplateCache.size() == 0) {					
				${fullClassName}[] array = ${fullClassName}.values();				
				for(int i=0;i<array.length;i++) {
					${fullClassName}.enumTemplateCache.put(java.lang.Integer.valueOf(array[i].value()), array[i]);					
				}	
			}				
		}
	}	
	return (${fullClassName})enumTemplateCache.get(java.lang.Integer.valueOf(value));
	
}			