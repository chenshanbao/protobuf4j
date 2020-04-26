/**
 * 
 */
package com.game37.protobuf4j.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* author chenshanbao
* create time 2020年1月20日 上午11:47:53
**/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ProtobufField {
	
	String  value() default "";

}
