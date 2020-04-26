/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import javassist.CtClass;

/**
* author chenshanbao
**/

public enum EncodeMethod {
	
	BASE_INT("writeInt32"), 
	INT("writeInt32"),
	
	BASE_LONG("writeInt64"), 
	LONG("writeInt64"),	
	
	BASE_DOUBLE("writeDouble"), 
	DOUBLE("writeDouble"),
	
	BASE_FLOAT("writeFloat"), 
	FLOAT("writeFloat"),
	
	BASE_BOOL("writeBool"), 
	BOOL("writeBool"),
	
	STRING("writeString"), 
	
	ENUM("writeEnum"),
	
	MESSAGE(null),
	
	BYTE_STRING("writeByteArray"),
		
	;
	
	private String value;

	private EncodeMethod(String value) {
		this.value = value;
	}

	public String getValue(CtClass ctClass) {
		return value;
	}
	
}
