/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.TypeUtil;

import javassist.CtClass;

/**
* author chenshanbao
**/

public enum ReadStringPackage {
	
	BASE_INT("Integer.valueOf(input.readInt32())"), 
	INT("Integer.valueOf(input.readInt32())"),
	
	BASE_LONG("Long.valueOf(input.readInt64())"), 
	LONG("Long.valueOf(input.readInt64())"),	
	
	BASE_DOUBLE("Double.valueOf(input.readDouble())"), 
	DOUBLE("Double.valueOf(input.readDouble())"),
	
	BASE_FLOAT("Float.valueOf(input.readFloat())"), 
	FLOAT("Float.valueOf(input.readFloat())"),
	
	BASE_BOOL("Boolean.valueOf(input.readBool())"), 
	BOOL("Boolean.valueOf(input.readBool())"),
	
	STRING("input.readString()"), 
	
	ENUM(null),
	
	MESSAGE(".readFrom(input)"),
		
	BYTE_STRING("input.readByteArray()"),
	;
	
	private String value;

	private ReadStringPackage(String value) {
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
