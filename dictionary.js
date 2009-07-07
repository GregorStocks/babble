var wordlist = ["butts",  "bums", "buttes", "uranus", "italy", "frankly i am offended by the preceding terms", "butt", "-y", "-s", "hurf", ",", "!", ".", "this is a very long word! why its not even a word at all!", "test", "bums", "test test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "test", "this one is pretty long too, in fact it might even be longer than the others!!!!!", "wheee!", "I", "is"];

var SUFFIX = "suffix";
var PREFIX = "prefix";
var ENDING_PUNCTUATION = "ending punctuation"; // ! . ? etc
var NONENDING_PUNCTUATION = "nonending punctuation"; // , ; : etc

var dictionary = {
	// any words not specified in the dictionary are assumed to be ordinary, well-behaved English
	// words without any special cases or anything.
	// unfortunately, JSON is so smart that it lets you have unquoted strings as keys, so we can't
	// use constants as keys. hopefully nobody ever mistypes "type".
	"-s": {
		type: SUFFIX 
	},
	"-y": {
		type: SUFFIX
	},
	"-ly": {
		type: SUFFIX
	},
	"ultra-": {
		type: PREFIX
	},
	"butt": {
		"-s": "buttz",
		"-ly": "buttily"
	},
	"test": {
		"-ed" : "testized"
	},
	"I": {
		"is": "I'm"
	},
	"!": {
		type: ENDING_PUNCTUATION
	},
	".": {
		type: ENDING_PUNCTUATION
	},
	"?": {
		type: ENDING_PUNCTUATION
	},
	",": {
		type: NONENDING_PUNCTUATION
	},
	";": {
		type: NONENDING_PUNCTUATION
	},
	":": {
		type: NONENDING_PUNCTUATION
	}
};
