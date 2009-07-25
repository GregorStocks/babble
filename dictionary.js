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
	'post-': {
		type: PREFIX
	},
	'pre-': {
		type: PREFIX
	},
	'de-': {
		type: PREFIX
	},
	'super-': {
		type: PREFIX
	},
	'ex-': {
		type: PREFIX
	},
	'un-': {
		type: PREFIX
	},
	'tri-': {
		type: PREFIX
	},
	'centi-': {
		type: PREFIX
	},
	'kilo-': {
		type: PREFIX
	},
	'milli-': {
		type: PREFIX
	},
	'multi-': {
		type: PREFIX
	},
	'semi-': {
		type: PREFIX
	},
	'mis-': {
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
	'-ing': {
		type: SUFFIX
	},
	'-able': {
		type: SUFFIX
	},
	'-ic': {
		type: SUFFIX
	},
	'-est': {
		type: SUFFIX
	},
	'-ness': {
		type: SUFFIX
	},
	'-icious': {
		type: SUFFIX
	},
	'-al': {
		type: SUFFIX
	},
	'-ful': {
		type: SUFFIX
	},
	'-holic': {
		type: SUFFIX
	},
	'-ism': {
		type: SUFFIX
	},
	'-ist': {
		type: SUFFIX
	},
	'-itude': {
		type: SUFFIX
	},
	'-less': {
		type: SUFFIX
	},
	'-phagia': {
		type: SUFFIX
	},
	'-ee': {
		type: SUFFIX
	},
	'-ese': {
		type: SUFFIX
	},
	'-ate': {
		type: SUFFIX
	},
	'-cide': {
		type: SUFFIX
	},
	'-er': {
		type: SUFFIX
	},
	'-fy': {
		type: SUFFIX
	},
	'-ism': {
		type: SUFFIX
	},
	'-ize': {
		type: SUFFIX
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
	'...': {
		type: NONENDING_PUNCTUATION
	},
	'-': {
		type: NONENDING_PUNCTUATION
	},
	'/': {
		type: NONENDING_PUNCTUATION
	},
	'\'': {
		type: NONENDING_PUNCTUATION
	},
	';': {
		type: NONENDING_PUNCTUATION
	},
	':': {
		type: NONENDING_PUNCTUATION
	},
	'eat': {
		combos: {
			'ed': 'ate'
		}
	},
	'I': {
		combos: {
			'is': 'I\'m'
		}
	}
};
