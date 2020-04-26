case __tagNumber__:{
	int lengthSub = input.readRawVarint32();
	final int oldLimitSub = input.pushLimit(lengthSub);
	mapValue__fieldNumber__ = __readStringPackage__;
	input.popLimit(oldLimitSub);
	break;
}