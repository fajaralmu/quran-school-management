function isUpperCase(_char) {
	var str = "";
	str += (_char);
	return str == (str.toUpperCase());
}

function extractCamelCase(camelCased) {

	var result = "";

	for (let i = 0; i < camelCased.length; i++) {
		const _char = camelCased[i];
		if (isUpperCase(_char)) {
			result += (" ");
		}
		result += (_char.toLowerCase());
	}

	return result;
}