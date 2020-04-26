boolean doneSub = false;
while (!doneSub) {
	int tagSub = input.readTag();
	switch (tagSub) {
		case 0:
			doneSub = true;
			break;
		__decodeMapSubTpl__
		default: {
			input.skipField(tagSub);
			doneSub = true;
			break;
		}
	}
}