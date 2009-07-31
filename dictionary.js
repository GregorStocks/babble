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
	're-': {
		type: PREFIX
	},
	'dis-': {
		type: PREFIX
	},
	'in-': {
		type: PREFIX
	},
	'mis-': {
		type: PREFIX
	},
	'non-': {
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
	'-ous': {
		type: SUFFIX
	},
	'-ion': {
		type: SUFFIX
	},
	'-ity': {
		type: SUFFIX
	},
	'-ive': {
		type: SUFFIX
	},
	'-ment': {
		type: SUFFIX
	},
	'-ness': {
		type: SUFFIX
	},
	'-\'d': {
		type: SUFFIX
	},
	'-\'ll': {
		type: SUFFIX
	},
	'-\'s': {
		type: SUFFIX
	},
	'-ance': {
		type: SUFFIX
	},
	'-en': {
		type: SUFFIX
	},
	'-ish': {
		type: SUFFIX
	},
	'-n\'t': {
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
			'is': 'I\'m',
			'are': 'I\'m',
			'am': 'I\'m'
		}
	},
	'person': {
		combos: {
			'-fy': 'personify'
		}
	},
	'succubus': {
		combos: {
			'-s': 'succubi'
		}
	},
	'Africa': {
		combos: {
			'-er': 'African'
		}
	},
	'Europe': {
		combos: {
			'-er': 'European'
		}
	},
	'America': {
		combos: {
			'-er': 'American'
		}
	},
	'Australia': {
		combos: {
			'-er': 'Australian'
		}
	},
	'Asia': {
		combos: {
			'-er': 'Asian'
		}
	},
	'Antarctica': {
		combos: {
			'-er': 'Antarctican'
		}
	},
	'Albania': {
		combos: {
			'-er': 'Albanian'
		}
	},
	'Angola': {
		combos: {
			'-er': 'Angolan'
		}
	},
	'Austria': {
		combos: {
			'-er': 'Austrian'
		}
	},
	'Belgium': {
		combos: {
			'-er': 'Belgian'
		}
	},
	'Canada': {
		combos: {
			'-er': 'Canadian'
		}
	},
	'China': {
		combos: {
			'-er': 'Chinese'
		}
	},
	'Denmark': {
		combos: {
			'-er': 'Danish'
		}
	},
	'Ethiopia': {
		combos: {
			'-er': 'Ethiopian'
		}
	},
	'Finland': {
		combos: {
			'-er': 'Finnish'
		}
	},
	'France': {
		combos: {
			'-er': 'French'
		}
	},
	'Georgia': {
		combos: {
			'-er': 'Georgian'
		}
	},
	'Germany': {
		combos: {
			'-er': 'German'
		}
	},
	'Greece': {
		combos: {
			'-er': 'Greek'
		}
	},
	'Hungary': {
		combos: {
			'-er': 'Hungarian'
		}
	},
	'India': {
		combos: {
			'-er': 'Indian'
		}
	},
	'Iraq': {
		combos: {
			'-er': 'Iraqi'
		}
	},
	'Israel': {
		combos: {
			'-er': 'Israeli'
		}
	},
	'Italy': {
		combos: {
			'-er': 'Italian'
		}
	},
	'Japan': {
		combos: {
			'-er': 'Japanese'
		}
	},
	'Korea': {
		combos: {
			'-er': 'Korean'
		}
	},
	'Laos': {
		combos: {
			'-er': 'Laotian'
		}
	},
	'Mexico': {
		combos: {
			'-er': 'Mexican'
		}
	},
	'Montenegro': {
		combos: {
			'-er': 'Montenegrin'
		}
	},
	'Pakistan': {
		combos: {
			'-er': 'Pakistani'
		}
	},
	'Paraguay': {
		combos: {
			'-er': 'Paraguayan'
		}
	},
	'Poland': {
		combos: {
			'-er': 'Polish'
		}
	},
	'Russia': {
		combos: {
			'-er': 'Russian'
		}
	},
	'Samoa': {
		combos: {
			'-er': 'Samoan'
		}
	},
	'Spain': {
		combos: {
			'-er': 'Spanish'
		}
	},
	'Syria': {
		combos: {
			'-er': 'Syrian'
		}
	},
	'Tunisia': {
		combos: {
			'-er': 'Tunisian'
		}
	},
	'Turkey': {
		combos: {
			'-er': 'Turkish'
		}
	},
	'USA': {
		combos: {
			'-er': 'American'
		}
	},
	'Vatican': {
		combos: {
			'-er': 'Vaticanese'
		}
	},
	'Zimbabwe': {
		combos: {
			'-er': 'Zimbabwean'
		}
	},
	'California': {
		combos: {
			'-er': 'Californian'
		}
	},
	'Utah': {
		combos: {
			'-er': 'Utahn'
		}
	},
	'Texas': {
		combos: {
			'-er': 'Texan'
		}
	},
	'Nebraska': {
		combos: {
			'-er': 'Nebraskan'
		}
	},
	'Illinois': {
		combos: {
			'-er': 'Illinois'
		}
	},
	'Ohio': {
		combos: {
			'-er': 'Ohioan'
		}
	},
	'Virginia': {
		combos: {
			'-er': 'Virginian'
		}
	},
	'Florida': {
		combos: {
			'-er': 'Floridan'
		}
	},
	'Pennsylvania': {
		combos: {
			'-er': 'Pennsylvanian'
		}
	},
	'Massachusetts': {
		combos: {
			'-er': 'Masshole'
		}
	},
	'Maine': {
		combos: {
			'-er': 'Mainer'
		}
	},
	'be': {
		combos: {
			'-ing': 'being'
		}
	}
}
