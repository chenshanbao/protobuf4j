public Object  decode(byte[] data) {
	com.google.protobuf.CodedInputStream input = com.google.protobuf.CodedInputStream.newInstance(data);
	return readFrom(input);
}	