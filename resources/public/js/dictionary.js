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
      'am': 'I\'m',
      'ed': 'I\'d',
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
  },
  '^': {
    combos: {
      '!': 'I'
    }
  },

  "are": {
    combos: {
      '-ed': "were"
    }
  },
  "am": {
    combos: {
      '-ed': "was"
    }
  },
  "as": {
    combos: {
      '-ed': "assed"
    }
  },
  "awake": {
    combos: {
      '-ed': "awoke"
    }
  },
  "babysit": {
    combos: {
      '-ed': "babysat"
    }
  },
  "bite": {
    combos: {
      '-ed': "bit"
    }
  },
  "bend": {
    combos: {
      '-ed': "bent"
    }
  },
  "bleed": {
    combos: {
      '-ed': "bled"
    }
  },
  "breed": {
    combos: {
      '-ed': "bred"
    }
  },
  "blow": {
    combos: {
      '-ed': "blew"
    }
  },
  "buy": {
    combos: {
      '-ed': "bought"
    }
  },
  "break": {
    combos: {
      '-ed': "broke"
    }
  },
  "begin": {
    combos: {
      '-ed': "began"
    }
  },
  "become": {
    combos: {
      '-ed': "became"
    }
  },
  "bring": {
    combos: {
      '-ed': "brought"
    }
  },
  "build": {
    combos: {
      '-ed': "built"
    }
  },
  "catch": {
    combos: {
      '-ed': "caught"
    }
  },
  "cut": {
    combos: {
      '-ed': "cut"
    }
  },
  "choose": {
    combos: {
      '-ed': "chose"
    }
  },
  "come": {
    combos: {
      '-ed': "came"
    }
  },
  "can't": {
    combos: {
      '-ed': "couldn't"
    }
  },
  "doesn't": {
    combos: {
      '-ed': "didn't"
    }
  },
  "don't": {
    combos: {
      '-ed': "didn't"
    }
  },
  "do": {
    combos: {
      '-ed': "did"
    }
  },
  "deal": {
    combos: {
      '-ed': "dealt"
    }
  },
  "draw": {
    combos: {
      '-ed': "drew"
    }
  },
  "drink": {
    combos: {
      '-ed': "drank"
    }
  },
  "drive": {
    combos: {
      '-ed': "drove"
    }
  },
  "eat": {
    combos: {
      '-ed': "ate"
    }
  },
  "equip": {
    combos: {
      '-ed': "equipped"
    }
  },
  "forget": {
    combos: {
      '-ed': "forgot"
    }
  },
  "forgive": {
    combos: {
      '-ed': "forgave"
    }
  },
  "fly": {
    combos: {
      '-ed': "flew"
    }
  },
  "fall": {
    combos: {
      '-ed': "fell"
    }
  },
  "feel": {
    combos: {
      '-ed': "felt"
    }
  },
  "feed": {
    combos: {
      '-ed': "fed"
    }
  },
  "find": {
    combos: {
      '-ed': "found"
    }
  },
  "fight": {
    combos: {
      '-ed': "fought"
    }
  },
  "fling": {
    combos: {
      '-ed': "flung"
    }
  },
  "freeze": {
    combos: {
      '-ed': "froze"
    }
  },
  "get": {
    combos: {
      '-ed': "got"
    }
  },
  "go": {
    combos: {
      '-ed': "went"
    }
  },
  "give": {
    combos: {
      '-ed': "gave"
    }
  },
  "grow": {
    combos: {
      '-ed': "grew"
    }
  },
  "he": {
    combos: {
      '-ed': "he'd",
      '-er': "her"
    }
  },
  "hear": {
    combos: {
      '-ed': "heard"
    }
  },
  "have": {
    combos: {
      '-ed': "had"
    }
  },
  "has": {
    combos: {
      '-ed': "had"
    }
  },
  "hang": {
    combos: {
      '-ed': "hung"
    }
  },
  "hold": {
    combos: {
      '-ed': "held"
    }
  },
  "hurt": {
    combos: {
      '-ed': "hurt"
    }
  },
  "hide": {
    combos: {
      '-ed': "hid"
    }
  },
  "how": {
    combos: {
      '-ed': "how'd"
    }
  },
  "I'm": {
    combos: {
      '-ed': "i was"
    }
  },
  "is": {
    combos: {
      '-ed': "was"
    }
  },
  "it": {
    combos: {
      '-ed': "it'd"
    }
  },
  "know": {
    combos: {
      '-ed': "knew"
    }
  },
  "keep": {
    combos: {
      '-ed': "kept"
    }
  },
  "lose": {
    combos: {
      '-ed': "lost"
    }
  },
  "leave": {
    combos: {
      '-ed': "left"
    }
  },
  "let": {
    combos: {
      '-ed': "let"
    }
  },
  "lead": {
    combos: {
      '-ed': "led"
    }
  },
  "lay": {
    combos: {
      '-ed': "laid"
    }
  },
  "light": {
    combos: {
      '-ed': "lit"
    }
  },
  "limit": {
    combos: {
      '-ed': "limited"
    }
  },
  "make": {
    combos: {
      '-ed': "made"
    }
  },
  "meet": {
    combos: {
      '-ed': "met"
    }
  },
  "mean": {
    combos: {
      '-ed': "meant"
    }
  },
  "pay": {
    combos: {
      '-ed': "paid"
    }
  },
  "quiver": {
    combos: {
      '-ed': "quivered"
    }
  },
  "quit": {
    combos: {
      '-ed': "quit"
    }
  },
  "quip": {
    combos: {
      '-ed': "quipped"
    }
  },
  "read": {
    combos: {
      '-ed': "read"
    }
  },
  "ride": {
    combos: {
      '-ed': "rode"
    }
  },
  "run": {
    combos: {
      '-ed': "ran"
    }
  },
  "ring": {
    combos: {
      '-ed': "rang"
    }
  },
  "rise": {
    combos: {
      '-ed': "rose"
    }
  },
  "refer": {
    combos: {
      '-ed': "referred"
    }
  },
  "she": {
    combos: {
      '-ed': "she'd"
    }
  },
  "sit": {
    combos: {
      '-ed': "sat"
    }
  },
  "set": {
    combos: {
      '-ed': "set"
    }
  },
  "steal": {
    combos: {
      '-ed': "stole"
    }
  },
  "stink": {
    combos: {
      '-ed': "stunk"
    }
  },
  "say": {
    combos: {
      '-ed': "said"
    }
  },
  "spin": {
    combos: {
      '-ed': "spun"
    }
  },
  "sell": {
    combos: {
      '-ed': "sold"
    }
  },
  "shake": {
    combos: {
      '-ed': "shook"
    }
  },
  "sling": {
    combos: {
      '-ed': "slung"
    }
  },
  "sting": {
    combos: {
      '-ed': "stung"
    }
  },
  "sleep": {
    combos: {
      '-ed': "slept"
    }
  },
  "sweep": {
    combos: {
      '-ed': "swept"
    }
  },
  "strike": {
    combos: {
      '-ed': "struck"
    }
  },
  "see": {
    combos: {
      '-ed': "saw"
    }
  },
  "seek": {
    combos: {
      '-ed': "sought"
    }
  },
  "stand": {
    combos: {
      '-ed': "stood"
    }
  },
  "sing": {
    combos: {
      '-ed': "sang"
    }
  },
  "spring": {
    combos: {
      '-ed': "sprung"
    }
  },
  "spirit": {
    combos: {
      '-ed': "spirited"
    }
  },
  "slide": {
    combos: {
      '-ed': "slid"
    }
  },
  "spend": {
    combos: {
      '-ed': "spent"
    }
  },
  "send": {
    combos: {
      '-ed': "sent"
    }
  },
  "shoot": {
    combos: {
      '-ed': "shot"
    }
  },
  "stick": {
    combos: {
      '-ed': "stuck"
    }
  },
  "sink": {
    combos: {
      '-ed': "sank"
    }
  },
  "swim": {
    combos: {
      '-ed': "swam"
    }
  },
  "spit": {
    combos: {
      '-ed': "spat"
    }
  },
  "speak": {
    combos: {
      '-ed': "spoke"
    }
  },
  "swing": {
    combos: {
      '-ed': "swung"
    }
  },
  "shrink": {
    combos: {
      '-ed': "shrunk"
    }
  },
  "throw": {
    combos: {
      '-ed': "threw"
    }
  },
  "think": {
    combos: {
      '-ed': "thought"
    }
  },
  "they": {
    combos: {
      '-ed': "they'd"
    }
  },
  "take": {
    combos: {
      '-ed': "took"
    }
  },
  "tear": {
    combos: {
      '-ed': "tore"
    }
  },
  "tell": {
    combos: {
      '-ed': "told"
    }
  },
  "teach": {
    combos: {
      '-ed': "taught"
    }
  },
  "understand": {
    combos: {
      '-ed': "understood"
    }
  },
  "we": {
    combos: {
      '-ed': "we'd",
      'is': "we're"
    }
  },
  "wear": {
    combos: {
      '-ed': "wore"
    }
  },
  "weave": {
    combos: {
      '-ed': "wove"
    }
  },
  "write": {
    combos: {
      '-ed': "wrote"
    }
  },
  "weep": {
    combos: {
      '-ed': "wept"
    }
  },
  "who": {
    combos: {
      '-ed': "who'd",
      '-s': "whose"
    }
  },
  "win": {
    combos: {
      '-ed': "won"
    }
  },
  "withhold": {
    combos: {
      '-ed': "withheld"
    }
  },
  "withdraw": {
    combos: {
      '-ed': "withdrew"
    }
  },
  "wake": {
    combos: {
      '-ed': "woke"
    }
  },
  "won't": {
    combos: {
      '-ed': "wouldn't"
    }
  },
  "you": {
    combos: {
      '-ed': "you'd"
    }
  },
  "strip": {
    combos: {
      '-er': "stripper"
    }
  }
}
