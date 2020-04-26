/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

import com.game37.protobuf4j.monitor.FileUtil;
import com.game37.protobuf4j.proto.utils.enums.TypeWrapperEnum;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.Descriptor;

/**
* author chenshanbao
**/

public class CodeGenerator {
		
	private final static String DYNAMIC_CONTENT = "dynamicContent";
	private final static String FULL_CLASS_NAME = "fullClassName";
	
	private final static Map<String,String> tplCahce = new HashMap<String, String>();
	private final static String TPL_METHOD_VALUE_TO_ENUM 
							="template/method_valueToEnum.tpl";
	private final static String TPL_METHOD_VALUE_TO_ENUM_OPTIMIZE 
							="template/method_valueToEnumOptimize.tpl";
	private final static String TPL_METHOD_READ_FROM				
							="template/method_readFrom.tpl";
	private final static String TPL_METHOD_DECODE 
							= "template/method_decode.tpl";
	private final static String TPL_METHOD_ENCODE 
							= "template/method_encode.tpl";
	private final static String TPL_METHOD_GET_SERIALIZED_SIZE 
							= "template/method_computeProtoSize.tpl";
	private final static String TPL_METHOD_WTIRE_TO 
							= "template/method_writeTo.tpl";
	
	private static volatile boolean isLoadedTpl = false;
	
	public static String getTplString(String tplPath){
		
		if(!isLoadedTpl) {			
			synchronized (tplCahce) {
				//load
				try {
					loadAllTemplateFile();
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(e.getMessage());
				}
				isLoadedTpl=true;
			}
		}	
		return tplCahce.get(tplPath);
	}
	
	private static void loadAllTemplateFile() throws IOException{
		tplCahce.put(TPL_METHOD_VALUE_TO_ENUM_OPTIMIZE, loadTemplateFile(TPL_METHOD_VALUE_TO_ENUM_OPTIMIZE));
		tplCahce.put(TPL_METHOD_VALUE_TO_ENUM, loadTemplateFile(TPL_METHOD_VALUE_TO_ENUM));
		tplCahce.put(TPL_METHOD_READ_FROM, loadTemplateFile(TPL_METHOD_READ_FROM));
		tplCahce.put(TPL_METHOD_DECODE, loadTemplateFile(TPL_METHOD_DECODE));
		tplCahce.put(TPL_METHOD_ENCODE, loadTemplateFile(TPL_METHOD_ENCODE));
		tplCahce.put(TPL_METHOD_GET_SERIALIZED_SIZE, loadTemplateFile(TPL_METHOD_GET_SERIALIZED_SIZE));
		tplCahce.put(TPL_METHOD_WTIRE_TO, loadTemplateFile(TPL_METHOD_WTIRE_TO));
		
		//map;
		tplCahce.put(DECODE_TPL_MAP, loadTemplateFile(DECODE_TPL_MAP));
		tplCahce.put(DECODE_TPL_MAP_SUB, loadTemplateFile(DECODE_TPL_MAP_SUB));
		tplCahce.put(DECODE_TPL_MAP_SUB_MESSAGE, loadTemplateFile(DECODE_TPL_MAP_SUB_MESSAGE));
		tplCahce.put(DECODE_TPL_MAP_SUB_SIMPLE, loadTemplateFile(DECODE_TPL_MAP_SUB_SIMPLE));
	}
	
	private static String loadTemplateFile(String path) throws IOException{
		
		URL url = CodeGenerator.class.getClassLoader().getResource(path);	
		if(GlobalConfig.getOpenLogFlag()) {		
			System.out.println(path);
			System.out.println(url);
		}
		InputStream input = null;
		try{
			input = url.openStream();
			byte[] data = new byte[input.available()];
			input.read(data);
			String out = new String(data);
			return out;
		}finally {
			if(input!=null) {
				input.close();
			}
		}
	}
	
	public static CtClass getListCtClass() throws NotFoundException{
		return ClassPool.getDefault().get("java.util.List");
	}
	
	public static CtClass getIterableCtClass() throws NotFoundException{
		return ClassPool.getDefault().get("java.util.Iterable");
	}
	
	public static CtClass getSetCtClass() throws NotFoundException{
		return ClassPool.getDefault().get("java.util.Set");
	}
	
	public static CtClass getMapCtClass() throws NotFoundException{
		return ClassPool.getDefault().get("java.util.Map");
	}
	
	public static CtClass getEnumCtClass() throws NotFoundException{
		return ClassPool.getDefault().get("java.lang.Enum");
	}
	
	public static String getGenericSignature(String input) {

		int startIndex = input.indexOf('<');
		int endIndex = input.lastIndexOf('>');
		return Descriptor.toJavaName(input.substring(startIndex+2, endIndex-1));
	}
	
	public static MapDesc getGenericSignatureMap(String input) {
//===============Ljava/util/Map<Ljava/lang/Integer;Ljava/lang/Integer;>;
		if(GlobalConfig.getOpenLogFlag()) {			
			System.out.println("==============="+input);
		}
		int startIndex = input.indexOf('<');
		int endIndex = input.lastIndexOf('>');
		String temp = input.substring(startIndex+2, endIndex-1);
		String[] array = temp.split(";");	
		return new MapDesc(Descriptor.toJavaName(array[0]),Descriptor.toJavaName(array[1].substring(1)));
	}
	
	
	public static void wrapperCollectionsCode(CtField field, StringBuilder bu, String newStr
			,String fieldAppend, String tabStringPre) throws NotFoundException {
		//init
		bu.append(tabStringPre).append("if( ").append(fieldAppend).append(" == null ){\r\n");
		bu.append(tabStringPre).append("\t").append(fieldAppend).append(" = ").append(newStr).append("\r\n");
		bu.append(tabStringPre).append("}\r\n");
		//
		String generTypeStr = getGenericSignature(field.getGenericSignature());
		CtClass ctClass = ClassPool.getDefault().get(generTypeStr);
		TypeWrapperEnum ft = TypeUtil.getTypeWrapperEnum(ctClass);
		if(TypeWrapperEnum.MESSAGE.equals(ft)) {			
			bu.append(tabStringPre).append(fieldAppend).append(".add(").append(generTypeStr)
				.append(".readFrom(input)").append(");\r\n");
		}else {
			//基本类型；
			bu.append(tabStringPre).append(fieldAppend).append(".add(")
				.append(ft.getReadStringPackage()).append(");\r\n");
		}			
	}
	
	private static String ENUM_TEMPLATE_FIELD = "enumTemplateCache";
	
	public static int makeMethod(final String fullClassName, String classSaveDir) throws Exception {
		ClassPool pool = ClassPool.getDefault();
		final CtClass ctClass = pool.get(fullClassName);
		if(ctClass.isFrozen()) {
			ctClass.defrost();
		}
		
		TypeWrapperEnum ftParent = TypeUtil.getTypeWrapperEnum(ctClass);
		if(TypeWrapperEnum.ENUM.equals(ftParent)) {
			//
			if(!TypeUtil.isExistMethod(ctClass, "value")) {
				return 0;
			}
			//valueToEnum
			if(TypeUtil.isExistMethod(ctClass, "valueToEnum", new CtClass[] {CtClass.intType})) {
				return 0;
			}
			String tpl = TPL_METHOD_VALUE_TO_ENUM;
			if(GlobalConfig.isAddEnumOptimizeCode()) {
				//add field;
				String desc = "java.util.Map";
				CtField field = new CtField(pool.get(desc), ENUM_TEMPLATE_FIELD, ctClass);
				int modif = AccessFlag.setPrivate(AccessFlag.STATIC);
				modif = modif | AccessFlag.TRANSIENT;
				field.setModifiers(modif);				
				ctClass.addField(field, "new java.util.HashMap()");	
				tpl = TPL_METHOD_VALUE_TO_ENUM_OPTIMIZE;
			}
			String method = makeMethodValueToEnum(ctClass, fullClassName, tpl);
			//System.out.println(method);
			CtMethod me = CtMethod.make(method, ctClass);
			ctClass.addMethod(me);
			ctClass.toClass();
			//
			if(classSaveDir!=null) {				
				ctClass.writeFile(classSaveDir);
			}
			return 1;
		}
		
		String computSize = "__computeSize";
		//check __computeSize is exist
		CtField fieldTemp = getCtField(ctClass, computSize);
		if(fieldTemp != null) {
			return 0;
		}
		//make __computeSize field	
		CtField field = new CtField(CtClass.intType, computSize,  ctClass);
		field.setModifiers(AccessFlag.setPrivate(AccessFlag.TRANSIENT));	
		ctClass.addField(field, String.valueOf(0));
		
		//add inerface ProtobufCodec
		ctClass.addInterface(pool.get("com.game37.protobuf4j.codec.ProtobufCodec"));
		
		List<String> list = new ArrayList<String>(4);
		list.add(makeMethodReadFrom(ctClass, fullClassName));
		list.add(makeMethodDecode(ctClass, fullClassName));
		list.add(makeMethodComputeProtoSize(ctClass, fullClassName));
		list.add(makeMethodWriteTo(ctClass, fullClassName));
		list.add(makeMethodEncode(ctClass, fullClassName));
		
		
		try {
			if(ctClass.isFrozen()) {
				ctClass.defrost();
			}
			for(String method:list) {	
				if(GlobalConfig.getOpenLogFlag()) {						
					System.out.println(method);
				}
				CtMethod me = CtMethod.make(method, ctClass);
				ctClass.addMethod(me);
			};
			//
			if(GlobalConfig.isGenerateGetAndSetMethod()) {
				makeMethodGet(ctClass);
				makeMethodSet(ctClass);
			}
			
			ctClass.toClass();
			//
			if(classSaveDir!=null) {				
				ctClass.writeFile(classSaveDir);
			}
			return 1;
		} catch (Exception e) {
			if(GlobalConfig.getOpenLogFlag()) {				
				list.forEach(temp->System.out.println(temp));
			}			
			throw e;
		}
	}
	

	public final static String  DECODE_TPL_MAP_SUB_MESSAGE = "template/DECODE_TPL_MAP_SUB_MESSAGE.tpl";
	public final static String  DECODE_TPL_MAP_SUB_SIMPLE = "template/DECODE_TPL_MAP_SUB_SIMPLE.tpl";
	public final static String  DECODE_TPL_MAP_SUB = "template/DECODE_TPL_MAP_SUB.tpl";	
	public final static String  DECODE_TPL_MAP = "template/DECODE_TPL_MAP.tpl";
	
	
	
	public final static String  DECODE_TPL_SIMPLE =
			"\t\t\t\tcase __tagNumber__:{\r\n" + 
			"\t\t\t\t		out.__fieldName__ = __readStringPackage__;\r\n" + 
			"\t\t\t\t		break;\r\n" + 
			"\t\t\t\t}\r\n";
	
	public final static String  DECODE_TPL_SET_PRE =
			"\t\t\t\tcase __tagNumber__:{\r\n" + 
			"\t\t\t\t		if( out.__fieldName__ == null ){\r\n" +  
			"\t\t\t\t				out.__fieldName__ = __newSetFullClass__;\r\n" + 
			"\t\t\t\t		}\r\n" + 
			"\t\t\t\t		out.__fieldName__.add(__readStringPackage__);\r\n" + 
			"\t\t\t\t		break;\r\n" + 
			"\t\t\t\t}\r\n";

	public final static String  DECODE_TPL_SET =
			"\t\t\t\tcase __tagNumber__:{\r\n" + 
			"\t\t\t\t		int length = input.readRawVarint32();\r\n" + 
			"\t\t\t\t		final int oldLimit = input.pushLimit(length);\r\n" +
			"\t\t\t\t		if( out.__fieldName__ == null ){\r\n" +  
			"\t\t\t\t				out.__fieldName__ = __newSetFullClass__;\r\n" + 
			"\t\t\t\t		}\r\n" + 
			
			"__readListSubTpl__"+
			
			"\t\t\t\t		input.popLimit(oldLimit);\r\n" + 
			"\t\t\t\t		break;\r\n" + 
			"\t\t\t\t}\r\n";
	
	
	
	public final static String  DECODE_TPL_SET_SIMPLE =
			"\t\t\t\tcase __tagNumber__:{\r\n" + 
			"\t\t\t\t		if( out.__fieldName__ == null ){\r\n" +  
			"\t\t\t\t				out.__fieldName__ = __newSetFullClass__;\r\n" + 
			"\t\t\t\t		}\r\n" + 		
			
			"__readListSubTpl__"+
			
			"\t\t\t\t		break;\r\n" + 
			"\t\t\t\t}\r\n";
	
	
	
	public final static String  DECODE_TPL_MESSAGE =
			"\t\t\t\tcase __tagNumber__:{\r\n" + 
			"\t\t\t\t		int length = input.readRawVarint32();\r\n" + 
			"\t\t\t\t		final int oldLimit = input.pushLimit(length);\r\n" +
			"\t\t\t\t		out.__fieldName__ = __readStringPackage__;\r\n" + 
			"\t\t\t\t		input.popLimit(oldLimit);\r\n" + 
			"\t\t\t\t		break;\r\n" + 
			"\t\t\t\t}\r\n";
	
	public final static String  ENCODE_TPL_SIMPLE_LIST =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 		
			"		for (int i = 0; i < __fieldName__.size(); i++) {\r\n" +  
			"				output.__writeMethod__(__fieldNumber__,((__elementFullClass__)__fieldName__.get(i)).__changeToBaseMemthod__);\r\n" + 
			"		}\r\n" + 
			"\t}\r\n";

	
	public final static String  ENCODE_TPL_SIMPLE_LIST_PACKABLE =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 	
	
			"		int dataSize = 0;\r\n" + 
			"		for (int i = 0; i < __fieldName__.size(); i++) {\r\n" + 
			"			dataSize += com.google.protobuf.CodedOutputStream\r\n" + 
			"				.__computeMethod__NoTag(((__elementFullClass__)__fieldName__.get(i)).__changeToBaseMemthod__);\r\n" + 
			"		}\r\n" + 
			
			"		int tag = com.game37.protobuf4j.proto.utils.WireFormatCopy.makeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" + 		
			"		output.writeUInt32NoTag(tag);\r\n" + 
			"		output.writeUInt32NoTag(dataSize);\r\n" + 
			
			"		for (int i = 0; i < __fieldName__.size(); i++) {\r\n" +  
			"				output.__writeMethod__NoTag(((__elementFullClass__)__fieldName__.get(i)).__changeToBaseMemthod__);\r\n" + 
			"		}\r\n" + 
			"\t}\r\n";
	public final static String  ENCODE_TPL_SIMPLE_LIST_2 =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 		
			"		for (int i = 0; i < __fieldName__.size(); i++) {\r\n" +  
			"				__writeMethod__\r\n" + 
			"		}\r\n" + 
			"\t}\r\n";
	
	public final static String  ENCODE_TPL_SIMPLE_COLLECTION =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"	     Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"			 		output.__writeMethod__(__fieldNumber__,((__elementFullClass__)it.next()).__changeToBaseMemthod__);\r\n" + 
			"	 	 }\r\n" + 
			"\t}\r\n";
	
	public final static String  ENCODE_TPL_SIMPLE_COLLECTION_PACKABLE =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
	
			"	 	 int dataSize = 0;\r\n" + 
			"	     java.util.Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"		 		dataSize += com.google.protobuf.CodedOutputStream\r\n" + 
			"			 		.__computeMethod__NoTag(((__elementFullClass__)it.next()).__changeToBaseMemthod__);\r\n" + 
			"	 	 }\r\n" + 
		
			"		int tag = com.game37.protobuf4j.proto.utils.WireFormatCopy.makeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" + 		
			"		output.writeUInt32NoTag(tag);\r\n" + 
			"		output.writeUInt32NoTag(dataSize);\r\n" + 
			
			"	     java.util.Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"			 		output.__writeMethod__NoTag(((__elementFullClass__)it.next()).__changeToBaseMemthod__);\r\n" + 
			"	 	 }\r\n" + 
			"\t}\r\n";
	public final static String  ENCODE_TPL_SIMPLE_COLLECTION_2 =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"	     java.util.Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"			 		__writeMethod__\r\n" + 
			"	 	 }\r\n" + 
			"\t}\r\n";
	
	public final static String  ENCODE_TPL_MESSAGE_LIST = 
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"		for(int i=0;i<__fieldName__.size();i++) {\r\n" + 
					
			//"			output.writeByteArray(__fieldNumber__,((__elementFullClass__)__fieldName__.get(i)).encode());\r\n" + 
			"			output.writeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" +
			"			output.writeUInt32NoTag(((__elementFullClass__)__fieldName__.get(i)).computeProtoSize());\r\n" + 
			"			((__elementFullClass__)__fieldName__.get(i)).writeTo(output);\r\n" +

			"	 	}\r\n" + 
			"\t}\r\n";
	
	public final static String  ENCODE_TPL_MESSAGE_COLLECTION =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"    	 java.util.Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			
			//"		 		output.writeByteArray(__fieldNumber__,((__elementFullClass__)it.next()).encode());\r\n" + 
			"			output.writeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" +
			"			__elementFullClass__ itTemp = (__elementFullClass__)it.next();\r\n" +
			"			output.writeUInt32NoTag(itTemp.computeProtoSize());\r\n" + 
			"			itTemp.writeTo(output);\r\n" +
			
			"	     }\r\n" + 
			"\t}\r\n";
	
	public final static String  ENCODE_TPL_SIMPLE_WRAPPER =    "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
			"		output.__writeMethod__(__fieldNumber__,((__elementFullClass__)__fieldName__).__changeToBaseMemthod__);\r\n" + 
			"	}\r\n";
	
	public final static String  ENCODE_TPL_SIMPLE =    "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
			"		output.__writeMethod__(__fieldNumber__,__fieldName__);\r\n" + 
			"	}\r\n";
	
	public final static String  ENCODE_TPL_SIMPLE_NO_IF =   
			"	output.__writeMethod__(__fieldNumber__,__fieldName__);\r\n"; 
	
	
	public final static String  ENCODE_TPL_MESSAGE =    "if (__fieldName__ != null) {\r\n" + 
			//"		byte[] data = ((__elementFullClass__)__fieldName__).encode();\r\n" + 
			//"		size += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(value);\r\n" +
			//"		output.writeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" + 
			//"		output.writeByteArray(__fieldNumber__,data);\r\n" + 
			//"		output.writeByteArrayNoTag(data);\r\n" + 
			"		output.writeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" +
			//"		output.writeTag(__fieldNumber__, 0);\r\n" +
			"		output.writeUInt32NoTag(((__elementFullClass__)__fieldName__).computeProtoSize());\r\n" + 
			"		__fieldName__.writeTo(output);\r\n" + 
			"	}\r\n";
	
	
	
			
	public final static String  COMPUTE_TPL_SIMPLE_LIST_PACKABLE =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
	
			"		int dataSize = 0;\r\n" + 
			"		for (int i = 0; i < __fieldName__.size(); i++) {\r\n" + 
			"			dataSize += com.google.protobuf.CodedOutputStream\r\n" + 
			"				.__computeMethod__NoTag(((__elementFullClass__)__fieldName__.get(i)).__changeToBaseMemthod__);\r\n" + 
			"		}\r\n" + 
			
			"		size += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(dataSize);\r\n" + 
			"		int tag = com.game37.protobuf4j.proto.utils.WireFormatCopy.makeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" + 
			"		size += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(tag);\r\n" + 
			"		size += dataSize;\r\n" + 

			//"		size += __fieldName__.size();\r\n" + 
			"\t}\r\n";
	
	public final static String  COMPUTE_TPL_SIMPLE_LIST =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"		int dataSize = 0;\r\n" + 
			"		for (int i = 0; i < __fieldName__.size(); i++) {\r\n" + 
			"			dataSize += com.google.protobuf.CodedOutputStream\r\n" + 
			"				.__computeMethod__NoTag(__stringToBytesMethod__(((__elementFullClass__)__fieldName__.get(i)).__changeToBaseMemthod__));\r\n" + 
			"		}\r\n" + 
			"		size += dataSize;\r\n" + 
			"		size += __fieldName__.size()*com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" + 
			"\t}\r\n";
	
	public final static String  COMPUTE_TPL_SIMPLE_COLLECTION =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"	 	 int dataSize = 0;\r\n" + 
			"	     Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"		 		dataSize += com.google.protobuf.CodedOutputStream\r\n" + 
			"			 		.__computeMethod__NoTag(__stringToBytesMethod__(((__elementFullClass__)it.next()).__changeToBaseMemthod__));\r\n" + 
			"	 	 }\r\n" + 
			"	 	 size += dataSize;\r\n" + 
			"	 	 size += __fieldName__.size()*com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" + 
			"\t}\r\n";
	
	public final static String  COMPUTE_TPL_SIMPLE_COLLECTION_PACKABLE =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"	 	 int dataSize = 0;\r\n" + 
			"	     java.util.Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"		 		dataSize += com.google.protobuf.CodedOutputStream\r\n" + 
			"			 		.__computeMethod__NoTag(((__elementFullClass__)it.next()).__changeToBaseMemthod__);\r\n" + 
			"	 	 }\r\n" + 
			
			"		size += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(dataSize);\r\n" + 
			"		int tag = com.game37.protobuf4j.proto.utils.WireFormatCopy.makeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" + 
			"		size += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(tag);\r\n" + 
			"		size += dataSize;\r\n" + 
			
			//"	 	 size += dataSize;\r\n" + 
			//"	 	 size += __fieldName__.size();\r\n" + 
			"\t}\r\n";
	
	
	public final static String  COMPUTE_TPL_MESSAGE_LIST = 
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"	 	int tagSize = com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" +
			"		for(int i=0;i<__fieldName__.size();i++) {\r\n" + 
			"			int value = ((__elementFullClass__)__fieldName__.get(i)).computeProtoSize();\r\n" + 
			"			size+=tagSize+value+com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(value);\r\n" + 
			"	 	}\r\n" + 
			"\t}\r\n";
	
	public final static String  COMPUTE_TPL_MESSAGE_COLLECTION =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"	 	int tagSize = com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" +
			"    	 java.util.Iterator it = __fieldName__.iterator();\r\n" + 
			"	     while(it.hasNext()) {\r\n" + 
			"		 		int value = ((__elementFullClass__)it.next()).computeProtoSize();\r\n" + 
			"				size+=tagSize+value+com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(value);\r\n" + 			
			"	     }\r\n" + 
			"\t}\r\n";
	
	
	public final static String  COMPUTE_TPL_SIMPLE =    "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
			"		size += com.google.protobuf.CodedOutputStream.__computeMethod__(__fieldNumber__, __stringToBytesMethod__(__fieldName2__));\r\n" + 
			"	}\r\n";
	public final static String  COMPUTE_TPL_SIMPLE_WRAPPER =    "if (__fieldName__ != __fieldDefaultValue__) {\r\n" + 
			"		size += com.google.protobuf.CodedOutputStream.__computeMethod__(__fieldNumber__, __fieldName2__.__changeToBaseMemthod__);\r\n" + 
			"	}\r\n";
	
	
	public final static String  COMPUTE_TPL_SIMPLE_NO_IF =   
			"	size += com.google.protobuf.CodedOutputStream.__computeMethod__(__fieldNumber__, __fieldName2__);\r\n";
			;
	public final static String  COMPUTE_TPL_SIMPLE_WRAPPER_NO_IF =   
			"	size += com.google.protobuf.CodedOutputStream.__computeMethod__(__fieldNumber__, __fieldName2__.__changeToBaseMemthod__);\r\n" 
			;
	
	
	
	public final static String  COMPUTE_TPL_MESSAGE =    "if (__fieldName__ != null) {\r\n" + 
			"		int value = __fieldName__.computeProtoSize();\r\n" + 
			"		size += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(value);\r\n" + 
			"		size += com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" + 
			"		size += value;\r\n" + 
			"	}\r\n";
	
	
	
	
	
	public final static String  COMPUTE_TPL_MAP_SUB_SIMPLE =   
			"int size__fieldNumber__ = com.google.protobuf.CodedOutputStream.__computeMethod__(__fieldNumber__, __stringToBytesMethod__(__fieldName__));\r\n";
			;
	public final static String  COMPUTE_TPL_MAP_SUB_SIMPLE_WRAPPER =   
			"int size__fieldNumber__ = com.google.protobuf.CodedOutputStream.__computeMethod__(__fieldNumber__, __fieldName__.__changeToBaseMemthod__);\r\n" 
			;
			
	public final static String  COMPUTE_TPL_MAP_SUB_MESSAGE =   
			"int size__fieldNumber__ = __fieldName__.computeProtoSize();\r\n" + 
			"size__fieldNumber__ += com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(size__fieldNumber__);\r\n" + 
			"size__fieldNumber__ += com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n"
			;
	
//	Iterator it = map.entrySet().iterator();
//	while(it.hasNext()) {
//		java.util.Map.Entry entry = (java.util.Map.Entry)it;
 
	public final static String  COMPUTE_TPL_MAP =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			"		int tagSize = com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" +
			"		java.util.Iterator it = __fieldName__.entrySet().iterator();\r\n" + 
			"		while(it.hasNext()) {\r\n" + 			
			"		 		java.util.Map.Entry entry = (java.util.Map.Entry)it.next();\r\n" + 
			
			"		 		__computeMapSubTpl__\r\n" +
			"		 		int value = size1 + size2;\r\n" +
			//"		 		int value = ((__elementFullClass__)it.next()).computeProtoSize();\r\n" + 						
			
			"				size+=tagSize+value+com.google.protobuf.CodedOutputStream.computeUInt32SizeNoTag(value);\r\n" + 			
			"		}\r\n" + 
			"\t}\r\n";
	
	
	public final static String  ENCODE_TPL_MAP =
			"if(__fieldName__!=null&&__fieldName__.size()>0) {\r\n" + 
			//"		int tagSize = com.google.protobuf.CodedOutputStream.computeTagSize(__fieldNumber__);\r\n" +
			"		java.util.Iterator it = __fieldName__.entrySet().iterator();\r\n" + 
			"		while(it.hasNext()) {\r\n" + 			
			"		 		java.util.Map.Entry entry = (java.util.Map.Entry)it.next();\r\n" + 
			
			"		 		__computeMapSubTpl__\r\n" +
			"		 		int value = size1 + size2;\r\n" +
			
			"		 		output.writeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" +
			"		 		output.writeUInt32NoTag(value);\r\n" +
											
			"		 		__writeMapSubTpl__\r\n" +
			
			"		}\r\n" + 
			"\t}\r\n";
	
	
	public final static String  ENCODE_TPL_MAP_SUB_SIMPLE =   
			"output.__writeMethod__(__fieldNumber__,__fieldName__);\r\n"; 
	
	public final static String  ENCODE_TPL_MAP_SUB_SIMPLE_WRAPPER =   
			"output.__writeMethod__(__fieldNumber__,(__fieldName__.__changeToBaseMemthod__));\r\n";
		
	public final static String  ENCODE_TPL_MAP_SUB_MESSAGE =   
			"		output.writeTag(__fieldNumber__, com.google.protobuf.WireFormat.WIRETYPE_LENGTH_DELIMITED);\r\n" +
			"		output.writeUInt32NoTag(__fieldName__.computeProtoSize());\r\n" + 
			"		__fieldName__.writeTo(output);\r\n";
	
	
	
	public final static String  __fieldName__ = "__fieldName__";
	public final static String  __fieldName2__ = "__fieldName2__";
	public final static String  __fieldDefaultValue__ = "__fieldDefaultValue__";
	public final static String  __fieldNumber__ = "__fieldNumber__";
	public final static String  __computeMethod__ = "__computeMethod__";
	public final static String  __elementFullClass__ = "__elementFullClass__";
	public final static String  __changeToBaseMemthod__ = "__changeToBaseMemthod__";
	public final static String  __changeToBaseMemthodRemove__ = ".__changeToBaseMemthod__";
	public final static String  __writeMethod__ = "__writeMethod__";
	public final static String  __tagNumber__ = "__tagNumber__";
	public final static String  __readStringPackage__ = "__readStringPackage__";
	public final static String  __newSetFullClass__ = "__newSetFullClass__";
	
	public final static String  __readListSubTpl__ = "__readListSubTpl__";
	
	public final static String  __computeMapSubTpl__ = "__computeMapSubTpl__";
	public final static String  __writeMapSubTpl__ = "__writeMapSubTpl__";
	
	
	public final static String  __decodeMapSub__ = "__decodeMapSub__";
	public final static String  __decodeMapSubTpl__ = "__decodeMapSubTpl__";
	public final static String  __decodeMapSubKeyClass__ = "__decodeMapSubKeyClass__";
	public final static String  __decodeMapSubValueClass__ = "__decodeMapSubValueClass__";
	
	public final static String  NEW_LIST = "new java.util.ArrayList()";
	public final static String  NEW_HASH_SET  = "new java.util.HashSet()";
	public final static String  NEW_HASH_MAP  = "new java.util.HashMap()";
		
	public final static String  __decodeSetEnumDefault__ = "__decodeSetEnumDefault__";
	
	public final static String  __stringToBytesMethod__ = "__stringToBytesMethod__";
	
	public static String makeMethodReadFrom(CtClass ctClass, String fullClassName) throws Exception {
		
		String template = getTplString(TPL_METHOD_READ_FROM);
		Map<String,String> replaceMap = new HashMap<String,String>();
		replaceMap.put(FULL_CLASS_NAME, fullClassName);
		
		String __decodeSetEnumDefault__ = "";
		
		String tabString = "";
		StringBuilder bu = new StringBuilder();
		int fieldNumber = 0;
		for(CtField field:getCtFieldList(ctClass)) {
			//
			if(!ClassModifyUtil.checkCtField(field)) {
				continue;
			}
			++fieldNumber;
			TypeWrapperEnum ft = TypeUtil.getTypeWrapperEnum(field.getType());
			if(GlobalConfig.getOpenLogFlag()) {		
				System.out.println("===="+field.getType().getName());
				System.out.println("===="+ft);			
			}
			
			int tag = WireFormatCopy.makeTag(fieldNumber, ft.getFieldType().getWireType());
			boolean isListType = false;
			
			if((isListType = field.getType().subclassOf(getListCtClass()))
					||field.getType().subclassOf(getSetCtClass())
					||field.getType().subclassOf(getMapCtClass())) {
				
				if(TypeWrapperEnum.MAP.equals(ft)) {
					MapDesc mapDesc = getGenericSignatureMap(field.getGenericSignature());
					bu.append(tabString).append(ft.getCodeRead()
							.getMethodMap(fieldNumber, field.getName(), mapDesc));
					continue;
				}				
				//
				String elementFullClass = getGenericSignature(field.getGenericSignature());
				CtClass elementCtClass  = ClassPool.getDefault().get(elementFullClass);
				TypeWrapperEnum elementType = TypeUtil.getTypeWrapperEnum(elementCtClass);

				String readString = elementType.getReadStringPackage().getValue(elementCtClass);
				boolean isNeedPre = elementType.getNeedGenePreCode().getValue(elementCtClass);
				if(isNeedPre) {

					String tempTpl = DECODE_TPL_SET_PRE	
							.replace(__tagNumber__, String.valueOf(tag-2))
							.replace(__fieldName__, field.getName())
							.replace(__readStringPackage__, readString);
					if(isListType) {
						tempTpl = tempTpl.replace(__newSetFullClass__, NEW_LIST);
					}else {
						tempTpl = tempTpl.replace(__newSetFullClass__, NEW_HASH_SET);
					}
					bu.append(tabString).append(tempTpl);
				}
																
				String tempTpl = null;
				if(isListType) {					
					tempTpl = elementType.getCodeRead().getMethodList(fieldNumber, elementFullClass, 
							field.getName(), elementCtClass, elementType);
				}else {
					tempTpl = elementType.getCodeRead().getMethodCollection(fieldNumber, elementFullClass, 
							field.getName(), elementCtClass, elementType);
				}
				bu.append(tabString).append(tempTpl);
			}else {
				//
				String tempTpl =	ft.getCodeRead().getMethodSimple(fieldNumber, field.getType().getName(), 
						field.getName(), field.getType(), ft);
				bu.append(tabString).append(tempTpl);
				//
				if(GlobalConfig.isDecodeSetEnumDefault()) {
					
					if(field.getType().isEnum()) {
						//
						String temp = "out."+field.getName()+" = "+field.getType().getName()+".values()[0];";
						__decodeSetEnumDefault__ = __decodeSetEnumDefault__ + temp + FileUtil.NEW_LINE;
					}					
				}
			}								
		}
		//
		replaceMap.put(CodeGenerator.__decodeSetEnumDefault__, __decodeSetEnumDefault__);
		replaceMap.put(DYNAMIC_CONTENT, bu.toString());
		String method = StrSubstitutor.replace(template, replaceMap);
		return method;		
	}
	//
	
	public static String makeMethodEncode(CtClass ctClass, String fullClassName) throws Exception {
		String template = getTplString(TPL_METHOD_ENCODE);
		return template;		
	}
	
	public static String makeMethodWriteTo(CtClass ctClass, String fullClassName) throws Exception {
		
		String template = getTplString(TPL_METHOD_WTIRE_TO);
		Map<String,String> replaceMap = new HashMap<String,String>();
		replaceMap.put(FULL_CLASS_NAME, fullClassName);
		//
		String tabString = "\t";
		StringBuilder bu = new StringBuilder();
		int fieldNumber = 0;
				
		for(CtField field:getCtFieldList(ctClass)) {
			//
			if(!ClassModifyUtil.checkCtField(field)) {
				continue;
			}
			//
			++fieldNumber;	
			TypeWrapperEnum ft = TypeUtil.getTypeWrapperEnum(field.getType());
			boolean isListType = false;
			
			String fieldName = "this."+field.getName();
			
			if((isListType = field.getType().subclassOf(getListCtClass()))
					||field.getType().subclassOf(getSetCtClass())
					||field.getType().subclassOf(getMapCtClass())) {
					
				if(TypeWrapperEnum.MAP.equals(ft)) {
					MapDesc mapDesc = getGenericSignatureMap(field.getGenericSignature());					
					bu.append(tabString).append(
							ft.getCodeWrite().getMethodMap(fieldNumber, fieldName, mapDesc));
					continue;
				}				
				//
				String elementFullClass = getGenericSignature(field.getGenericSignature());
				CtClass elementCtClass  = ClassPool.getDefault().get(elementFullClass);
				TypeWrapperEnum elementType = TypeUtil.getTypeWrapperEnum(elementCtClass);
				if(isListType) {
					String tempTpl = elementType.getCodeWrite()
						.getMethodList(fieldNumber, elementFullClass, fieldName, elementCtClass, elementType);
					bu.append(tabString).append(tempTpl);
				}else {
					String tempTpl = elementType.getCodeWrite()
							.getMethodCollection(fieldNumber, elementFullClass, fieldName, elementCtClass, elementType);
					bu.append(tabString).append(tempTpl);
				}
			}else {
				
				String tempTpl = ft.getCodeWrite()
						.getMethodSimple(fieldNumber, field.getType().getName(), fieldName, field.getType(), ft);
				bu.append(tabString).append(tempTpl);								
			}								
		}
		//
		replaceMap.put(DYNAMIC_CONTENT, bu.toString());
		String method = StrSubstitutor.replace(template, replaceMap);
		return method;		
	}
	
	
	public static String makeMethodComputeProtoSize(CtClass ctClass, String fullClassName) throws Exception {
		
		String template = getTplString(TPL_METHOD_GET_SERIALIZED_SIZE);
		Map<String,String> replaceMap = new HashMap<String,String>();
		replaceMap.put(FULL_CLASS_NAME, fullClassName);
		//
		String tabString = "\t";
		StringBuilder bu = new StringBuilder();
		int fieldNumber = 0;
				
		for(CtField field:getCtFieldList(ctClass)) {
			//
			if(!ClassModifyUtil.checkCtField(field)) {
				continue;
			}
			//
			++fieldNumber;	
			TypeWrapperEnum ft = TypeUtil.getTypeWrapperEnum(field.getType());			
			String fieldName = "this."+field.getName();
			
			boolean isListType = false;
			if((isListType = field.getType().subclassOf(getListCtClass()))
					||field.getType().subclassOf(getSetCtClass())
					||field.getType().subclassOf(getMapCtClass())) {
					
				if(TypeWrapperEnum.MAP.equals(ft)) {
					MapDesc mapDesc = getGenericSignatureMap(field.getGenericSignature());					
					bu.append(tabString).append(
							ft.getCodeCompute().getMethodMap(fieldNumber, fieldName, mapDesc));
					continue;
				}				
				//
				String elementFullClass = getGenericSignature(field.getGenericSignature());
				CtClass elementCtClass  = ClassPool.getDefault().get(elementFullClass);
				TypeWrapperEnum elementType = TypeUtil.getTypeWrapperEnum(elementCtClass);
			
				if(isListType) {
					String tempTpl = elementType.getCodeCompute()
							.getMethodList(fieldNumber, elementFullClass, fieldName, elementCtClass, elementType);
					bu.append(tabString).append(tempTpl);										
				}else {
					String tempTpl = elementType.getCodeCompute()
							.getMethodCollection(fieldNumber, elementFullClass, fieldName, elementCtClass, elementType);
					bu.append(tabString).append(tempTpl);	
				}
																
			}else {
											
				String tempTpl = ft.getCodeCompute().getMethodSimple(fieldNumber,
						field.getType().getName(), fieldName, field.getType(), ft);
				bu.append(tabString).append(tempTpl);
			}								
		}
		//
		replaceMap.put(DYNAMIC_CONTENT, bu.toString());
		String method = StrSubstitutor.replace(template, replaceMap);
		return method;		
	}
	
	
	public static String makeMethodDecode(CtClass ctClass, String fullClassName) throws Exception {
		String template = getTplString(TPL_METHOD_DECODE);
		Map<String,String> replaceMap = new HashMap<String,String>();
		replaceMap.put(FULL_CLASS_NAME, fullClassName);
		String method = StrSubstitutor.replace(template, replaceMap);
		return method;		
	}
	
	public static String makeMethodValueToEnum(CtClass ctClass, String fullClassName, String tpl) throws Exception {
		String template = getTplString(tpl);
		if(template == null) {
			System.out.println(Thread.currentThread().getName()+"=2null");
		}
		Map<String,String> replaceMap = new HashMap<String,String>();
		replaceMap.put(FULL_CLASS_NAME, fullClassName);
		String method = StrSubstitutor.replace(template, replaceMap);
		return method;		
	}
	
	
	public static String makeMethodGet(CtClass ctClass) throws Exception {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(CtField field:getCtFieldList(ctClass)) {
			//
			if(!ClassModifyUtil.checkCtField(field)) {
				continue;
			}
			
			String methodName = "get"+firstToUpper(field.getName());
			try {				
				ctClass.getDeclaredMethod(methodName);
				continue;
			} catch (Exception e) {
			}
						
			stringBuilder.append("public ").append(field.getType().getName())
					.append(" ").append(methodName)
					.append("() {").append(FileUtil.NEW_LINE)
					.append("    return this.").append(field.getName()).append(";").append(FileUtil.NEW_LINE)
					.append("}").append(FileUtil.NEW_LINE)
					;		
			String method = stringBuilder.toString();
			stringBuilder.delete(0, stringBuilder.length());
			CtMethod me = CtMethod.make(method, ctClass);
			ctClass.addMethod(me);
		}
		return null;		
	}
	
	public static String makeMethodSet(CtClass ctClass) throws Exception {
		
		
		StringBuilder stringBuilder = new StringBuilder();
		for(CtField field:getCtFieldList(ctClass)) {
			//
			if(!ClassModifyUtil.checkCtField(field)) {
				continue;
			}
			
			String methodName = "set"+firstToUpper(field.getName());
			try {				
				ctClass.getDeclaredMethod(methodName, new CtClass[] {field.getType()});
				continue;
			} catch (Exception e) {
			}
			
			stringBuilder.append("public void ")
					.append(methodName)
					.append("(").append(field.getType().getName()).append(" ").append(field.getName())
					.append(") {").append(FileUtil.NEW_LINE)
					.append("   this.").append(field.getName())
					.append(" = ").append(field.getName()).append(";").append(FileUtil.NEW_LINE)
					.append("}").append(FileUtil.NEW_LINE)
					;		
			String method = stringBuilder.toString();
			stringBuilder.delete(0, stringBuilder.length());
			CtMethod me = CtMethod.make(method, ctClass);
			ctClass.addMethod(me);
		}
		return null;			
	}
	
	
	private static String firstToUpper(String input) {
		char[] tempArray = input.toCharArray();
		tempArray[0] = Character.toUpperCase(tempArray[0]);
		return new String(tempArray);
	}
	
	
	public static void main(String[] args) throws NotFoundException {
		
//		String fullClassName = "com.game37.protobuf4j.proto.OutJava";
//		CtClass cl = ClassPool.getDefault().get(fullClassName);
//		
//		List<CtField> outList = getCtFieldList(cl);
//		outList.forEach(temp->System.out.println(temp));
		
		testStrSubstitutor();
	}
	
	private static void testStrSubstitutor() throws NotFoundException {
		
		String fullClassName = "com.game37.protobuf4j.proto.OutJava";
		CtClass cl = ClassPool.getDefault().get(fullClassName);
		int size = 500;
		List<Thread> list = new ArrayList<>(size);
		do {
			list.add(new Thread(()->{
				try {
					String out = makeMethodValueToEnum(cl, fullClassName, TPL_METHOD_VALUE_TO_ENUM);
					if(out==null) {
						System.out.println(Thread.currentThread().getName()+"=null");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}));
			--size;
		}while(size>0);
		list.forEach(temp->temp.start());
		
	}
	
	
	private static CtField getCtField(CtClass ctClass, String fieldName) {
		try {
			if(ctClass.isFrozen()) {
				ctClass.defrost();
			}
			return ctClass.getDeclaredField(fieldName);
		} catch (Exception e) {
		}
		return null;
	}
	
	public static List<CtField> getCtFieldList(CtClass cl){
		List<CtField> outList = new ArrayList<>();
		return getCtFieldList0(outList, cl);
	}
	
	private static List<CtField> getCtFieldList0(List<CtField> outList, CtClass cl){
		
		for(CtField f:cl.getDeclaredFields()) {
			outList.add(f);
		}
		
		try {
			if(cl.isFrozen()) {
				cl.defrost();
			}
			if(cl.getSuperclass()!=null) {				
				CtClass parent = ClassPool.getDefault().get(cl.getSuperclass().getName());
				if(parent==null||parent.isInterface()) {
				}else {			
					outList = getCtFieldList0(outList, parent);
				}			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return outList;
	}
	
	
}
