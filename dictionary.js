var SUFFIX = 'suffix';
var PREFIX = 'prefix';
var ENDING_PUNCTUATION = 'ending punctuation'; // ! . ? etc
var NONENDING_PUNCTUATION = 'nonending punctuation'; // , ; : etc

var dictionary = {
	// any words not specified in the dictionary are assumed to be ordinary,
	// well-behaved English words without any special cases or anything.
	// unfortunately, JSON is so smart that it lets you have unquoted strings
	// as keys, so we can't use constants as keys. hopefully nobody ever 
	// mistypes 'type'.
	'pro-': {
		type: PREFIX
	},
	'anti-': {
		type: PREFIX
	},
	'ultra-': {
		type: PREFIX
	},
	'-ize': {
		type: SUFFIX
	},
	'-ify': {
		type: SUFFIX
	},
	'-s': {
		type: SUFFIX 
	},
	'-y': {
		type: SUFFIX
	},
	'-ly': {
		type: SUFFIX
	},
	'-ed': {
		type: SUFFIX
	},
	'butt': {
		combos: {
			'-s': 'buttz',
			'-ly': 'buttily'
		}
	},
	'test': {
		combos: {
			'-ed' : 'testized'
		}
	},
	'I': {
		combos: {
			'is': 'I\'m'
		}
	},
	'!': {
		type: ENDING_PUNCTUATION
	},
	'.': {
		type: ENDING_PUNCTUATION
	},
	'?': {
		type: ENDING_PUNCTUATION
	},
	',': {
		type: NONENDING_PUNCTUATION
	},
	';': {
		type: NONENDING_PUNCTUATION
	},
	':': {
		type: NONENDING_PUNCTUATION
	}
};
