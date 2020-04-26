/**
 * 
 */
package com.game37.protobuf4j.proto.utils;

/**
* author chenshanbao
**/

public class GlobalConfig {
	
	/**
	 * 解码时是否设置枚举类型的默认值
	 */
	public static boolean decodeSetEnumDefault = true;
	
	/**
	 * 解码枚举是否增加优化代码；
	 */
	public static boolean addEnumOptimizeCode = false;
	
	/**
	 *是否增加生成 get set 方法； 
	 */
	public static boolean generateGetAndSetMethod = false;
	
	

	public static boolean isGenerateGetAndSetMethod() {
		return generateGetAndSetMethod;
	}

	public static void setGenerateGetAndSetMethod(boolean generateGetAndSetMethod) {
		GlobalConfig.generateGetAndSetMethod = generateGetAndSetMethod;
	}

	public static boolean isDecodeSetEnumDefault() {
		return decodeSetEnumDefault;
	}

	public static void setDecodeSetEnumDefault(boolean decodeSetEnumDefault) {
		GlobalConfig.decodeSetEnumDefault = decodeSetEnumDefault;
	}

	public static boolean isAddEnumOptimizeCode() {
		return addEnumOptimizeCode;
	}

	public static void setAddEnumOptimizeCode(boolean addEnumOptimizeCode) {
		GlobalConfig.addEnumOptimizeCode = addEnumOptimizeCode;
	}	
	
	/**
	 *是否开启日志
	 */
	private static boolean isOpenLog = false;
	
	public static void setOpenLogInfoFlag(boolean flag) {
		isOpenLog = flag;
	}

	public static boolean getOpenLogFlag() {
		return isOpenLog;
	}
	

}
