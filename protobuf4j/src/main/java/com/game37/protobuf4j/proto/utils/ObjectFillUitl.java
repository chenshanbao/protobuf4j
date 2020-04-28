/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;


/**
* author chenshanbao
**/

public class ObjectFillUitl {
	
	private final static ThreadLocalRandom  random = ThreadLocalRandom.current();
	
	private final static Map<Class<?>, Supplier<?>> map = new ConcurrentHashMap<>();
	static {
		
		map.put(boolean.class, ()->{return random.nextBoolean();});
		map.put(Boolean.class, ()->{return (Boolean)random.nextBoolean();});
		
		map.put(int.class, ()->{return random.nextInt(Integer.MAX_VALUE);});
		map.put(Integer.class, ()->{return (Integer)random.nextInt(Integer.MAX_VALUE);});
		
		map.put(long.class, ()->{return random.nextLong(Long.MAX_VALUE);});
		map.put(Long.class, ()->{return (Long)random.nextLong(Long.MAX_VALUE);});
		
		map.put(double.class, ()->{return random.nextDouble(Double.MAX_VALUE);});
		map.put(Double.class, ()->{return (Double)random.nextDouble(Double.MAX_VALUE);});
		
		map.put(float.class, ()->{return random.nextFloat();});
		map.put(Float.class, ()->{return (Float)random.nextFloat();});	
		
		
		String[] chinaArray = new String[] {"你","好","大","中","国"};
		char[] charArray = new char[] {'a','b','c','d','e'};
		map.put(String.class, ()->{						
			return chinaArray[random.nextInt(chinaArray.length)]
					+charArray[random.nextInt(charArray.length)]
					+random.nextInt();
		});
				
		map.put(byte[].class, ()->{
			int size = 10;
			byte[] data = new byte[size];
			for(int i =0;i<size;i++) {
				data[i] = (byte)random.nextInt(128);
			}
			return data;
		});	
		
		
	}
	
	public static List<Field> getFiledList(Class<?> cl){
		
		List<Field> outList = new ArrayList<>();
		return getFiledList0(cl, outList);
		
	}
	public static List<Field> getFiledList0(Class<?> cl, List<Field> outList){
		
		for(Field f:cl.getDeclaredFields()) {
			outList.add(f);
		}
		if(cl.isInterface()||Object.class.equals(cl)) {
			return outList;
		}
		return getFiledList0(cl.getSuperclass(), outList);
	}
	
	public static Object fillObject(Object obj) throws IllegalArgumentException, 
		IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, InstantiationException {
		return fillObject(obj, null);
	}
	
	public static Object fillObject(Object obj, Supplier<Boolean> supplier) throws IllegalArgumentException, 
		IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, InstantiationException {
		return fillObject(obj, false, supplier);
	}
	
	public static Object fillObject(Object obj, boolean printInfo, Supplier<Boolean> supplier) throws IllegalArgumentException, 
			IllegalAccessException, NoSuchMethodException, SecurityException, InvocationTargetException, InstantiationException {
		
		//
		if(printInfo) {
			System.out.println(obj.getClass().getName());
		}
		//
		for(Field f:getFiledList(obj.getClass())) {
			//
			if(!needFill(f.getModifiers())) {
				continue;
			}
			//未命中，直接过滤；
			if(supplier!=null && !supplier.get()){
				continue;
			}
			//
			f.setAccessible(true);
			Class<?> type = f.getType();
			boolean isList = false;
			if((isList = isType(type, List.class))||
					isType(type, Set.class)) {
				//
				Collection<Object> list = null;
				if(isList) {
					list = type.isInterface()?new ArrayList<>():(Collection<Object>)type.newInstance();
				}else {
					list = type.isInterface()?new HashSet<>():(Collection<Object>)type.newInstance();
				}
				//
				f.set(obj, list);
				Class<?> eleType = getGenericType(f, 0);
				if(eleType!=null) {
					if(eleType.equals(obj.getClass())) {
						continue;
					}
					for(int i=0;i<10;i++) {
						Object temp = getRandomValue(eleType);
						if(temp==null) {
							if(needFill(eleType.getModifiers())) {									
								list.add(fillObject(eleType.newInstance(), printInfo, supplier));
							}
						}else {
							list.add(temp);
						}
					}
				}
			}else if(isType(type, Map.class)) {
				Map tempMap = type.isInterface()?new HashMap<>():(Map)type.newInstance();
				Class<?> keyType = getGenericType(f, 0);
				Class<?> valueType = getGenericType(f, 1);
				if(keyType!=null && valueType!=null) {
					if(keyType.equals(obj.getClass())||
							valueType.equals(obj.getClass())) {
						continue;
					}
					for(int i=0;i<10;i++) {
						Object key = getRandomValue(keyType);
						if(key==null) {
							if(needFill(keyType.getModifiers())) {							
								key = fillObject(keyType.newInstance(), printInfo, supplier);
							}
						}
						Object value = getRandomValue(valueType);
						if(value==null) {
							if(needFill(valueType.getModifiers())) {								
								value = fillObject(valueType.newInstance(), printInfo, supplier);
							}
						}
						if(key!=null&&value!=null) {							
							tempMap.put(key, value);
						}
					}
				}
				f.set(obj, tempMap);
			}else{
				//base
				Object temp =getRandomValue(type);
				if(temp!=null) {
					f.set(obj, temp);					
				}else {
					//
					if(needFill(f.getModifiers())
							&&needFill(type.getModifiers())) {
						f.set(obj, fillObject(type.newInstance(), printInfo, supplier));	
					}					
				}				
			}												
		}		
		return obj;
	}
	
	private static boolean needFill(int modifiers) {
		if(Modifier.isInterface(modifiers))
			return false;
		if(Modifier.isAbstract(modifiers))
			return false;
		if(Modifier.isTransient(modifiers))
			return false;
		if(Modifier.isStatic(modifiers))
			return false;
		return true;
	}
	
	private static Object getRandomValue(Class<?> type) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//
		if(type.isEnum()) {
			return getEnum(type);
		}else {					
			Supplier<?> sup = map.get(type);
			if(sup == null) {
				return null;
			}
			return sup.get();
		}
	}
	
	public static Class<?> getGenericType(Field f, int index){
		Type type = f.getGenericType();
		if(type instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) type;
            // 得到泛型里的class类型对象
            Class<?> actualTypeArgument = (Class<?>)pt.getActualTypeArguments()[index];
			return actualTypeArgument;
		}
		return null;
	}
	
	public static Object getEnum(Class<?> type) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//
		Object[] array = type.getEnumConstants();
		return array[random.nextInt(array.length)];
	}
	
	public static boolean isType(Class<?> type, Class<?> targetClass) {
		if(type.equals(targetClass)) {
			return true;
		}
		for(Class<?> c:type.getInterfaces()) {
			if(c.equals(targetClass)) {
				return true;
			}
		}
		Class<?> sup = type.getSuperclass();
		if(sup != null) {		
			return isType(sup, targetClass);
		}
		return false;
	}
	
	/**
	 * 	如果属性 含有set集合，集合元素一定要重写hash和equals方法
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception
	 */
	
	public static boolean deepEquals(Object a, Object b, boolean printFalseInfo) throws Exception{
				
		if(a == b) {
			return true;
		}else if(a == null || b == null) {
			return false;
		}else {
			if(a.getClass().isPrimitive() ||
					a.getClass().isEnum() ||
					a.getClass().getName().startsWith("java.lang.")) {
				boolean out = Objects.deepEquals(a, b);
				if(printFalseInfo && !out) {
					System.out.println(a);
					System.out.println(b);
				}
				return out;
			}
			
			if(!a.getClass().equals(b.getClass())) {
				if(printFalseInfo) {
					System.out.println(a.getClass());
					System.out.println(b.getClass());
				}
				return false;
			}			
			List<Field> fieldList = getFiledList(a.getClass());
			int size = fieldList.size();
			if(size == 0) {
				return true;
			}
			
			boolean out = false;
			if(a.getClass().isArray()) {
				//
				if(a instanceof Object[]) {
					return equalsArray(a, b, printFalseInfo);
				}else {
					return Objects.deepEquals(a, b);
				}
			}else if(isType(a.getClass(), List.class)) {
				return equalsList(a, b, printFalseInfo);
			}else if(isType(a.getClass(), Set.class)) {
				//set 只能对比hashcode的累加值了；
				return equalsSet(a, b, printFalseInfo);
			}else if(isType(a.getClass(), Map.class)) {
				return equalsMap(a, b, printFalseInfo);
			}else {				
				for(int i=0;i<size;i++) {
					Field f = fieldList.get(i);
					if(!f.isAccessible()) {
						f.setAccessible(true);
					}
					Object tempA = f.get(a);
					Object tempB = f.get(b);
					out = deepEquals(tempA, tempB, printFalseInfo);
					if(!out) {
						if(printFalseInfo && !out) {
							System.out.println("field="+f.getName());
							System.out.println(tempA);
							System.out.println(tempB);
						}
						return out;
					}
				}	
			}									
			return out;
		}
	}
	
	public static boolean equalsArray(Object a, Object b, boolean printFalseInfo) throws Exception{
		boolean out = false;
		int size = 0;
		Object[] aArray = (Object[])a;
		Object[] bArray = (Object[])b;
		if((size = aArray.length) != bArray.length) {
			return false;
		}
		if(size == 0) {
			return true;
		}
		for(int i=0;i<size;i++) {
			out = deepEquals(aArray[i], bArray[i], printFalseInfo);
			if(!out) {
				return out;
			}
		}
		return out;
	}
	
	public static boolean equalsList(Object a, Object b, boolean printFalseInfo) throws Exception{
		boolean out = false;
		int size = 0;
		@SuppressWarnings("unchecked")
		List<Object> aArray = (List<Object>)a;
		@SuppressWarnings("unchecked")
		List<Object> bArray = (List<Object>)b;
		if((size = aArray.size()) != bArray.size()) {
			return false;
		}
		if(size == 0) {
			return true;
		}
		for(int i=0;i<size;i++) {
			out = deepEquals(aArray.get(i), bArray.get(i), printFalseInfo);
			if(!out) {
				return out;
			}
		}
		return out;
	}
	
	public static boolean equalsMap(Object a, Object b, boolean printFalseInfo) throws Exception{
		boolean out = false;
		int size = 0;
		@SuppressWarnings("unchecked")
		Map<Object,Object> aArray = (Map<Object,Object>)a;
		@SuppressWarnings("unchecked")
		Map<Object,Object> bArray = (Map<Object,Object>)b;
		if((size = aArray.size()) != bArray.size()) {
			return false;
		}
		if(size == 0) {
			return true;
		}
		Set<Map.Entry<Object, Object>> set = aArray.entrySet();
		Iterator<Map.Entry<Object, Object>> it = set.iterator();
		while(it.hasNext()) {
			Map.Entry<Object, Object> en = it.next();
			Object key = en.getKey();
			Object value = en.getValue();
			Object value2 = bArray.get(key);
			
			out = deepEquals(value, value2, printFalseInfo);
			if(!out) {
				return out;
			}						
		}
		return out;
	}
	
	
	public static boolean equalsSet(Object a, Object b, boolean printFalseInfo) throws Exception{

		int size = 0;
		@SuppressWarnings("unchecked")
		Set<Object> aArray = (Set<Object>)a;
		@SuppressWarnings("unchecked")
		Set<Object> bArray = (Set<Object>)b;
		if((size = aArray.size()) != bArray.size()) {
			return false;
		}
		if(size == 0) {
			return true;
		}
		//
		boolean flag = true;
		Iterator<Object> it = aArray.iterator();
		while(it.hasNext()) {
			Object value = it.next();
			if(!bArray.contains(value)) {
				return false;
			}
		}
		return flag;
	}

}
