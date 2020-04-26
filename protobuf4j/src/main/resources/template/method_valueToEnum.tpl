public static ${fullClassName} valueToEnum(int value) {
	${fullClassName}[] array = ${fullClassName}.values();
	for(int i=0;i<array.length;i++) {
		if(array[i].value()==value) {
			return array[i];
		}
	}
	return null;
}	

		