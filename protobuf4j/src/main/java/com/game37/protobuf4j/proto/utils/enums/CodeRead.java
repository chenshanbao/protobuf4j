/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.CodeGenerator;
import com.game37.protobuf4j.proto.utils.MapDesc;
import com.game37.protobuf4j.proto.utils.WireFormatCopy;

import javassist.CtClass;

/**
* author chenshanbao
* create time 2020年3月6日 下午8:12:44
**/

public enum CodeRead {
	
	BASE_INT, 
	INT,
	
	BASE_LONG, 
	LONG,
	
	BASE_DOUBLE, 
	DOUBLE,
	
	BASE_FLOAT, 
	FLOAT,
		
	BASE_BOOL, 
	BOOL,
	
	
	STRING{
		

		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO
					,CodeGenerator.DECODE_TPL_SET_SIMPLE);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_LIST);
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO
					,CodeGenerator.DECODE_TPL_SET_SIMPLE);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_HASH_SET);
		}
		
	}, 
	
	ENUM{
		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO
					,CodeGenerator.DECODE_TPL_SET_SIMPLE);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_LIST);
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO
					,CodeGenerator.DECODE_TPL_SET_SIMPLE);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_HASH_SET);
		}
	},
	
	MESSAGE{
		@Override
		public String getMethodSimple(Integer fieldNumber, String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {			
			
			int tag = WireFormatCopy.makeTag(fieldNumber, typeWrapperEnum.getFieldType().getWireType());
			String readString = typeWrapperEnum.getReadStringPackage().getValue(field);
			String tempTpl = CodeGenerator.DECODE_TPL_MESSAGE	
					.replace(CodeGenerator.__tagNumber__, String.valueOf(tag))
					.replace(CodeGenerator.__fieldName__, fieldName)
					.replace(CodeGenerator.__readStringPackage__, readString);
			return tempTpl;
		}

		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_LIST);
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_HASH_SET);
		}
	},
	
	BYTE_STRING{
		@Override
		public String getMethodList(Integer fieldNumber, String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			// 
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_LIST);
		}

		@Override
		public String getMethodCollection(Integer fieldNumber, String className, String fieldName,
				CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
			String tempTpl =  getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABL_NO);
			return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_HASH_SET);
		}
	},
	
	;
			
	final String TEMPLATE_IF = "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
			"		__writeMethod__\r\n" + 
			"	}\r\n";
	
	final String TEMPLATE_WRITE_STRING = 
			"output.writeBytes(__fieldNumber__, com.google.protobuf.ByteString.copyFromUtf8((java.lang.String)__fieldName__));";



	public String getMethodSimple(Integer fieldNumber ,String className, String fieldName, CtClass field, TypeWrapperEnum typeWrapperEnum) {
		
		//			
		int tag = WireFormatCopy.makeTag(fieldNumber, typeWrapperEnum.getFieldType().getWireType());
		String readString = 
				field.getName().length() < 10 ? typeWrapperEnum.getReadString().getValue(field)
				: typeWrapperEnum.getReadStringPackage().getValue(field);
		String tempTpl = CodeGenerator.DECODE_TPL_SIMPLE	
				.replace(CodeGenerator.__tagNumber__, String.valueOf(tag))
				.replace(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__readStringPackage__, readString);
		return tempTpl;
	};
	
	
	
//	"__readListSubTpl__"+
	final String TEMPLATE_READ_LIST_PACKABLE = 	
	"\t\t\t\t		while (input.getBytesUntilLimit() > 0) {\r\n" + 
	"\t\t\t\t			out.__fieldName__.add(__readStringPackage__);\r\n" + 
	"\t\t\t\t		}\r\n" ; 
	
	final String TEMPLATE_READ_LIST_PACKABL_NO =
	"\t\t\t\t		out.__fieldName__.add(__readStringPackage__);\r\n";
	
	
	
	
	public String getMethodList(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
						
		String tempTpl = getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABLE);		
		return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_LIST);
	};
	
	protected String getMethodCollInner(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum
			, String tpl, String tplSet) {	
		//
		//int tag = WireFormatCopy.makeTag(fieldNumber, typeWrapperEnum.getFieldType().getWireType());
		int tag = TypeWrapperEnum.ENUM.equals(typeWrapperEnum)?
				WireFormatCopy.makeTag(fieldNumber, typeWrapperEnum.getFieldType().getWireType())
				:WireFormatCopy.makeTag(fieldNumber,TypeWrapperEnum.MESSAGE.getFieldType().getWireType());
				
		String readString = typeWrapperEnum.getReadStringPackage().getValue(elementCtClass);
		String tempTpl =tplSet	
				.replace(CodeGenerator.__tagNumber__, String.valueOf(tag))
				.replace(CodeGenerator.__fieldName__, fieldName)
				;
		
		//packable collections;
		String __readListSubTpl__ = tpl
				.replace(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__readStringPackage__, readString);
		
		String out =  tempTpl.replace(CodeGenerator.__readListSubTpl__, __readListSubTpl__);
		//System.out.println(out);
		return out;
	}
	
	protected String getMethodCollInner(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum
			, String tpl) {
		return getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, tpl, CodeGenerator.DECODE_TPL_SET);
	}
	
	
	public String getMethodCollection(Integer fieldNumber ,String className, String fieldName, CtClass elementCtClass, TypeWrapperEnum typeWrapperEnum) {
		
		String tempTpl = getMethodCollInner(fieldNumber, className, fieldName, elementCtClass, typeWrapperEnum, TEMPLATE_READ_LIST_PACKABLE);		
		return tempTpl.replace(CodeGenerator.__newSetFullClass__, CodeGenerator.NEW_HASH_SET);
	};

	
	
	public String getMethodMap(Integer fieldNumber , String fieldName, MapDesc mapDesc) {
		
		//sub simple or message;
		String __decodeMapSubTpl__ = null;	
		{
			//key
			String __fieldNumber__ = "1";
			String keyTpl = getMethodMapInner(__fieldNumber__, null,
					mapDesc.getKeyTypeWrapper(), mapDesc.getKeyClass());
			__decodeMapSubTpl__ = keyTpl;			
		}
		{
			//value
			String __fieldNumber__ = "2";
			String valueTpl = getMethodMapInner(__fieldNumber__, null,
					mapDesc.getValueTypeWrapper(), mapDesc.getValueClass());
			__decodeMapSubTpl__ = 
					__decodeMapSubTpl__ + "				" + valueTpl;		
		}
		
		//map sub
		String __decodeMapSub__ = 
			CodeGenerator.getTplString(CodeGenerator.DECODE_TPL_MAP_SUB)
			.replace(CodeGenerator.__decodeMapSubTpl__, __decodeMapSubTpl__);	
						
		//map
		int tag = WireFormatCopy.makeTag(fieldNumber, 
						TypeWrapperEnum.MESSAGE.getFieldType().getWireType());				
		String tempTpl = CodeGenerator.getTplString(CodeGenerator.DECODE_TPL_MAP)
				.replace(CodeGenerator.__tagNumber__, String.valueOf(tag))
				.replace(CodeGenerator.__fieldName__, fieldName)
				.replace(CodeGenerator.__decodeMapSubKeyClass__, mapDesc.getKeyClassName())
				.replace(CodeGenerator.__decodeMapSubValueClass__, mapDesc.getValueClassName())
				;
														
		tempTpl = "				"+tempTpl.replace(CodeGenerator.__decodeMapSub__, __decodeMapSub__);		
		return tempTpl;
	};
	

	private String getMethodMapInner(String fieldNumber, String fieldName2, TypeWrapperEnum type, CtClass ctClass) {
		String keyTpl  = null;
		int tag = WireFormatCopy.makeTag(Integer.valueOf(fieldNumber), 
				type.getFieldType().getWireType());	
		
		if(TypeWrapperEnum.MESSAGE.equals(type)) {
			keyTpl = CodeGenerator.getTplString(CodeGenerator.DECODE_TPL_MAP_SUB_MESSAGE);	
		}else {
			keyTpl = CodeGenerator.getTplString(CodeGenerator.DECODE_TPL_MAP_SUB_SIMPLE);
		}
		String __readStringPackage__ = type.getReadStringPackage().getValue(ctClass);
		keyTpl = keyTpl
					.replace(CodeGenerator.__tagNumber__, String.valueOf(tag))
					.replace(CodeGenerator.__fieldNumber__, fieldNumber)
					.replace(CodeGenerator.__readStringPackage__, __readStringPackage__);
		
		return keyTpl;
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(WireFormatCopy.makeTag(1,TypeWrapperEnum.INT.getFieldType().getWireType()));
	}
	
}
