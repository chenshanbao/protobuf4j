/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.CodeGenerator;
import com.game37.protobuf4j.proto.utils.MapDesc;

import javassist.CtClass;

/**
* author chenshanbao
* create time 2020年3月6日 下午8:12:44
**/

public enum CodeWrite {
	
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
		public String getMethodSimple(Integer fieldNumber ,String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
			
			//
			String writeMethod = typeWrapperEnum.getEncodeMethod().getValue(field);
			String tempTpl = CodeGenerator.ENCODE_TPL_SIMPLE_NO_IF
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__writeMethod__, writeMethod);																	
			return tempTpl;
		};
	}, 
	BOOL,
	
	
	STRING{
		

		@Override
		public String getMethodSimple(Integer fieldNumber, String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
			// 
			String writeMethod = TEMPLATE_WRITE_STRING
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__fieldName__, fieldName);
			
			return TEMPLATE_IF.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldDefaultValue__, TypeWrapperEnum.STRING.getDefaultValue().getValue(field))
					.replace(CodeGenerator.__writeMethod__, writeMethod);
		}

		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String writeMethod = TEMPLATE_WRITE_STRING
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__fieldName__, fieldName+".get(i)");
			
			return CodeGenerator.ENCODE_TPL_SIMPLE_LIST_2
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__writeMethod__, writeMethod);
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			String writeMethod = TEMPLATE_WRITE_STRING
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__fieldName__, "("+className+")it.next()");
			
			return CodeGenerator.ENCODE_TPL_SIMPLE_COLLECTION_2
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__writeMethod__, writeMethod);
		}
		
	}, 
	
	ENUM{
		@Override
		public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.ENCODE_TPL_SIMPLE_LIST);
		};
		@Override
		public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.ENCODE_TPL_SIMPLE_COLLECTION);
		};
	},
	
	MESSAGE{
		@Override
		public String getMethodSimple(Integer fieldNumber, String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl = CodeGenerator.ENCODE_TPL_MESSAGE							
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__elementFullClass__, className);
			return tempTpl;
		}

		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl = CodeGenerator.ENCODE_TPL_MESSAGE_LIST
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
					.replace(CodeGenerator.__elementFullClass__, className);
			return tempTpl;
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl = CodeGenerator.ENCODE_TPL_MESSAGE_COLLECTION
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
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.ENCODE_TPL_SIMPLE_LIST);
		};
		@Override
		public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			
			//
			return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.ENCODE_TPL_SIMPLE_COLLECTION);
		};
	},
	
	MAP,
	
	;
			
	final String TEMPLATE_IF = "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
			"		__writeMethod__\r\n" + 
			"	}\r\n";
	
	final String TEMPLATE_WRITE_STRING = 
			"output.writeBytes(__fieldNumber__, com.google.protobuf.ByteString.copyFromUtf8((java.lang.String)__fieldName__));";



	public String getMethodSimple(Integer fieldNumber ,String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
		
		//
		String writeMethod = typeWrapperEnum.getEncodeMethod().getValue(field);
		String changeToBaseMemthod = typeWrapperEnum.getToBaseTypeMethod().getValue(field);
		String tempTpl = null;
		if(changeToBaseMemthod != null) {
			tempTpl = CodeGenerator.ENCODE_TPL_SIMPLE_WRAPPER								
					.replace(CodeGenerator.__elementFullClass__, field.getName())
					.replace(CodeGenerator.__changeToBaseMemthod__, changeToBaseMemthod);						
		} else {
			tempTpl = CodeGenerator.ENCODE_TPL_SIMPLE;
		}

		tempTpl = tempTpl.replace(CodeGenerator.__fieldDefaultValue__, typeWrapperEnum.getDefaultValue().getValue(field))
				.replace(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
				.replace(CodeGenerator.__writeMethod__, writeMethod);																	
		return tempTpl;
	};
	
	
	public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
		
		//
		return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.ENCODE_TPL_SIMPLE_LIST_PACKABLE);
	};
	
	public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
		
		//
		return getMethodCollectionInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, CodeGenerator.ENCODE_TPL_SIMPLE_COLLECTION_PACKABLE);
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
		String writeMethod = typeWrapperEnum.getEncodeMethod().getValue(elementCtClass);
		String changeToBaseMemthod = typeWrapperEnum.getToBaseTypeMethod().getValue(elementCtClass);					
		String tempTpl = tpl							
				.replaceAll(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber))
				.replace(CodeGenerator.__writeMethod__, writeMethod)
				.replace(CodeGenerator.__elementFullClass__, elementCtClass.getName())
				.replace(CodeGenerator.__computeMethod__, __computeMethod__)
				;
		if(changeToBaseMemthod!=null) {
			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthod__, changeToBaseMemthod);
		}else {
			tempTpl = tempTpl.replace(CodeGenerator.__changeToBaseMemthodRemove__, "");
		}
		return tempTpl;
	};
	
	
	
	public String getMethodMap(Integer fieldNumber , String fieldName, MapDesc mapDesc) {
		
		//
		String tempTpl = CodeGenerator.ENCODE_TPL_MAP							
				.replaceAll(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__fieldNumber__, String.valueOf(fieldNumber));
		
		tempTpl = tempTpl.replace(CodeGenerator.__computeMapSubTpl__,
							CodeCompute.MAP.getComputeMapTpl(fieldNumber, fieldName, mapDesc));
						
		String __writeMapSubTpl__ = null;	
		{
			//key
			String __fieldNumber__ = "1";
			String __fieldName__ = "(("+mapDesc.getKeyClassName()+")entry.getKey())";
			String keyTpl = getMethodMapInner(__fieldNumber__, __fieldName__,
									mapDesc.getKeyTypeWrapper(), mapDesc.getKeyClass());
			__writeMapSubTpl__ = keyTpl;
			
		}
		{
			//value
			String __fieldNumber__ = "2";
			String __fieldName__ = "(("+mapDesc.getValueClassName()+")entry.getValue())";
			String valueTpl = getMethodMapInner(__fieldNumber__, __fieldName__,
									mapDesc.getValueTypeWrapper(), mapDesc.getValueClass());
			__writeMapSubTpl__ = 
					__writeMapSubTpl__ + "				" + valueTpl;
			
		}
		
		tempTpl = tempTpl.replace(CodeGenerator.__writeMapSubTpl__, __writeMapSubTpl__);		
		return tempTpl;
	};
	

	private String getMethodMapInner(String fieldNumber, String fieldName, TypeWrapperEnum type, CtClass ctClass) {
		String keyTpl  = null;
		if(TypeWrapperEnum.MESSAGE.equals(type)) {
			keyTpl = CodeGenerator.ENCODE_TPL_MAP_SUB_MESSAGE;	
		}else {
			String __changeToBaseMemthod__ = type.getToBaseTypeMethod()
					.getValue(ctClass);				
			if(__changeToBaseMemthod__ == null) {
				keyTpl = CodeGenerator.ENCODE_TPL_MAP_SUB_SIMPLE;					
			}else {
				keyTpl = CodeGenerator.ENCODE_TPL_MAP_SUB_SIMPLE_WRAPPER
						.replace(CodeGenerator.__changeToBaseMemthod__, __changeToBaseMemthod__);
			}	
			String __writeMethod__ = type.getEncodeMethod().getValue(ctClass);
			keyTpl = keyTpl.replace(CodeGenerator.__writeMethod__, __writeMethod__);
		}
		keyTpl = keyTpl.replace(CodeGenerator.__fieldNumber__, fieldNumber)
						.replace(CodeGenerator.__fieldName__, fieldName);
		//System.out.println("-----------------"+keyTpl);
		return keyTpl;
	}
	

}
