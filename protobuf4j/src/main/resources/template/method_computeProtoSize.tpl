public int computeProtoSize() {
    	int size = 0;
    	if(this.__computeSize > 0){
    		return this.__computeSize;
    	}
  
     
${dynamicContent}  

 		this.__computeSize = size;
    	return size;
}