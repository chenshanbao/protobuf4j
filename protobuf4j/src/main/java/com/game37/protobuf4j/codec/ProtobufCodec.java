/**
 * 
 */
package com.game37.protobuf4j.codec;

/**
* author chenshanbao
* create time 2020年3月9日 下午2:38:33
**/

public interface ProtobufCodec<T> {

	public T decode(byte[] data);
	
	public byte[] encode() throws java.io.IOException;
}
