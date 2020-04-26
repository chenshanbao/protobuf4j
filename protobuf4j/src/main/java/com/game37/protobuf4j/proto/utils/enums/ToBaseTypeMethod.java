/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.TypeUtil;

import javassist.CtClass;

/**
* author chenshanbao
* create time 2020年3月5日 上午9:57:09
**/

public enum ToBaseTypeMethod {
	
	BASE_INT(null), 
	INT("intValue()"),
	
	BASE_LONG(null), 
	LONG("longValue()"),	
	
	BASE_DOUBLE(null), 
	DOUBLE("doubleValue()"),
	
	BASE_FLOAT(null), 
	FLOAT("floatValue()"),
	
	BASE_BOOL(null), 
	BOOL("booleanValue()"),
	
	STRING(null), 
	
	ENUM("ordinal()"),
	
	MESSAGE(null),
	
	BYTE_STRING(null),
		
	;
	
	private String value;

	private ToBaseTypeMethod(String value) {
		this.value = value;
	}

	public String getValue(CtClass ctClass) {
		if(this.equals(ENUM) &&
				TypeUtil.isExistMethod(ctClass, "value")) {
			return "value()";
		}
		return value;
	}
	
}
