package com.game37.protobuf4j.proto.utils;

import java.util.function.BiConsumer;

/**
* author chenshanbao
**/

public class FunctionUtil {
	
	@FunctionalInterface
	public interface CheckedBiConsumer<T, U>{
		 void accept(T t, U u) throws Exception;
	}
	
	public static <T,U>  BiConsumer<T, U> wrapCheckedBiConsumer(CheckedBiConsumer<T, U> checkedBiConsumer){
		return (t,u) -> {
			try {				
				checkedBiConsumer.accept(t, u);
			}catch(Exception e) {
				throw new RuntimeException(e);
			}
		};
	}
	

}
