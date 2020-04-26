/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import javassist.CtClass;

/**
* author chenshanbao
**/

public enum DefaultValue {
	
	BASE_INT("Integer.MAX_VALUE"), 
	INT("null"),
	
	BASE_LONG("Long.MAX_VALUE"), 
	LONG("null"),	
	
	BASE_DOUBLE("Double.MAX_VALUE"), 
	DOUBLE("null"),
	
	BASE_FLOAT("Float.MAX_VALUE"), 
	FLOAT("null"),
	
	
//	BASE_INT("0"), 
//	INT("null"),
//	
//	BASE_LONG("0"), 
//	LONG("null"),	
//	
//	BASE_DOUBLE("0"), 
//	DOUBLE("null"),
//	
//	BASE_FLOAT("0"), 
//	FLOAT("null"),
	
	
	BASE_BOOL("false"), 
	BOOL("null"),
	
	STRING("null"), 
	
	ENUM("null"),
	
	MESSAGE("null"),
	
	BYTE_STRING("null"),
		
	;
	
	private String value;

	private DefaultValue(String value) {
		this.value = value;
	}

	public String getValue(CtClass ctClass) {
		return value;
	}
	
}
