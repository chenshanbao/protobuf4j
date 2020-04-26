/**
 * 
 */
package com.game37.protobuf4j.codec;
/**
* author chenshanbao
* create time 2020年3月17日
**/

public enum EnumTemplate {
	
	ONE(1),
	TWE(2),
	;
	
	private int value;
	
	private EnumTemplate(int value) {
		this.value =value;
	}
		
	public int getValue() {
		return value;
	}
	
	public int value() {
		return value;
	}


	private static java.util.Map enumTemplateCache = new java.util.HashMap();
		
	public static EnumTemplate valueToEnum(int value) {
		if(enumTemplateCache.size() == 0) {
			synchronized (enumTemplateCache) {
				if(enumTemplateCache.size() == 0) {					
					EnumTemplate[] array = values();
					for(EnumTemplate o:array) {												
						enumTemplateCache.put(o.value(), o);
					}
				}				
			}
		}	
		return (EnumTemplate)enumTemplateCache.get(value);
	}
	
	public static void main(String[] args) {
		System.out.println(valueToEnum(1));
		System.out.println(valueToEnum(3));
	}

}
