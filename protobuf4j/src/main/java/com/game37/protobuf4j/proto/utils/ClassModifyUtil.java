/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.game37.protobuf4j.proto.utils.enums.TypeWrapperEnum;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.FieldInfo;

/**
* author chenshanbao
**/

public class ClassModifyUtil {
	
	private static Map<String,LockObject> classLockMap = new ConcurrentHashMap<String,LockObject>();
	
	private static List<Set<String>> list = new ArrayList<Set<String>>();
	
	private static Map<Integer,Predicate<CtField>> checkMap = new ConcurrentHashMap<Integer,Predicate<CtField>>();
	
	private static volatile boolean isOpenMultiThread = false;
	
	public static void preCompile(String targetFullClassName) throws Exception{
		preCompile(targetFullClassName, null);
	}
	
	public static void preCompile(String targetFullClassName
								, String classSaveDir) throws Exception{
				
		if(targetFullClassName == null ||targetFullClassName.trim().length() == 0) {				
			throw new RuntimeException("targetFullClassName is null");
		}
		
		List<String> targetFullClassNameList = new ArrayList<String>(1);
		targetFullClassNameList.add(targetFullClassName);
		
		preCompile(targetFullClassNameList, null, classSaveDir);
		
	}
	
	public static void preCompile(List<String> targetFullClassNameList
			) throws Exception{
		preCompile(targetFullClassNameList, null, null);
	}
	
	public static void preCompile(List<String> targetFullClassNameList
				, String classSaveDir) throws Exception{
		preCompile(targetFullClassNameList, null, classSaveDir);
	}
	
	public synchronized static void preCompile(List<String> targetFullClassNameList
			, Set<String> annoClassSet, String classSaveDir) 
			throws Exception{
		
		try {
			if(targetFullClassNameList == null ||targetFullClassNameList.size() == 0) {				
				throw new RuntimeException("targetFullClassNameList is null or size is zero");
			}
			
			BiConsumer<String, Set<String>> con = 
					FunctionUtil.wrapCheckedBiConsumer(ClassModifyUtil::visitClass);
			targetFullClassNameList.forEach(temp-> con.accept(temp, annoClassSet));
			
			makeDynamicMethod(null, classSaveDir);
		}finally {
			list.clear();
		}		
		
		
	}
	
	public static void preCompileByPackScan(List<String> packScanList, 
			Set<String> annoClassSet) throws Exception{		
		preCompileByPackScan(packScanList, annoClassSet, null);
	}
	
	public synchronized static void preCompileByPackScan(List<String> packScanList, 
											Set<String> annoClassSet, String classSaveDir) throws Exception{		
		try {
			if(packScanList == null ||packScanList.size() == 0) {				
				throw new RuntimeException("packScanList is null or size is zero");
			}
			if(annoClassSet == null ||annoClassSet.size() == 0) {				
				throw new RuntimeException("annoClassSet is null or size is zero");
			}
			

			List<String>  listClass = ClassSearchUtil.findClassName(packScanList);	
			
			BiConsumer<String, Set<String>> con = 
					FunctionUtil.wrapCheckedBiConsumer(ClassModifyUtil::visitClass);
			listClass.forEach(temp-> con.accept(temp, annoClassSet));
			
			makeDynamicMethod(annoClassSet, classSaveDir);
		}finally {
			list.clear();
		}		
	}
	
	
	public static synchronized void visitAllClass(Consumer<String> consumer) {
		list.forEach(temp->{
			temp.forEach(className->consumer.accept(className));
		});
	}
	
	public static List<Set<String>> getVisitAllClassList(){
		return list;
	}
	
	public static void setMultiThreadFlag(boolean flag) {
		isOpenMultiThread = flag;
	}
	
	protected static boolean checkCtField(CtField ctField) {
		
		boolean isTransient = 
				((AccessFlag.TRANSIENT|ctField.getModifiers())^ctField.getModifiers()) == 0;
		if(isTransient) {
			return false;
		}		
		//程序自动生成的过滤率掉；
		if(ctField.getName().startsWith("__")) {
			return false;
		}
		
		if(checkMap.isEmpty()) {
			return true;
		}
		for(Map.Entry<Integer, Predicate<CtField>> en:checkMap.entrySet()) {
			boolean flag = en.getValue().test(ctField);
			if(flag) {
				return true;
			}
		}
		return false;
	}
	
	public static void makeDynamicMethod(Set<String> targetSet) throws Exception {
		makeDynamicMethod(targetSet, null);
	}
	
	public static void makeDynamicMethod(Set<String> targetSet, String classSaveDir) throws Exception {
		//
		final AtomicInteger recompileCount =  new AtomicInteger();
		if(GlobalConfig.getOpenLogFlag()) {
			System.out.println(list);
		}
		//
		if(targetSet!=null&&targetSet.size()>0) {
			Integer hashCode = targetSet.hashCode();
			Predicate<CtField> predi = (ctField) -> {return isHaveAnno(ctField, targetSet);};
			checkMap.put(hashCode, predi);
		}		
		//
		int size = list.size();
		for(int i=size-1;i>=0;i--) {
			Set<String> tempList = list.get(i);
			synchronized (tempList) {
				int tempSize = tempList.size();
				if(isOpenMultiThread) {
					//
					CountDownLatch cdl = new CountDownLatch(tempSize);
					tempList.forEach(fullClassName -> {										
						ForkJoinPool.commonPool().execute(()->{
							try{
								makeDynamicMethod0(fullClassName, targetSet, classSaveDir, recompileCount);	
							}finally {
								cdl.countDown();
							}
						});						
					});		
					cdl.await();
				}else {				
					tempList.forEach(fullClassName -> {										
						makeDynamicMethod0(fullClassName, targetSet, classSaveDir, recompileCount);				
					});				
				}							
			}
		}
		//
		System.out.println("==========>success recompiled class file "+recompileCount.get());
	}
	
	private static void makeDynamicMethod0(String fullClassName, Set<String> targetSet
			, String classSaveDir, AtomicInteger recompileCount){		
		
		LockObject lock = classLockMap.computeIfAbsent(fullClassName, (key)->{return new LockObject();});
		synchronized (lock) {
			if(!lock.isModifySuccess()) {			
				try {
					if(GlobalConfig.getOpenLogFlag()) {					
						System.out.println("modifyClass======"+fullClassName);
					}
					int count = CodeGenerator.makeMethod(fullClassName, classSaveDir);
					if(count > 0) {
						recompileCount.addAndGet(count);
					}
					lock.setModifySuccess(true);
				}catch(Exception exception) {
					exception.printStackTrace();
					System.out.println("modifyClass======failure="+fullClassName);
				}
			}					
		}
	}
	
	public final static String ENUM_CLASS_NAME = "java.lang.Enum";
	
	
	protected static boolean isHaveAnno(CtClass ctClass, Set<String> targetSet) {	
		//
		if(targetSet!=null&&targetSet.size()>0) {
			
			try {
				Object[] array = ctClass.getAvailableAnnotations();
				for(Object obj:array) {
					if(obj instanceof Annotation &&
							targetSet.contains(((Annotation)obj).annotationType().getName())) {
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}else {	
			//如果为空，表示不做判断，都需要检索
				return true;
		}
	}
	
	
	private static boolean isHaveAnno(CtField ctField, Set<String> targetSet) {
		
		//
		if(targetSet!=null&&targetSet.size()>0) {
			
			try {
				if(ctField.getDeclaringClass().isFrozen()) {
					ctField.getDeclaringClass().defrost();
				}
				FieldInfo fi = ctField.getFieldInfo();
				AnnotationsAttribute  ai = (AnnotationsAttribute) fi.getAttribute(AnnotationsAttribute.visibleTag);
				if(ai==null) {
					return false;
				}
				for(javassist.bytecode.annotation.Annotation o:ai.getAnnotations()) {
					//System.out.println("==66=="+o.getTypeName());//Protobuf
					//System.out.println("==66=="+Protobuf.class.getName());
					if(targetSet.contains(o.getTypeName())) {
						//System.out.println("==66=="+o.getTypeName());
						return  true;
					}	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}else {	
			//如果为空，表示不做判断，都需要检索
			return true;
		}
	}
//	
//	private void checkAndAddFieldInfo(CtClass ctClass, int index) {
//		
//		for(CtField field:ctClass.getDeclaredFields()) {
//			isMark(field);
//		}
//		
//	}
	
	
	
	public static void visitClass(String fullClassName, Set<String> annoSet) throws Exception {
		visitClass0(fullClassName, 0, annoSet);
	}
	
	public static void visitClass(String fullClassName) throws Exception {
		visitClass0(fullClassName, 0, null);
	}
	
	private static void visitClass0(String fullClassName, int index, Set<String> annoSet) throws Exception {
		
		CtClass ctClass = ClassPool.getDefault().get(fullClassName);
		if(isHaveAnno(ctClass, annoSet)) {			
			visitClass(fullClassName, index, annoSet);
		}		
	}
	
	private static Set<String> supperClassSet = new HashSet<String>();
	static {
		supperClassSet.add(ENUM_CLASS_NAME);
		supperClassSet.add("java.lang.Object");
	}
		
	private static void visitClass(String fullClassName, int index, Set<String> annoSet) throws Exception {
		
		CtClass ctClass = ClassPool.getDefault().get(fullClassName);
		if(list.size() <= index) {
			synchronized (ClassModifyUtil.class) {
				if(list.size() <= index) {					
					Set<String> tempList = new HashSet<String>();
					list.add(index, tempList);	
				}
			}
		}
		
		//
		if(ctClass.getSuperclass()!=null&&!ctClass.getSuperclass().isInterface()
				&&!supperClassSet.contains(ctClass.getSuperclass().getName())) {
			visitClass(ctClass.getSuperclass().getName(), index+1, annoSet);			
		}				
		TypeWrapperEnum ftParent = TypeUtil.getTypeWrapperEnum(ctClass);
		//FieldType ftParent = TypeUtil.getFieldType(ctClass);
		Set<String> tempList = list.get(index);
		synchronized (tempList) {			
			tempList.add(fullClassName);
		}
		if(TypeWrapperEnum.ENUM.equals(ftParent)) {
			//枚举优先；
			return;
		}
		++index;
		
		for(CtField field:CodeGenerator.getCtFieldList(ctClass)) {
			
			if(!isHaveAnno(field, annoSet)) {
				continue;
			}
			
			//FieldType ft = TypeUtil.getFieldType(field.getType());
			TypeWrapperEnum ft = TypeUtil.getTypeWrapperEnum(field.getType());
			if(TypeWrapperEnum.MESSAGE.equals(ft) 
					|| TypeWrapperEnum.ENUM.equals(ft)
					|| TypeWrapperEnum.MAP.equals(ft)) {
				//
				if(field.getType().subclassOf(CodeGenerator.getListCtClass())||
						field.getType().subclassOf(CodeGenerator.getSetCtClass())
						||field.getType().subclassOf(CodeGenerator.getMapCtClass())) {
							
					if(TypeWrapperEnum.MAP.equals(ft)) {
						MapDesc mapDesc = CodeGenerator.getGenericSignatureMap(field.getGenericSignature());					
						if(!mapDesc.getKeyClassName().equals(ctClass.getName())
								&&(TypeWrapperEnum.MESSAGE.equals(mapDesc.getKeyTypeWrapper()) 
										|| TypeWrapperEnum.ENUM.equals(mapDesc.getKeyTypeWrapper()))) {
							visitClass(mapDesc.getKeyClassName(), index, annoSet);								
						}
						if(!mapDesc.getValueClassName().equals(ctClass.getName())
								&&(TypeWrapperEnum.MESSAGE.equals(mapDesc.getValueTypeWrapper()) 
										|| TypeWrapperEnum.ENUM.equals(mapDesc.getValueTypeWrapper()))) {
							visitClass(mapDesc.getValueClassName(), index, annoSet);	
						}
						continue;
					}				
					String generTypeStr = CodeGenerator.getGenericSignature(field.getGenericSignature());
					if(generTypeStr.equals(ctClass.getName())) {
						continue;
					}
					//FieldType ftTemp = TypeUtil.getFieldType(ClassPool.getDefault().get(generTypeStr));
					TypeWrapperEnum ftTemp = TypeUtil.getTypeWrapperEnum(ClassPool.getDefault().get(generTypeStr));
					//System.out.println(generTypeStr+"=="+ftTemp);
					if(TypeWrapperEnum.MESSAGE.equals(ftTemp) || TypeWrapperEnum.ENUM.equals(ftTemp)) {						
						visitClass(generTypeStr, index, annoSet);		
					}
				}else {
					if(field.getType().getName().equals(ctClass.getName())) {
						continue;
					}
					visitClass(field.getType().getName(), index, annoSet);	
				}				
			}
		}		
	}

	@Override
	public String toString() {
		return "VisitClassObject [list=" + list + "]";
	}
	
	private static class LockObject{
		
		private volatile boolean modifySuccess;

		public boolean isModifySuccess() {
			return modifySuccess;
		}

		public void setModifySuccess(boolean modifySuccess) {
			this.modifySuccess = modifySuccess;
		}						
	}
	
}
