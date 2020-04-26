case __tagNumber__:{
	int length = input.readRawVarint32();
	final int oldLimit = input.pushLimit(length);
	if( out.__fieldName__ == null ){
		out.__fieldName__ = new java.util.HashMap();
	} 			
			
	__decodeMapSubKeyClass__ mapValue1 = null;
	__decodeMapSubValueClass__ mapValue2 = null;
				
	__decodeMapSub__
	
	if(mapValue1!=null && mapValue2!=null){
		out.__fieldName__.put(mapValue1, mapValue2);
	}
	input.popLimit(oldLimit);
	break;
}