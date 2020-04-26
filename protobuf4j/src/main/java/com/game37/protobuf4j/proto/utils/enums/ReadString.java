/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.TypeUtil;

import javassist.CtClass;

/**
* author chenshanbao
**/

public enum ReadString {
	
	BASE_INT("input.readInt32()"), 
	INT("input.readInt32()"),
	
	BASE_LONG("input.readInt64()"), 
	LONG("input.readInt64()"),	
	
	BASE_DOUBLE("input.readDouble()"), 
	DOUBLE("input.readDouble()"),
	
	BASE_FLOAT("input.readFloat()"), 
	FLOAT("input.readFloat()"),
	
	BASE_BOOL("input.readBool()"), 
	BOOL("input.readBool()"),
	
	STRING("input.readString()"), 
	
	ENUM(null),
	
	MESSAGE(".readFrom(input)"),
	
	BYTE_STRING("input.readByteArray()"),
		
	;
	
	private String value;

	private ReadString(String value) {
		this.value = value;
	}

	public String getValue(CtClass ctClass) {
		if(this.equals(MESSAGE)){
			return  ctClass.getName()+this.value;
		}			
		if(this.equals(ENUM)){
			if(TypeUtil.isExistMethod(ctClass, "value")) {
	    		 return ctClass.getName()+".valueToEnum(input.readEnum())";
	    	}else {
	    		 return ctClass.getName()+".values()[input.readEnum()]";
	    	}
		}
		return value;
	}
	
}
