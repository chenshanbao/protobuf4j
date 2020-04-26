/**
 * 
 */
package com.game37.protobuf4j.proto.utils.enums;

import com.game37.protobuf4j.proto.utils.WireFormatCopy.FieldType;

/**
* author chenshanbao
* create time 2020年3月5日 上午9:46:30
**/

public enum TypeWrapperEnum {
	
	BASE_INT(ComputeSizeMethod.BASE_INT, DefaultValue.BASE_INT, 
			EncodeMethod.BASE_INT ,NeedGenePreCode.BASE_INT , 
			ReadString.BASE_INT , ReadStringPackage.BASE_INT,
			ToBaseTypeMethod.BASE_INT ,FieldType.INT32
			,CodeWrite.BASE_INT, CodeRead.BASE_INT
			,CodeCompute.BASE_INT),	
	INT(ComputeSizeMethod.INT, DefaultValue.INT, 
			EncodeMethod.INT ,NeedGenePreCode.INT , 
			ReadString.INT , ReadStringPackage.INT,
			ToBaseTypeMethod.INT ,FieldType.INT32
			,CodeWrite.INT, CodeRead.INT
			,CodeCompute.INT),
	
	BASE_LONG(ComputeSizeMethod.BASE_LONG, DefaultValue.BASE_LONG, 
			EncodeMethod.BASE_LONG ,NeedGenePreCode.BASE_LONG , 
			ReadString.BASE_LONG , ReadStringPackage.BASE_LONG,
			ToBaseTypeMethod.BASE_LONG ,FieldType.INT64
			,CodeWrite.BASE_LONG, CodeRead.BASE_LONG
			,CodeCompute.BASE_LONG),
	LONG(ComputeSizeMethod.LONG, DefaultValue.LONG, 
			EncodeMethod.LONG ,NeedGenePreCode.LONG , 
			ReadString.LONG , ReadStringPackage.LONG,
			ToBaseTypeMethod.LONG ,FieldType.INT64
			,CodeWrite.LONG, CodeRead.LONG
			,CodeCompute.LONG),
	
	BASE_DOUBLE(ComputeSizeMethod.BASE_DOUBLE, DefaultValue.BASE_DOUBLE, 
			EncodeMethod.BASE_DOUBLE ,NeedGenePreCode.BASE_DOUBLE , 
			ReadString.BASE_DOUBLE , ReadStringPackage.BASE_DOUBLE,
			ToBaseTypeMethod.BASE_DOUBLE ,FieldType.DOUBLE
			,CodeWrite.BASE_DOUBLE, CodeRead.BASE_DOUBLE
			,CodeCompute.BASE_DOUBLE),
	DOUBLE(ComputeSizeMethod.DOUBLE, DefaultValue.DOUBLE, 
			EncodeMethod.DOUBLE ,NeedGenePreCode.DOUBLE , 
			ReadString.DOUBLE , ReadStringPackage.DOUBLE,
			ToBaseTypeMethod.DOUBLE ,FieldType.DOUBLE
			,CodeWrite.DOUBLE, CodeRead.DOUBLE
			,CodeCompute.DOUBLE),
	
	BASE_FLOAT(ComputeSizeMethod.BASE_FLOAT, DefaultValue.BASE_FLOAT, 
			EncodeMethod.BASE_FLOAT ,NeedGenePreCode.BASE_FLOAT , 
			ReadString.BASE_FLOAT , ReadStringPackage.BASE_FLOAT,
			ToBaseTypeMethod.BASE_FLOAT ,FieldType.FLOAT
			,CodeWrite.BASE_FLOAT, CodeRead.BASE_FLOAT
			,CodeCompute.BASE_FLOAT),
	FLOAT(ComputeSizeMethod.FLOAT, DefaultValue.FLOAT, 
			EncodeMethod.FLOAT ,NeedGenePreCode.FLOAT , 
			ReadString.FLOAT , ReadStringPackage.FLOAT,
			ToBaseTypeMethod.FLOAT ,FieldType.FLOAT
			,CodeWrite.FLOAT, CodeRead.FLOAT
			,CodeCompute.FLOAT),
	
	BASE_BOOL(ComputeSizeMethod.BASE_BOOL, DefaultValue.BASE_BOOL, 
			EncodeMethod.BASE_BOOL ,NeedGenePreCode.BASE_BOOL , 
			ReadString.BASE_BOOL , ReadStringPackage.BASE_BOOL,
			ToBaseTypeMethod.BASE_BOOL ,FieldType.BOOL
			,CodeWrite.BASE_BOOL, CodeRead.BASE_BOOL
			,CodeCompute.BASE_BOOL),
	BOOL(ComputeSizeMethod.BOOL, DefaultValue.BOOL, 
			EncodeMethod.BOOL ,NeedGenePreCode.BOOL , 
			ReadString.BOOL , ReadStringPackage.BOOL,
			ToBaseTypeMethod.BOOL ,FieldType.BOOL
			,CodeWrite.BOOL, CodeRead.BOOL
			,CodeCompute.BOOL),
	
	STRING(ComputeSizeMethod.STRING, DefaultValue.STRING, 
			EncodeMethod.STRING ,NeedGenePreCode.STRING , 
			ReadString.STRING , ReadStringPackage.STRING,
			ToBaseTypeMethod.STRING ,FieldType.STRING
			,CodeWrite.STRING, CodeRead.STRING
			,CodeCompute.STRING),
	MESSAGE(ComputeSizeMethod.MESSAGE, DefaultValue.MESSAGE, 
			EncodeMethod.MESSAGE ,NeedGenePreCode.MESSAGE , 
			ReadString.MESSAGE , ReadStringPackage.MESSAGE,
			ToBaseTypeMethod.MESSAGE ,FieldType.MESSAGE
			,CodeWrite.MESSAGE, CodeRead.MESSAGE
			,CodeCompute.MESSAGE),
	ENUM(ComputeSizeMethod.ENUM, DefaultValue.ENUM, 
			EncodeMethod.ENUM ,NeedGenePreCode.ENUM , 
			ReadString.ENUM , ReadStringPackage.ENUM,
			ToBaseTypeMethod.ENUM ,FieldType.ENUM
			,CodeWrite.ENUM, CodeRead.ENUM
			,CodeCompute.ENUM),		
	
	BYTE_STRING(ComputeSizeMethod.BYTE_STRING, DefaultValue.BYTE_STRING, 
			EncodeMethod.BYTE_STRING ,NeedGenePreCode.BYTE_STRING , 
			ReadString.BYTE_STRING , ReadStringPackage.BYTE_STRING,
			ToBaseTypeMethod.BYTE_STRING ,FieldType.BYTES
			,CodeWrite.BYTE_STRING, CodeRead.BYTE_STRING
			,CodeCompute.BYTE_STRING),	
	
	MAP(ComputeSizeMethod.MESSAGE, DefaultValue.MESSAGE, 
			EncodeMethod.MESSAGE ,NeedGenePreCode.MESSAGE , 
			ReadString.MESSAGE , ReadStringPackage.MESSAGE,
			ToBaseTypeMethod.MESSAGE ,FieldType.MESSAGE
			,CodeWrite.MAP, CodeRead.MESSAGE
			,CodeCompute.MAP),
	
	;
	
	private ComputeSizeMethod computeSizeMethod;
	private DefaultValue defaultValue;
	private EncodeMethod encodeMethod;
	private NeedGenePreCode needGenePreCode;
	private ReadString readString;
	private ReadStringPackage readStringPackage;
	private ToBaseTypeMethod toBaseTypeMethod;
	private FieldType fieldType;
	private CodeWrite codeWrite;
	private CodeRead codeRead;
	private CodeCompute codeCompute;
	
	private TypeWrapperEnum(ComputeSizeMethod computeSizeMethod, DefaultValue defaultValue, EncodeMethod encodeMethod,
			NeedGenePreCode needGenePreCode, ReadString readString, ReadStringPackage readStringPackage,
			ToBaseTypeMethod toBaseTypeMethod, FieldType fieldType,
			CodeWrite codeWrite, CodeRead codeRead,
			CodeCompute codeCompute) {
		this.computeSizeMethod = computeSizeMethod;
		this.defaultValue = defaultValue;
		this.encodeMethod = encodeMethod;
		this.needGenePreCode = needGenePreCode;
		this.readString = readString;
		this.readStringPackage = readStringPackage;
		this.toBaseTypeMethod = toBaseTypeMethod;
		this.fieldType = fieldType;
		this.codeWrite = codeWrite;
		this.codeRead = codeRead;
		this.codeCompute = codeCompute;
	}

	public ComputeSizeMethod getComputeSizeMethod() {
		return computeSizeMethod;
	}

	public DefaultValue getDefaultValue() {
		return defaultValue;
	}

	public EncodeMethod getEncodeMethod() {
		return encodeMethod;
	}

	public NeedGenePreCode getNeedGenePreCode() {
		return needGenePreCode;
	}

	public ReadString getReadString() {
		return readString;
	}

	public ReadStringPackage getReadStringPackage() {
		return readStringPackage;
	}

	public ToBaseTypeMethod getToBaseTypeMethod() {
		return toBaseTypeMethod;
	}

	public FieldType getFieldType() {
		return fieldType;
	}
	
	public CodeWrite getCodeWrite() {
		return codeWrite;
	}

	public CodeRead getCodeRead() {
		return codeRead;
	}

	public CodeCompute getCodeCompute() {
		return codeCompute;
	}

	
	
	

}
