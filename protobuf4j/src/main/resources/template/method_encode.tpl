public byte[] encode() throws java.io.IOException{
			 
	int size = computeProtoSize();
	byte[] data = new byte[size];
	com.google.protobuf.CodedOutputStream output = com.google.protobuf.CodedOutputStream.newInstance(data);
	try{
		writeTo(output);
	}finally{
		if(com.game37.protobuf4j.proto.utils.GlobalConfig.getOpenLogFlag()) {
			System.out.println(java.util.Arrays.toString(data));
		}
	}	
	return data;
}