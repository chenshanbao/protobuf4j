/**
 * 
 */
package com.game37.protobuf4j.monitor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
* author chenshanbao
**/

public class MonitorUtil {
	
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	
	private static String TYPE_ENCODE = "encode";
	private static String TYPE_DECODE = "decode";
	
	public static void checkAndLogEncode(String className, byte[] data, byte[] dataNew) {
		
		executorService.execute(()->checkAndLogEncode0(className, data, dataNew));
		
	}
		
	private static void checkAndLogEncode0(String className, byte[] data, byte[] dataNew) {
		
		if(data.length != dataNew.length) {
			//			 
			FileUtil.writeData(wrapperString(className, data, dataNew, TYPE_ENCODE));
		}else {
			for(int i=0; i<data.length;i++) {
				if(data[i]!=dataNew[i]) {
					FileUtil.writeData(wrapperString(className, data, dataNew, TYPE_ENCODE));
					return;
				}
			}			
		}		
	}
	
	public static void checkAndLogDecode(String className, byte[] data, Object obj, Object objNew) {
		executorService.execute(()->checkAndLogDecode0(className, data, obj, objNew));
	}
	private static void checkAndLogDecode0(String className, byte[] data, Object obj, Object objNew) {
		
		String s1  = JSONObject.toJSONString(obj, SerializerFeature.MapSortField, SerializerFeature.SortField);
		String s2  = JSONObject.toJSONString(objNew, SerializerFeature.MapSortField, SerializerFeature.SortField);
		if(s1.equals(s2)) {
			return;
		}	
		FileUtil.writeData(wrapperString(className, data, TYPE_DECODE));
		
	}
	
	public static void analyze(LocalDate date) throws IOException {
		List<String> list =FileUtil.readFile(date);
		list.forEach(temp->System.out.println(temp));
	}
	
	
	private static String wrapperString(String className, byte[] data, String type) {
		JSONObject out = new JSONObject();
		out.put("type", type);
		out.put("data", data);
		out.put("className", className);
		return out.toString();
	}
	
	private static String wrapperString(String className, byte[] data, byte[] dataNew, String type) {
		JSONObject out = new JSONObject();
		out.put("type", type);
		out.put("data", data);
		out.put("dataNew", dataNew);
		out.put("className", className);
		return out.toString();
	}
	
}
