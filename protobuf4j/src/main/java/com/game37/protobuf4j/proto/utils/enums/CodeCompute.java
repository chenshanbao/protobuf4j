/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.CodeGenerator;
import com.game37.protobuf4j.proto.utils.MapDesc;
import com.game37.protobuf4j.proto.utils.TypeUtil;
import com.google.protobuf.WireFormat.FieldType;

import javassist.CtClass;

/**
* author chenshanbao
**/

public enum CodeCompute {
	
	BASE_INT, 
	INT,
	
	BASE_LONG, 
	LONG,
	
	BASE_DOUBLE, 
	DOUBLE,
	
	BASE_FLOAT, 
	FLOAT,
		
	BASE_BOOL{

		@Override
		public String getMethodSimple(Integer fieldNumber, String className, String fieldName, CtClass field,
				TypeWrapperEnum typeWrapperEnum) {
			String computeMethod = typeWrapperEnum.getComputeSizeMethod().getValue(field);
			String tempTpl = CodeGenerator.COMPUTE_TPL_SIMPLE_NO_IF;
			tempTpl = tempTpl
						.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
						.replace(CodeGenerator.__computeMethod__, computeMethod)
						.replace(CodeGenerator.__fieldName2__, fieldName);
			return tempTpl;
		}
		
	}, 
	BOOL,
	
	
	STRING{
		
		@Override
		public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum,
					CodeGenerator.COMPUTE_TPL_SIMPLE_LIST
					.replace(CodeGenerator.__stringToBytesMethod__, "com.google.protobuf.ByteString.copyFromUtf8"));
		};
		@Override
		public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, 
					CodeGenerator.COMPUTE_TPL_SIMPLE_COLLECTION
					.replace(CodeGenerator.__stringToBytesMethod__, "com.google.protobuf.ByteString.copyFromUtf8"));
		};
		
	}, 
	
	ENUM{
		@Override
		public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.COMPUTE_TPL_SIMPLE_LIST);
		};
		@Override
		public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.COMPUTE_TPL_SIMPLE_COLLECTION);
		};
	},
	
	MESSAGE{
		@Override
		public String getMethodSimple(Integer fieldNumber, String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
			// 			
			String tempTpl = CodeGenerator.COMPUTE_TPL_MESSAGE	
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replaceAll(CodeGenerator.__fieldName__, fieldName);
			return tempTpl;
		}

		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl = CodeGenerator.COMPUTE_TPL_MESSAGE_LIST							
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__elementFullClass__, className);
			return tempTpl;
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl = CodeGenerator.COMPUTE_TPL_MESSAGE_COLLECTION							
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__elementFullClass__, className);
			return tempTpl;
		}
	},
	
	BYTE_STRING{
		@Override
		public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.COMPUTE_TPL_SIMPLE_LIST);
		};
		@Override
		public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.COMPUTE_TPL_SIMPLE_COLLECTION);
		};
	},
	
	MAP,
	
	;
			
//	final String TEMPLATE_IF = "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
//			"		__writeMethod__\r\n" + 
//			"	}\r\n";
//	
//	final String TEMPLATE_WRITE_STRING = 
//			"output.writeBytes(__fieldNumber__, com.google.protobuf.ByteString.copyFromUtf8((java.lang.String)__fieldName__));";



	public String getMethodSimple(Integer fieldNumber ,String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
		
		//
//		String writeMethod = typeWrapperEnum.getEncodeMethod().getValue(field);
//		String changeToBaseMemthod = typeWrapperEnum.getToBaseTypeMethod().getValue(field);
//		String tempTpl = null;
//		if(changeToBaseMemthod != null) {
//			tempTpl = CodeGenerator.ENCODE_TPL_SIMPLE_WRAPPER								
//					.replace(CodeGenerator.__elementFullClass__, field.getName())
//					.replace(CodeGenerator.__changeToBaseMemthod__, changeToBaseMemthod);						
//		} else {
//			tempTpl = CodeGenerator.ENCODE_TPL_SIMPLE;
//		}
//
//		tempTpl = tempTpl.replace(CodeGenerator.__fieldDefaultValue__, typeWrapperEnum.getDefaultValue().getValue(field))
//				.replace(CodeGenerator.__fieldName__, fieldName)
//				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
//				.replace(CodeGenerator.__writeMethod__, writeMethod);		
		
		
		String computeMethod = typeWrapperEnum.getComputeSizeMethod().getValue(field);
		String  changeToBaseMemthod = typeWrapperEnum.getToBaseTypeMethod().getValue(field);
		boolean isWrapper = changeToBaseMemthod!=null;
		String tempTpl = isWrapper?CodeGenerator.COMPUTE_TPL_SIMPLE_WRAPPER:
											CodeGenerator.COMPUTE_TPL_SIMPLE;
				//.replace(__fieldDefaultValue__, TypeUtil.getDefaultValueString(ft))
		tempTpl = tempTpl.replace(CodeGenerator.__fieldDefaultValue__, typeWrapperEnum.getDefaultValue().getValue(field))
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__computeMethod__, computeMethod);
		if(isWrapper) {
			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthod__, changeToBaseMemthod);
		}
							
		if(TypeWrapperEnum.ENUM.equals(typeWrapperEnum)) {						
			if(TypeUtil.isExistMethod(field, "value")) {
				tempTpl = tempTpl.replace(CodeGenerator.__fieldName2__, fieldName);
			}else {	
				tempTpl = tempTpl.replace(CodeGenerator.__fieldName2__, fieldName);
			}
		}else {						
			tempTpl = tempTpl.replace(CodeGenerator.__fieldName2__, fieldName);
		}
		
		//
		if(TypeWrapperEnum.STRING.equals(typeWrapperEnum)) {
			tempTpl = tempTpl.replace(CodeGenerator.__stringToBytesMethod__, "com.google.protobuf.ByteString.copyFromUtf8");
		}else {
			tempTpl = tempTpl.replace(CodeGenerator.__stringToBytesMethod__, "");
		}
		return tempTpl;
	};
	
	
	public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
		
		//
		return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.COMPUTE_TPL_SIMPLE_LIST_PACKABLE);
	};
	
	public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
		
		//
		return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.COMPUTE_TPL_SIMPLE_COLLECTION_PACKABLE);
	};
	
	
	public String getMethodCollectionInner(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum
			,String tpl) {
		
		//枚举在集合中使用整形计算；
		String __computeMethod__ = null;
		if(TypeWrapperEnum.ENUM.equals(typeWrapperEnum)) {
			__computeMethod__ = TypeWrapperEnum.BASE_INT.getComputeSizeMethod().getValue(elementCtClass);
		}else {
			__computeMethod__ = typeWrapperEnum.getComputeSizeMethod().getValue(elementCtClass);
		}
		//String computeMethod = typeWrapperEnum.getComputeSizeMethod().getValue(elementCtClass);
		//String changeToBaseMemthod = TypeUtil.getToBaseTypeMethod(elementType, elementCtClass);	
		String changeToBaseMemthod = typeWrapperEnum.getToBaseTypeMethod().getValue(elementCtClass);
		String tempTpl = tpl							
				.replaceAll(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__computeMethod__, __computeMethod__)
				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
				.replace(CodeGenerator.__elementFullClass__, elementCtClass.getName());
		//System.out.println(computeMethod+"========="+elementFullClass+"==="+changeToBaseMemthod);
		if(changeToBaseMemthod!=null) {
			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthod__, changeToBaseMemthod);
		}else {
			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthodRemove__, "");
		}
		
//		//
//		String writeMethod = typeWrapperEnum.getEncodeMethod().getValue(elementCtClass);
//		String changeToBaseMemthod = typeWrapperEnum.getToBaseTypeMethod().getValue(elementCtClass);					
//		String tempTpl = tpl							
//				.replaceAll(CodeGenerator.__fieldName__, fieldName)
//				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
//				.replace(CodeGenerator.__writeMethod__, writeMethod)
//				.replace(CodeGenerator.__elementFullClass__, elementCtClass.getName())
//				.replace(CodeGenerator.__computeMethod__, typeWrapperEnum.getComputeSizeMethod().getValue(elementCtClass))
//				;
//		if(changeToBaseMemthod!=null) {
//			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthod__, changeToBaseMemthod);
//		}else {
//			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthodRemove__, "");
//		}
		tempTpl = tempTpl.replace(CodeGenerator.__stringToBytesMethod__, "");
		return tempTpl;
	};
	
	
	public String getMethodMap(Integer fieldNumber , String fieldName, MapDesc mapDesc) {
		
		//
		String tempTpl = CodeGenerator.COMPUTE_TPL_MAP							
				.replaceAll(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber));
			
		tempTpl = tempTpl.replace(CodeGenerator.__computeMapSubTpl__, 
						getComputeMapTpl(fieldNumber, fieldName, mapDesc));		
		return tempTpl;
	};
	
	public String getComputeMapTpl(Integer fieldNumber , String fieldName, MapDesc mapDesc) {
		String __computeMapSubTpl__ = null;	
		{
			//key
			String __fieldNumber__ = "1";
			String __fieldName__ = "(("+mapDesc.getKeyClassName()+")entry.getKey())";
			String keyTpl = getMethodMapInner(__fieldNumber__, __fieldName__,
									mapDesc.getKeyTypeWrapper(), mapDesc.getKeyClass());
			__computeMapSubTpl__ = keyTpl;
			
		}
		{
			//value
			String __fieldNumber__ = "2";
			String __fieldName__ = "(("+mapDesc.getValueClassName()+")entry.getValue())";
			String valueTpl = getMethodMapInner(__fieldNumber__, __fieldName__,
									mapDesc.getValueTypeWrapper(), mapDesc.getValueClass());
			__computeMapSubTpl__ = 
					__computeMapSubTpl__ + "				" + valueTpl;
			
		}
		return __computeMapSubTpl__;
	}

	private String getMethodMapInner(String fieldNumber, String fieldName, TypeWrapperEnum type, CtClass ctClass) {
		String keyTpl  = null;
		if(TypeWrapperEnum.MESSAGE.equals(type)) {
			keyTpl = CodeGenerator.COMPUTE_TPL_MAP_SUB_MESSAGE;	
		}else {
			String __changeToBaseMemthod__ = type.getToBaseTypeMethod()
					.getValue(ctClass);				
			if(__changeToBaseMemthod__ == null) {
				keyTpl = CodeGenerator.COMPUTE_TPL_MAP_SUB_SIMPLE;					
			}else {
				keyTpl = CodeGenerator.COMPUTE_TPL_MAP_SUB_SIMPLE_WRAPPER
						.replace(CodeGenerator.__changeToBaseMemthod__, __changeToBaseMemthod__);
			}	
			String __computeMethod__ = type.getComputeSizeMethod().getValue(ctClass);
			keyTpl = keyTpl.replace(CodeGenerator.__computeMethod__, __computeMethod__);
			//
			if(TypeWrapperEnum.STRING.equals(type)) {
				keyTpl = keyTpl.replace(CodeGenerator.__stringToBytesMethod__, "com.google.protobuf.ByteString.copyFromUtf8");
			}else {
				keyTpl = keyTpl.replace(CodeGenerator.__stringToBytesMethod__, "");
			}
			
		}
		keyTpl = keyTpl.replace(CodeGenerator.__fieldNumber__, fieldNumber)
						.replace(CodeGenerator.__fieldName__, fieldName);
		//System.out.println("-----------------"+keyTpl);
		return keyTpl;
	}

}
