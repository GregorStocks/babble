function add_suffix(phrase, suffix) {
	// strip the leading dash
	var suff = suffix.replace(/^-(.*)$/, '$1');
	if(suff == 's') { // pluralization, hoo boy
		// don't try to implement a fully correct pluralization algorithm, since our set of words is small
		// (relative to the english language anyways)
		// using a cut-down form of the algorithm described in Conway's "An Algorithmic Approach to English
		// Pluralization"
		// special dictionary-defined cases are handled before we ever get to this function
		if(phrase.match(/ch$/) || phrase.match(/sh$/) || phrase.match(/ss$/)) {
			suff = 'es';
		} else if(phrase.match(/[aeo]lf$/) || phrase.match(/[^d]eaf$/) || phrase.match(/arf$/)) {
			return phrase.replace(/f$/, 'ves');
		} else if(phrase.match(/[nlw]ife$/)) {
			return phrase.replace(/fe$/, 'ves');
		} else if(phrase.match(/y$/) && !(phrase.match(/[aeiou]y$/) || phrase.match(/[A-Z].*y$/))) {
			return phrase.replace(/y$/, 'ies');
		} else if(phrase.match(/o$/) && !phrase.match(/[aeiou]o$/)) {
			suff = 'es';
		}
	} else if(suff == 'y') {
		if(phrase.match(/t$/)) {
			suff = 'ty';
		}
	} else if(suff == 'ly') {
		if(phrase.match(/ic$/)) {
			suff = 'ally';
		}
	}
	return phrase + suff;
}

function add_prefix(phrase, prefix) {
	var pref = prefix.replace(/^(.*)-$/, "$1");
	return pref + phrase;
}

function capitalize(word) {
	return word.substring(0, 1).toUpperCase() + word.substring(1, word.length);
}

function add_to_sentence(sentence, phrase, sentence_start, prefixes) {
	// apply all prefixes
	while(prefixes.length) {
		phrase = add_prefix(phrase, prefixes.pop());
	}
	if(sentence_start) {
		phrase = capitalize(phrase);
	}
	if(sentence.length > 0) {
		sentence += " " + phrase;
	} else {
		sentence = phrase;
	}
	return sentence
}

function updateSentence() {
	$('#sentence').empty();
	var words = []
	$.each($('#dropbox > .wordbox'), function(idx, box) {
		var idstr = $(box).attr('id');
		var i = idstr.match(/\d+/);
		if(i) {
			idnum = parseInt(i);
			words.push(wordlist[idnum]);
		}
	});
	$("#sentence").append(makeSentence(words));
	sendSentence(words);
}

function makeSentence(words) {
	var prefixstack = [];
	var sentence = "";
	var curphrase = "";
	var sentence_start = true;
	var capitalize_next = false;
	for(var wordnum in words) {
		var word = words[wordnum];
		var dict_entry = dictionary[word];
		if(dictionary[curphrase] && dictionary[curphrase]['combos'] && dictionary[curphrase]['combos'][word]) { // a special way to combine
			curphrase = dictionary[curphrase]['combos'][word];
		} else if(dict_entry && dict_entry["type"] == SUFFIX) {
			curphrase = add_suffix(curphrase, word);
		} else if(dict_entry && dict_entry["type"] == NONENDING_PUNCTUATION) {
			curphrase += word;
		} else if(dict_entry && dict_entry["type"] == ENDING_PUNCTUATION) {
			 curphrase += word;
			 capitalize_next = true;
		} else {
			// if there's a phrase ready to add to the sentence, add it (empties prefix stack)
			if(curphrase) {
				sentence = add_to_sentence(sentence, curphrase, sentence_start, prefixstack);
			}

			// start new phrase
			if(dict_entry && dict_entry["type"] == PREFIX) {
				curphrase = ""
				prefixstack.push(word);
			} else {
				curphrase = word;
			}
			if(sentence.length > 0) {
				sentence_start = capitalize_next;
				capitalize_next = false;
			}
		}
	}
	sentence = add_to_sentence(sentence, curphrase, sentence_start, prefixstack);
	return sentence;
}

function sendSentence(words) {
	$.post("api/updatesentence.cgi", {"sesskey": get_sess_key(), "words": words, "roomid": get_room_id()}); 
}

function get_sess_key() {
	return $('#sesskey').val();
}

function get_room_id() {
	return $('#roomid').val();
}

function shouldInsertBefore(target, dropX, dropY, insertee) {
	var targpos = target.position();
	var targwidth = target.width
	if(dropY < target.position()['top']) { // top of target is below drop loc
		if(target.position()['top'] != $('#dropbox > .wordbox:first').position()['top']) { // not in the top row
			return true;
		}
	}
	if(dropX > target.position()['left'] + target.width() / 2) { // center of target is to the left of droploc
		return false;
	}
	if(dropY < target.position()['top'] + insertee.height()) { // bottom of target is below drop loc
		return true;
	}
	if(target.position()['top'] == $('#dropbox > .wordbox:last').position()['top']) { // target is in the bottom row
		return true;
	}
	return false;
}

var cureventid = 0

function eventLoop() {
	// TODO: find out whether TCP guarantees that the events will arrive in order
	// when you do it asynchronously like this
	// also, ensure that only one event is being applied at any given time
	$.getJSON("api/geteventssince.cgi", {"eventid": cureventid, "roomid": get_room_id()}, function(events) {
		var event = events;
		for(var i in events) {
			processEvent(events[i]);
		}
	});
	setTimeout(eventLoop, 1000);
}

$(eventLoop());

function processEvent(ev) {
	eventid = ev["eventid"]
	if(eventid <= cureventid) { // likely with lag of > 1 second
		return;
	}
	cureventid = eventid;
	evtype = ev["type"];
	if(evtype == "new round" && ev["words"]) {
		startRound();
		insertWords(ev["words"]);
	} else if(evtype == "collecting") {
		startCollecting();
	} else if(evtype == "vote" && ev["sentences"]) {
		startVoting(ev["sentences"]);
	} else if(evtype == "voting over") {
		startCollectingVotes();
	} else if(evtype == "winner" && ev["winner"] && ev["votes"] && ev["scores"]) {
		showWinners(ev["winner"], ev["votes"], ev["scores"]);
	} else if(evtype == "game over" && ev["scores"]) {
		showGameWinners(ev["scores"]);
	}
}

function resetUi() {
	$("#gamebox").empty();
	$(".wordbox").remove();
}

function startRound() {
	resetUi();
	$("#gamebox")
		.append("<div class='wordsbox wordscontainer' id='wordsbox'></div>")
		.append("<div class='dropbox wordscontainer' id='dropbox'></div>")
		.append("<p class='sentence' id='sentence'></p>");
	$("#dropbox")
		.append("<div class='prop' id='prop'></div>")
		.append("<div class='clear' id='clear'></div></div>")
		.bind('drop', function(event) {
			// find the farthest-left wordbox with a center to the right of the mouse pointer.
			// insert word before it.
			// note that the wordboxes are already sorted in order for us.
			var done = false;
			$.each($('#dropbox > .wordbox'), function(idx, box) {
				if(!done && shouldInsertBefore($(box), event.pageX, event.pageY, $(event.dragTarget))) {
					done = true;
					$(event.dragTarget).insertBefore(box);
				}
			});
			if(!done) {
				$(event.dragTarget).insertBefore($('#clear'))
			}
			$(event.dragTarget).css({
				position: 'static',
				float: 'left'
			});
			$.dropManage(); // might have resized from adding a fella
			updateSentence();
		});
}

function startCollecting() {
	resetUi();
	$("#gamebox").append("<p>Collecting sentences!</p>");
}

function startVoting(sentences) {
	resetUi();
	$("#gamebox").append("<p>voting!</p");
	$("#gamebox").append("<table class='votetable' id='votetable' border=1></table");
	for(sentenceid in sentences) {
		sentence = makeSentence(sentences[sentenceid]);
		$("#votetable").append("<tr><td>" + sentence + "</td><td><button onclick=\"$.get('api/vote.cgi', {sentenceid: '" + sentenceid + "', sesskey: '" + get_sess_key() + "'}) \">Vote</button></td></tr>");
	}
}

function startCollectingVotes() {
	resetUi();
	$("#gamebox").append("<p>Collecting votes!</p>");
}

function showWinners(winner, votes, scores) {
	// TODO: make this massively more sophisticated
	resetUi();
	$("#gamebox").append("<p>Winner: " + winner + "</p>");
	for(vote in votes) {
		$("#gamebox").append("<p>" + vote + " voted for " + votes[vote] + "!</p>");
	}
	for(score in scores) {
		$("#gamebox").append("<p>" + score + " has " + scores[score] + " points!</p>");
	}
}

function showGameWinners(scores) {
	resetUi();
	$("#gamebox").append("<p>GAME OVER!!!</p>");
	for(person in scores) {
		$("#gamebox").append("<p>" + person + " had " + scores[person] + " points.</p>");
	}
}

var wordlist = []

function insertWords(words) {
	wordlist = words;

	// first, insert all the boxes with static positioning
	for(var i = 0; i < words.length; i++) {
		var box = $('<span id="wordbox' + i + '" class="wordbox">' + capitalize(words[i]) + '</span>');
		$('#wordsbox').append(box);
	}

	// next, save all of their positions
	var box_poses = []
	$.each($('.wordbox'), function(idx, box) {
		box_poses[$(box).attr('id')] = $(box).position(); 
	});

	// then, convert them all to absolute positioning using saved positions
	$.each($('.wordbox'), function(idx, box) {
		$(box).css({
			position: 'absolute',
			top: box_poses[$(box).attr('id')]['top'],
			left: box_poses[$(box).attr('id')]['left'],
			float: 'none'
		});
	});
	$('.wordbox').bind('drag', function(event){
		$(this).css({
			position: 'absolute',
			top: event.offsetY,
			left: event.offsetX
		});
	})
	.bind('dragstart', function(event) {
		$('body').append(this); // remove from the sentence
		$.dropManage(); // drop area might have resized from removing it
		updateSentence();
	});
}
