public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException{
			 
	${dynamicContent}   
	
	if(com.game37.protobuf4j.proto.utils.GlobalConfig.getOpenLogFlag()) {
		System.out.println("====="+this);
		System.out.println("====="+this.__computeSize);
	}
	
	this.__computeSize = 0;//每次序列化后清理一下
	
}