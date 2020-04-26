/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import javassist.CtClass;

/**
* author chenshanbao
**/

public enum NeedGenePreCode {
	
	BASE_INT(true), 
	INT(true),
	
	BASE_LONG(true), 
	LONG(true),	
	
	BASE_DOUBLE(true), 
	DOUBLE(true),
	
	BASE_FLOAT(true), 
	FLOAT(true),
	
	BASE_BOOL(true), 
	BOOL(true),
	
	STRING(false), 
	
	ENUM(false),
	
	MESSAGE(false),
	
	BYTE_STRING(false),
		
	;
	
	private Boolean value;

	private NeedGenePreCode(Boolean value) {
		this.value = value;
	}

	public Boolean getValue(CtClass ctClass) {
		return value;
	}
	
}
