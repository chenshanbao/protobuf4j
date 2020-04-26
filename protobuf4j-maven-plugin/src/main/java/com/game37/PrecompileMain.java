/**
 * 
 */
package com.game37;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;

import com.game37.protobuf4j.annotation.ProtobufClass;
import com.game37.protobuf4j.annotation.ProtobufField;
import com.game37.protobuf4j.proto.utils.ClassModifyUtil;
import com.game37.protobuf4j.proto.utils.ClassSearchUtil;

/**
* author chenshanbao
* create time 2020年3月17日 下午5:31:56
**/

public class PrecompileMain {

	/**
	 * @param args
	 * @throws MojoExecutionException 
	 */
	public static void main(String[] args) throws MojoExecutionException {
		// 
		String scanPackageList = null;
		String targetAnnoList = null;
		String outputDir = null;
		
		if(args.length == 0) {
			System.out.println("please input scanPackageList");
			return;
		}
		
		scanPackageList = args[0];
		
		if(args.length >= 2) {
			targetAnnoList = args[1];
		}
		
		if(args.length>=3) {
			outputDir = args[2];
		}else {
			outputDir = "./target";
		}
		
		execute(scanPackageList, targetAnnoList, outputDir, true);

	}
	
	public static void execute(String scanPackageList, String targetAnnoList, String outputDir) throws MojoExecutionException {
		execute(scanPackageList, targetAnnoList, outputDir, false);
		
	}
	public static void execute(String scanPackageList, String targetAnnoList, String outputDir, boolean callFromMain) throws MojoExecutionException {
		
		//
		List<String> classList = 
				callFromMain?ClassSearchUtil.findClassNameByPackageListString(scanPackageList):
					ClassSearchUtil.findClassNameByPackageListStringAndDir(scanPackageList, outputDir);
		if(classList.isEmpty()) {				
			System.out.println("==========>scan class size is zero!");
			return;
		}
		System.out.println("==========>scan class size is "+classList.size());
		//
		Set<String> annoSet = new HashSet<String>();
		if(targetAnnoList==null||"".equals(targetAnnoList)) {
			annoSet.add(ProtobufClass.class.getName());
			annoSet.add(ProtobufField.class.getName());
		}else {
			if(targetAnnoList.contains(";")) {
				String[] array = targetAnnoList.split(";");
				for(String s:array) {
					annoSet.add(s);
				}
			}else {
				annoSet.add(targetAnnoList);
			}			
		}
		
		if(outputDir==null||"".equals(outputDir)) {
			outputDir = ".";
		}
		
		
		for(String className:classList) {			
			try {
				ClassModifyUtil.visitClass(className, annoSet);
			} catch (Exception e) {
				throw new MojoExecutionException("", e);
			}
		}
		
		try {
			ClassModifyUtil.makeDynamicMethod(annoSet, outputDir);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new MojoExecutionException("", e);
		}
				
	}
	

}
