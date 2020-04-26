/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

import java.util.HashMap;
import java.util.Map;

import com.game37.protobuf4j.proto.utils.enums.TypeWrapperEnum;
import com.google.protobuf.WireFormat;

import javassist.CtClass;
import javassist.NotFoundException;

/**
* author chenshanbao
**/

public class TypeUtil {
	
	private static final Map<String,WireFormat.FieldType> typeCahce = 
								new HashMap<String,WireFormat.FieldType>(){{
									
		put(boolean.class.getName(),WireFormat.FieldType.BOOL);
		put(Boolean.class.getName(),WireFormat.FieldType.BOOL);
		
		put(double.class.getName(),WireFormat.FieldType.DOUBLE);
		put(Double.class.getName(),WireFormat.FieldType.DOUBLE);
		
		put(float.class.getName(),WireFormat.FieldType.FLOAT);
		put(Float.class.getName(),WireFormat.FieldType.FLOAT);
		
		put(int.class.getName(),WireFormat.FieldType.INT32);
		put(Integer.class.getName(),WireFormat.FieldType.INT32);
		
		put(long.class.getName(),WireFormat.FieldType.INT64);
		put(Long.class.getName(),WireFormat.FieldType.INT64);
				
		put(byte[].class.getName(),WireFormat.FieldType.BYTES);
		put(String.class.getName(),WireFormat.FieldType.STRING);
				
	}};
	
	private static final Map<String,TypeWrapperEnum> typeWraperCache = 
			new HashMap<String,TypeWrapperEnum>(){{
				
		put(boolean.class.getName(),TypeWrapperEnum.BASE_BOOL);
		put(Boolean.class.getName(),TypeWrapperEnum.BOOL);
		
		put(double.class.getName(),TypeWrapperEnum.BASE_DOUBLE);
		put(Double.class.getName(),TypeWrapperEnum.DOUBLE);
		
		put(float.class.getName(),TypeWrapperEnum.BASE_FLOAT);
		put(Float.class.getName(),TypeWrapperEnum.FLOAT);
		
		put(int.class.getName(),TypeWrapperEnum.BASE_INT);
		put(Integer.class.getName(),TypeWrapperEnum.INT);
		
		put(long.class.getName(),TypeWrapperEnum.BASE_LONG);
		put(Long.class.getName(),TypeWrapperEnum.LONG);
		
		put(String.class.getName(),TypeWrapperEnum.STRING);
		
		put("byte[]",TypeWrapperEnum.BYTE_STRING);
		
	}};

	
	public static TypeWrapperEnum getTypeWrapperEnum(CtClass ctClass) throws NotFoundException{
		
		TypeWrapperEnum out = typeWraperCache.get(ctClass.getName());
//		System.out.println("----"+ctClass.getName());
//		System.out.println("----"+out);
		if(out == null) {
			if(ctClass.subclassOf(CodeGenerator.getEnumCtClass())) {
				return TypeWrapperEnum.ENUM;
			}else if(ctClass.subclassOf(CodeGenerator.getMapCtClass())) {
				return TypeWrapperEnum.MAP;
			}else {
				return TypeWrapperEnum.MESSAGE;
			}
		}
		return out;
	}
	
	public static boolean isExistMethod(CtClass ctClass, String methodName) {			
		try {
			return ctClass.getDeclaredMethod(methodName) != null;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static boolean isExistMethod(CtClass ctClass, String methodName, CtClass[] args) {			
		try {
			return ctClass.getDeclaredMethod(methodName, args) != null;
		} catch (Exception e) {
		}
		return false;
	}
	
	public static String getToBaseTypeMethod(WireFormat.FieldType type, CtClass ctClass) {
		if(ctClass.getName().length()<10) {
			return null;
		}
		switch (type) {
	      case DOUBLE:
	        return "doubleValue()";
	      case FLOAT:
	        return "floatValue()";
	      case INT64:
	        return "longValue()";
	      case UINT64:
	        return "longValue()";
	      case INT32:
	        return "intValue()";
	      case FIXED64:
	        return "longValue()";
	      case FIXED32:
	        return "intValue()";
	      case BOOL:
	        return "booleanValue()";
	      case UINT32:
	        return "intValue()";
	      case SFIXED32:
	        return "intValue()";
	      case SFIXED64:
	        return "longValue()";
	      case SINT32:
	        return "intValue()";
	      case SINT64:
	        return "longValue()";
	      case ENUM:
	    	  return isExistMethod(ctClass, "value")?"value()":"ordinal()";
		}
		return null;
	}
	
	public static String getEncodeMethodString(WireFormat.FieldType type) {
		switch (type) {
	      case DOUBLE:
	        return "writeDouble";
	      case FLOAT:
	        return "writeFloat";
	      case INT64:
	        return "writeInt64";
	      case UINT64:
	        return "writeUInt64";
	      case INT32:
	        return "writeInt32";
	      case FIXED64:
	        return "writeFixed64";
	      case FIXED32:
	        return "writeFixed32";
	      case BOOL:
	        return "writeBool";
	      case BYTES:
	        return "writeByteArray";
	      case UINT32:
	        return "writeUInt32";
	      case SFIXED32:
	        return "writeSFixed32";
	      case SFIXED64:
	        return "writeSFixed64";
	      case SINT32:
	        return "writeSInt32";
	      case SINT64:
	        return "writeSInt64";
	      case STRING:
	    	return "writeString";
	      case ENUM:
	    	  return "writeEnum";
		}
		return null;
	}
	
	public static String getComputeMethodString(WireFormat.FieldType type) {
		switch (type) {
	      case DOUBLE:
	        return "computeDoubleSize";
	      case FLOAT:
	        return "computeFloatSize";
	      case INT64:
	        return "computeInt64Size";
	      case UINT64:
	        return "computeUInt64Size";
	      case INT32:
	        return "computeInt32Size";
	      case FIXED64:
	        return "computeFixed64Size";
	      case FIXED32:
	        return "computeFixed32Size";
	      case BOOL:
	        return "computeBoolSize";
	      case BYTES:
	        return "computeByteArraySize";
	      case UINT32:
	        return "computeUInt32Size";
	      case SFIXED32:
	        return "computeSFixed32Size";
	      case SFIXED64:
	        return "computeSFixed64Size";
	      case SINT32:
	        return "computeSInt32Size";
	      case SINT64:
	        return "computeSInt64Size";
	      case STRING:
	    	return "computeStringSize";
	      case ENUM:
	    	  return "computeEnumSize";
		}
		return null;
	}
	
	public static String getDefaultValueString(WireFormat.FieldType type) {
		
		switch (type) {
	      case DOUBLE:
	        return "0D";
	      case FLOAT:
	        return "0F";
	      case INT64:
	        return "0";
	      case UINT64:
	        return "0";
	      case INT32:
	        return "0";
	      case FIXED64:
	        return "0";
	      case FIXED32:
	        return "0";
	      case BOOL:
	        return "false";
	      case BYTES:
	        return "null";
	      case UINT32:
	        return "0";
	      case SFIXED32:
	        return "0";
	      case SFIXED64:
	        return "0";
	      case SINT32:
	        return "0";
	      case SINT64:
	        return "0";
	      case STRING:
	    	return "null";
	      case ENUM:
	    	  return "null";
		}
		return "null";
	}
	
	public static String getReadString(WireFormat.FieldType type, CtClass ctClass) {
		
		switch (type) {
	      case DOUBLE:
	        return "input.readDouble()";
	      case FLOAT:
	        return "input.readFloat()";
	      case INT64:
	        return "input.readInt64()";
	      case UINT64:
	        return "input.readUInt64()";
	      case INT32:
	        return "input.readInt32()";
	      case FIXED64:
	        return "input.readFixed64()";
	      case FIXED32:
	        return "input.readFixed32()";
	      case BOOL:
	        return "input.readBool()";
	      case BYTES:
	        return "input.readBytes()";
	      case UINT32:
	        return "input.readUInt32()";
	      case SFIXED32:
	        return "input.readSFixed32()";
	      case SFIXED64:
	        return "input.readSFixed64()";
	      case SINT32:
	        return "input.readSInt32()";
	      case SINT64:
	        return "input.readSInt64()";
	      case STRING:
	    	return "input.readString()";
	      case ENUM:{
	    	  if(isExistMethod(ctClass, "value")) {
	    		  return ctClass.getName()+".valueToEnum(input.readEnum())";
	    	  }else {
	    		  return ctClass.getName()+".values()[input.readEnum()]";
	    	  }
	      }	    	  
		}
		return ctClass.getName()+".readFrom(input)";
	}
	
	public static String getReadStringPackage(WireFormat.FieldType type,CtClass ctClass) {
		
		switch (type) {
	      case DOUBLE:
	        return "Double.valueOf(input.readDouble())";
	      case FLOAT:
	        return "Float.valueOf(input.readFloat())";
	      case INT64:
	        return "Long.valueOf(input.readInt64())";
	      case UINT64:
	        return "Long.valueOf(input.readUInt64())";
	      case INT32:
	        return "Integer.valueOf(input.readInt32())";
	      case FIXED64:
	        return "Long.valueOf(input.readFixed64())";
	      case FIXED32:
	        return "Integer.valueOf(input.readFixed32())";
	      case BOOL:
	        return "Boolean.valueOf(input.readBool())";
	      case BYTES:
	        return "input.readBytes()";
	      case UINT32:
	        return "input.readUInt32())";
	      case SFIXED32:
	        return "Integer.valueOf(input.readSFixed32())";
	      case SFIXED64:
	        return "Long.valueOf(input.readSFixed64())";
	      case SINT32:
	        return "Integer.valueOf(input.readSInt32())";
	      case SINT64:
	        return "Long.valueOf(input.readSInt64())";
	      case STRING:
	    	return "input.readString()";
	      case ENUM:{	    	  
	    	  if(isExistMethod(ctClass, "value")) {
	    		  return ctClass.getName()+".valueToEnum(input.readEnum())";
	    	  }else {
	    		  return ctClass.getName()+".values()[input.readEnum()]";
	    	  }
	      }
		}
		return ctClass.getName()+".readFrom(input)";
	}
	
	public static boolean isNeedGeneratorPreCode(WireFormat.FieldType type,CtClass ctClass) {
		
		switch (type) {
	      case DOUBLE:
	        return true;
	      case FLOAT:
	        return true;
	      case INT64:
	        return true;
	      case UINT64:
	        return true;
	      case INT32:
	        return true;
	      case FIXED64:
	        return true;
	      case FIXED32:
	        return true;
	      case BOOL:
	        return true;
	      case BYTES:
	        return false;
	      case UINT32:
	        return true;
	      case SFIXED32:
	        return true;
	      case SFIXED64:
	        return true;
	      case SINT32:
	        return true;
	      case SINT64:
	        return true;
	      case STRING:
	    	return false;
	      case ENUM:
	    	return true;
		}
		return false;
	}
	
}
