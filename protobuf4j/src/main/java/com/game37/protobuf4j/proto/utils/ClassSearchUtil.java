/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javassist.ClassPool;
import javassist.NotFoundException;

/**
* author chenshanbao
* create time 2020年3月4日 
**/

public class ClassSearchUtil {
	
	public static void addClassPathToJavassist(String path) throws NotFoundException {
		ClassPool.getDefault().appendClassPath(path);	
		System.out.println("add to classpath="+path);
	}
	
	public static void addPathToClassPath(File dir)
			throws NoSuchMethodException, SecurityException, 
			IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, MalformedURLException, NotFoundException {
		
		if(dir==null || (!dir.exists())) {
			return;
		}
		
		URL url = dir.toURI().toURL();
		System.out.println("add to classpath = "+url);
		Method method = URLClassLoader.class.getDeclaredMethod("addURL",new Class[]{URL.class});
		method.setAccessible(true);
		method.invoke(ClassLoader.getSystemClassLoader(), new Object[] {url});		
		
	}
	
	public static List<String> findClassNameByPackageListString(String packageList) {		
		return findClassName(changeToList(packageList));
	}
	
	public static List<String> findClassNameByPackageListStringAndDir(String packageList, String dir) {
		return findClassNameByDir(changeToList(packageList), dir);
	}
	
	private static List<String> changeToList(String packageList) {
		if(packageList!=null) {
			List<String> packList = new ArrayList<String>();
			if(packageList.contains(";")) {
				String[] array = packageList.split(";");
				for(String s:array) {
					packList.add(s);
				}
			}else {
				packList.add(packageList);
			}
			return packList;
		}
		return Collections.emptyList();		
	}
	
	
	
	public static List<String> findClassName(List<String> packageList) {
		List<String> out = new ArrayList<String>();
		packageList.forEach(temp -> findClassName(temp, out));
		return out;
	}
	
	public static List<String> findClassNameByDir(List<String> packageList, String dir) {
		List<String> out = new ArrayList<String>();
		packageList.forEach(temp -> findClassNameByDir(dir, temp, out));
		return out;
	}
	
	public static List<String> findClassName(String packageName, List<String> out) {
		
		String pathName = packageName.replaceAll("\\.", "/");
		System.out.println(pathName);
		System.out.println(com.game37.protobuf4j.monitor.FileUtil.PATH);
		//System.out.println(File.separatorChar);
		File file = new File(Thread.currentThread()
				.getContextClassLoader().getResource(pathName).getFile());
		findClassName0(file, packageName, out);
		return out;
	}
	
	public static List<String> findClassNameByDir(String dir, String packageName, List<String> out) {	
		//
		System.out.println("dir="+dir);
		System.out.println("dir="+com.game37.protobuf4j.monitor.FileUtil.PATH);
		File file = new File(dir);
		findClassName0(file, packageName, out);
		return out;
	}
	
	
	private static void findClassName0(File file, String packageName,List<String> out) {
		File[] fArray = file.listFiles();
		if(fArray == null) {
			return;
		}
		for(File f:fArray) {
			if(f.isFile() && f.getName().endsWith(".class")) {				
				
				//System.out.println(f.getAbsolutePath());
				//
				String fName = null;
				if(System.getProperties().get("os.name").toString().toLowerCase().contains("win")) {				
					fName = f.getAbsolutePath().replaceAll("\\"+File.separatorChar, ".");
				} else {
					fName = f.getAbsolutePath().replaceAll("/"+File.separatorChar, ".");
				}

				int index = fName.lastIndexOf(packageName);
				if(index < 0) {
					System.out.println("not match================");
					System.out.println(fName);
					System.out.println(packageName);
					System.out.println("not match================end====");
					continue;
				}
				fName = fName.substring(index);
				fName = fName.substring(0, fName.length()-6);
				out.add(fName);
				
			}else if(f.isDirectory()) {
				findClassName0(f, packageName, out);
			}
		}		
	}
		
}
