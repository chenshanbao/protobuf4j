/**
 * 
 */
package com.game37.protobuf4j.monitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
* author chenshanbao
**/

public class FileUtil {
	
	public static String  PATH = System.getProperty("user.dir")+File.separator;
	public  static String NEW_LINE  = System.getProperty("line.separator");
	private static String logFileName = "monitorLog.txt";
	
	private static long   delayMillis = 1000*10;
		
	private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
	 
	private static ConcurrentLinkedQueue<String> queue =  new ConcurrentLinkedQueue<String>();
	
	static {
		executorService.scheduleWithFixedDelay(()->{
			try {
				saveData();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}						
		}, delayMillis, delayMillis, TimeUnit.MILLISECONDS);
	}
	
	/**
	 *  not thread safe
	 * @param data
	 * @throws IOException
	 */
	public static void writeData(String data){
		
		queue.add(data);
		
	}
	
	private static void saveData() throws IOException{
		
		String data = queue.poll();		
		if(data == null) {
			return;
		}
		File file = new File(PATH+getYearMonthDay(LocalDate.now())+logFileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));		
		try {			
			while(data!=null) {		
				bw.write(data);
				bw.write(NEW_LINE);
				data = queue.poll();
			}
		}finally {		
			if(bw!=null) {				
				bw.close();
			}
		}				
	}
	
	public static List<String> readFile(LocalDate date) throws IOException{
		
		File file = new File(PATH+getYearMonthDay(date)+logFileName);
		if(!file.exists()) {
			return Collections.emptyList();
		}
		List<String> list = new ArrayList<>();
		BufferedReader bw = new BufferedReader(new FileReader(file));	
		String line = null;
		try {
			while((line = bw.readLine())!=null) {
				list.add(line);
			}		
		}finally {
			if(bw!=null)
				bw.close();
		}	
		return list;
	}
	
	
	private static String getYearMonthDay(LocalDate date) {
		long out = date.getYear()*10000+date.getMonthValue()*100+date.getDayOfMonth();
		return Long.toString(out);
	}
	
	public static void main(String[] args) throws IOException {
		System.out.println(NEW_LINE+PATH);
		System.out.println(NEW_LINE);		
		System.out.println(LocalDate.now().equals(LocalDate.now()));
		System.out.println(getYearMonthDay(LocalDate.now()));
		//
		//MonitorUtil.analyze(LocalDate.now());
	}
	

}
