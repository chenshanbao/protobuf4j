/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

import com.game37.protobuf4j.proto.utils.enums.TypeWrapperEnum;

import javassist.ClassPool;
import javassist.CtClass;

/**
* author chenshanbao
**/

public class MapDesc {
	
	private String keyClassName;
	
	private String valueClassName;
	
	private CtClass keyClass;
	
	private CtClass valueClass;
	
	private TypeWrapperEnum keyTypeWrapper;
	
	private TypeWrapperEnum valueTypeWrapper;

	public MapDesc(String keyClassName, String valueClassName) {
		super();
		this.keyClassName = keyClassName;
		this.valueClassName = valueClassName;
		try {
			this.keyClass = ClassPool.getDefault().get(keyClassName);
			this.valueClass = ClassPool.getDefault().get(valueClassName);
			this.keyTypeWrapper = TypeUtil.getTypeWrapperEnum(this.keyClass );
			this.valueTypeWrapper = TypeUtil.getTypeWrapperEnum(this.valueClass);
		} catch (Exception e) {
			// 
			e.printStackTrace();
			throw new RuntimeException(keyClass+" or "+valueClass+" is not support type!");
		}
	}


	
	
	public String getKeyClassName() {
		return keyClassName;
	}




	public String getValueClassName() {
		return valueClassName;
	}




	public CtClass getKeyClass() {
		return keyClass;
	}




	public CtClass getValueClass() {
		return valueClass;
	}




	public TypeWrapperEnum getKeyTypeWrapper() {
		return keyTypeWrapper;
	}




	public TypeWrapperEnum getValueTypeWrapper() {
		return valueTypeWrapper;
	}

}
