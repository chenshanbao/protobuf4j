/**
 * 
 */
package com.game37.protobuf4j.codec;

/**
* author chenshanbao
* create time 2020年3月9日
**/

public interface ProtobufCodec<T> {

	public T decode(byte[] data);
	
	public byte[] encode() throws java.io.IOException;
}
