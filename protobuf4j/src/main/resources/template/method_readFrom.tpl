public static ${fullClassName} readFrom(com.google.protobuf.CodedInputStream input) {
				
	${fullClassName} out = new ${fullClassName}();
	${__decodeSetEnumDefault__}
	boolean done = false;
	try {
		while (!done) {
			int tag = input.readTag();
			//System.out.println("tag====="+tag);
			switch (tag) {
				case 0:
					return out;
${dynamicContent}
				default: {
					//input.skipField(tag);
					done = true;
					break;
				}
			}
		}
	} catch (java.io.IOException e) {
		// 
		e.printStackTrace();
	}						
	return out;
}